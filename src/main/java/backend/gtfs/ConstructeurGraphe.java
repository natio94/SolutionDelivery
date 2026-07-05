package backend.gtfs;

import backend.models.*;

import java.nio.file.Path;
import java.util.*;

public class ConstructeurGraphe {
    public static final ChargeurGTFS charg = new ChargeurGTFS();

    public Graphe buildGraph() {
        Graphe g = new Graphe();

        // 1. Lignes
        Map<String, RouteGTFS> routes = charg.lireRoutes(this.getClass().getResourceAsStream("/datas/routes.txt"));
        for (RouteGTFS r : routes.values()) {
            g.addLigne(new Ligne(r.getId(), r.shortName(), r.color()));
        }

        // 2. Stops (tous pour lookup) + stop_times métro pour filtrer les stations
        Map<String, StopGTFS> stops = charg.lireStops(this.getClass().getResourceAsStream("/datas/stops.txt"));

        // 2 suite. Stop times groupés par tripId, triés par stop_sequence
        Map<String, List<StopTimeGTFS>> stopTimesParTrip =
                charg.lireStopsTime(this.getClass().getResourceAsStream("/datas/stop_times_metro.txt"));

        // Collecter les IDs de stations parentes réellement utilisées par le métro
        Set<String> metroParentIds = new HashSet<>();
        for (List<StopTimeGTFS> arrets : stopTimesParTrip.values()) {
            for (StopTimeGTFS st : arrets) {
                StopGTFS stop = stops.get(st.stopId());
                if (stop == null) continue;
                String parentId;
                if (stop.parentStation().isEmpty()) {
                    parentId = stop.stopId();
                } else {
                    parentId = stop.parentStation();
                }

                metroParentIds.add(parentId);
            }
        }

        // Stations filtrées : uniquement les hubs physiques du métro
        for (StopGTFS s : stops.values()) {
            if (metroParentIds.contains(s.getId())) {
                g.addStation(new Station(s.getId(), s.name(), s.lon(), s.lat()));
            }
        }

        // 3. Un trip représentatif par (routeId, directionId, terminus) pour couvrir toutes les branches
        Map<String, TripGTFS> trips = charg.lireTrips(this.getClass().getResourceAsStream("/datas/trips.txt"), routes.keySet());
        Map<String, TripGTFS> unTripParRouteDir = new HashMap<>();
        for (TripGTFS t : trips.values()) {
            List<StopTimeGTFS> arrets = stopTimesParTrip.get(t.tripId());
            if (arrets == null || arrets.isEmpty()) continue;
            String terminus = arrets.get(arrets.size()-1).stopId();
            String cle = t.routeId() + "_" + t.directionId() + "_" + terminus;
            TripGTFS actuel = unTripParRouteDir.get(cle);
            if (actuel == null) {
                unTripParRouteDir.put(cle, t);
            } else {
                int nbNouv = arrets.size();
                int nbActuel = stopTimesParTrip.getOrDefault(actuel.tripId(), List.of()).size();
                if (nbNouv > nbActuel) unTripParRouteDir.put(cle, t);
            }
        }


        // 5. Quais + Aretes metro
        for (TripGTFS trip : unTripParRouteDir.values()) {
            Ligne ligne = g.getLigne(trip.routeId());
            List<StopTimeGTFS> arrets = stopTimesParTrip.get(trip.tripId());
            if (arrets == null) continue;

            Quai quaiPrecedent = null;
            int tempsPrecedent = 0;

            for (StopTimeGTFS st : arrets) {
                StopGTFS stop = stops.get(st.stopId());
                if (stop == null) continue;

                // Trouver la station parente
                String parentId = stop.parentStation().isEmpty() ? stop.stopId() : stop.parentStation();
                Station station = g.getStation(parentId);
                if (station == null) continue;

                // Créer le Quai s'il n'existe pas encore
                Quai quai = g.getQuai(st.stopId());
                if (quai == null) {
                    quai = new Quai(st.stopId(), ligne, station, stop.lon(), stop.lat());
                    g.addQuai(quai);
                    station.addQuai(quai);
                }

                // Créer l'arête metro vers ce Quai depuis le précédent
                if (quaiPrecedent != null) {
                    int duree = parseTemps(st.time()) - tempsPrecedent;
                    Arete a = new Arete(quaiPrecedent, quai, duree, ligne, Arete.Type.metro);
                    quaiPrecedent.addVoisin(a);
                    g.addArete(a);
                }

                quaiPrecedent = quai;
                tempsPrecedent = parseTemps(st.time());
            }
        }

        // 6. Aretes de correspondance (a pied entre lignes)
        List<TransferGTFS> transfers = charg.lireTransfers(this.getClass().getResourceAsStream("/datas/transfers.txt"));
        for (TransferGTFS t : transfers) {
            Quai quaiFrom = g.getQuai(t.fromStopId());
            Quai quaiTo   = g.getQuai(t.toStopId());
            if (quaiFrom == null || quaiTo == null) continue;
            Arete a = new Arete(quaiFrom, quaiTo, t.minTransferTime(), null, Arete.Type.pied);
            quaiFrom.addVoisin(a);
            g.addArete(a);
        }

        return g;
    }

    public Graphe buildGraphCorrespondances() {
        Graphe g = buildGraph();

	for (var arete : g.getAretes()) {
		if ( arete.getSource().getLigne().equals(arete.getDestination().getLigne()) ) {
			arete.setPoid(0);
		} else {
			arete.setPoid(1);
		}
	}

	return g;
    }

    public Graphe buildGraphCO2() {
        Graphe g = buildGraph();

	for (var arete : g.getAretes()) {
		if (arete.getType() == Arete.Type.metro) {
			double lat1 = arete.getSource().getLatitude();
			double lon1 = arete.getSource().getLongitude();
			double lat2 = arete.getDestination().getLatitude();
			double lon2 = arete.getDestination().getLongitude();
			double distance = distance(lat1, lat2, lon1, lon2);
			double magicCO2 = 3.8; // CO2 emissions per traveler per km on the RATP metro service, in grams. retrieved from https://data.iledefrance-mobilites.fr/explore/dataset/emission-de-co2e-par-voyageur-kilometre-sur-le-reseau/information/
			arete.setPoid(magicCO2 * distance);
		} else {
			arete.setPoid(0.0);
		}
	}

	return g;
    }


    // Source - https://stackoverflow.com/a/16794680
    // Posted by David George, modified by community. See post 'Timeline' for change history
    // Retrieved 2026-07-02, License - CC BY-SA 4.0
    /**
     * Calculate distance between two points in latitude and longitude
     * Uses Haversine method as its base.
     * 
     * lat1, lon1 Start point lat2, lon2 End point 
     * @returns Distance in Kilometers
     */
    public static double distance(double lat1, double lat2, double lon1, double lon2) {

	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
		    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
		    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c;

	    distance = Math.pow(distance, 2);

	    return Math.sqrt(distance);
    }

    private int parseTemps(String hms) {
        String[] p = hms.split(":");
        return Integer.parseInt(p[0]) * 3600 + Integer.parseInt(p[1]) * 60 + Integer.parseInt(p[2]);
    }
}
