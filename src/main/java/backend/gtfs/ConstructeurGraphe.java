package backend.gtfs;

import backend.models.*;

import java.nio.file.Path;
import java.util.*;

public class ConstructeurGraphe {
    public static final ChargeurGTFS charg = new ChargeurGTFS();

    public Graphe buildGraph() {
        Graphe g = new Graphe();

        // 1. Lignes
        Map<String, RouteGTFS> routes = charg.lireRoutes(Path.of("Datas/routes.txt"));
        for (RouteGTFS r : routes.values()) {
            g.addLigne(new Ligne(r.getId(), r.shortName(), r.color()));
        }

        // 2. Stations (stops de type 1 = hub physique, ou stops sans parent)
        Map<String, StopGTFS> stops = charg.lireStops(Path.of("Datas/stops.txt"));
        for (StopGTFS s : stops.values()) {
            if (s.locationType() == 1 || (s.locationType() == 0 && s.parentStation().isEmpty())) {
                g.addStation(new Station(s.getId(), s.name(), s.lon(), s.lat()));
            }
        }

        // 3. Un seul trip représentatif par (routeId, directionId)
        Map<String, TripGTFS> trips = charg.lireTrips(Path.of("Datas/trips.txt"), routes.keySet());
        Map<String, TripGTFS> unTripParRouteDir = new HashMap<>();
        for (TripGTFS t : trips.values()) {
            String cle = t.routeId() + "_" + t.directionId();
            unTripParRouteDir.putIfAbsent(cle, t);
        }

        // 4. Stop times groupés par tripId, triés par stop_sequence
        Map<String, List<StopTimeGTFS>> stopTimesParTrip =
                charg.lireStopsTime(Path.of("Datas/stop_times_metro.txt"));

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
                    quai = new Quai(st.stopId(), ligne, station);
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
        List<TransferGTFS> transfers = charg.lireTransfers(Path.of("Datas/transfers.txt"));
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

    private int parseTemps(String hms) {
        String[] p = hms.split(":");
        return Integer.parseInt(p[0]) * 3600 + Integer.parseInt(p[1]) * 60 + Integer.parseInt(p[2]);
    }
}