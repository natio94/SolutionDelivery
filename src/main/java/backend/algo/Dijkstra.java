package backend.algo;

import backend.models.Graphe;
import backend.models.Arete;
import backend.models.Quai;
import backend.models.DistanceAntecedants;

import java.util.*;

public class Dijkstra {
	public static Map<Quai, DistanceAntecedants> getDistanceAntecedantsMap(Graphe graph, Quai origin) {
		// initialization
		Map<Quai, DistanceAntecedants> distanceAntecedantsMap = new HashMap<>();
		Quai antecedant = null;
		distanceAntecedantsMap.put(origin, new DistanceAntecedants(0, antecedant));
		Comparator<Quai> compareByDistance = (Quai quai1, Quai quai2) -> {
			if (distanceAntecedantsMap.get(quai1) == null && distanceAntecedantsMap.get(quai2) == null) {
				return 0;
			} else if (distanceAntecedantsMap.get(quai1) == null) {
				return 1;
			} else if (distanceAntecedantsMap.get(quai2) == null) {
				return -1;
			} else {
				return distanceAntecedantsMap.get(quai1).getDistance().compareTo(distanceAntecedantsMap.get(quai2).getDistance());
			}
	 	};
		TreeSet<Quai> unvisited = new TreeSet<>(compareByDistance);
		for (var quai : graph.getQuais()) {
			unvisited.add(quai);
		}

		// iteration
		while (!unvisited.isEmpty()) {
			Quai currentQuai = unvisited.first();
			for (var arete : currentQuai.getVoisins()) {
				Quai voisin = arete.getDestination();
				if (distanceAntecedantsMap.get(voisin) == null) {
					Integer distance = distanceAntecedantsMap.get(currentQuai).getDistance() + arete.getPoid();
					antecedant = currentQuai;
					distanceAntecedantsMap.put(voisin, new DistanceAntecedants(distance, antecedant));
					// update the ordering of the unvisited set
					unvisited.remove(voisin);
					unvisited.add(voisin);
				} else if (
					distanceAntecedantsMap.get(currentQuai).getDistance() + arete.getPoid() < distanceAntecedantsMap.get(voisin).getDistance()
					) {
					Integer distance = distanceAntecedantsMap.get(currentQuai).getDistance() + arete.getPoid();
					antecedant = currentQuai;
					distanceAntecedantsMap.get(voisin).setDistance(distance);
					distanceAntecedantsMap.get(voisin).setAntecedants(antecedant);
					// update the ordering of the unvisited set
					unvisited.remove(voisin);
					unvisited.add(voisin);
				} else if (
					distanceAntecedantsMap.get(currentQuai).getDistance() + arete.getPoid() == distanceAntecedantsMap.get(voisin).getDistance()
					  ) {
					antecedant = currentQuai;
					distanceAntecedantsMap.get(voisin).getAntecedants().add(antecedant);
				}
			}

			unvisited.remove(currentQuai);
		}

		return distanceAntecedantsMap;
	}
}
