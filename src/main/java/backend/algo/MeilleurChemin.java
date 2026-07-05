package backend.algo;

import backend.models.*;
import backend.algo.Dijkstra;
import backend.Service;

import java.util.*;

public class MeilleurChemin {
	public static Chemin MeilleurChemin(Map<Quai, DistanceAntecedants> distanceAntecedantsMap, Quai origin, Quai destination) {
		List<Quai> cheminQuai = new ArrayList<>();
		List<Arete> cheminArete = new ArrayList<>();
		double poid = distanceAntecedantsMap.get(destination).getDistance();

		Quai current_quai = destination;
		while (!current_quai.equals(origin)) {
			cheminQuai.add(current_quai);
			cheminArete.add(distanceAntecedantsMap.get(current_quai).getAntecedantsArete().get(0));
			current_quai = distanceAntecedantsMap.get(current_quai).getAntecedantsQuai().get(0);
		}
		cheminQuai.add(current_quai);
		Collections.reverse(cheminQuai);
		Collections.reverse(cheminArete);

		return new Chemin(cheminQuai, cheminArete, poid);
	}

	public static Chemin MeilleurCheminTemps(Station origin, Station destination) {
		Service service = Service.getInstance();
		Graphe graph = service.getGraphe();

		Map<Quai, DistanceAntecedants> distanceMapMin = Dijkstra.getDistanceAntecedantsMap(graph, origin.getQuais().get(0));
		double minDistance = distanceMapMin.get(destination.getQuais().get(0)).getDistance();
		Quai originQuaiMin = origin.getQuais().get(0);
		Quai destinationQuaiMin = destination.getQuais().get(0);

		for (var originQuai : origin.getQuais()) {
			Map<Quai, DistanceAntecedants> distanceMap = Dijkstra.getDistanceAntecedantsMap(graph, originQuai);
			for (var destinationQuai : destination.getQuais()) {
				if (distanceMap.get(destinationQuai).getDistance() < minDistance) {
					distanceMapMin = distanceMap;
					minDistance = distanceMap.get(destinationQuai).getDistance();
					originQuaiMin = originQuai;
					destinationQuaiMin = destinationQuai;
				}
			}
		}

		return MeilleurChemin(distanceMapMin, originQuaiMin, destinationQuaiMin);
	}

	public static Chemin MeilleurCheminCorrespondances(Station origin, Station destination) {
		Service service = Service.getInstance();
		Graphe graph = service.getGrapheCorrespondances();

		Map<Quai, DistanceAntecedants> distanceMapMin = Dijkstra.getDistanceAntecedantsMap(graph, origin.getQuais().get(0));
		double minDistance = distanceMapMin.get(destination.getQuais().get(0)).getDistance();
		Quai originQuaiMin = origin.getQuais().get(0);
		Quai destinationQuaiMin = destination.getQuais().get(0);

		for (var originQuai : origin.getQuais()) {
			Map<Quai, DistanceAntecedants> distanceMap = Dijkstra.getDistanceAntecedantsMap(graph, originQuai);
			for (var destinationQuai : destination.getQuais()) {
				if (distanceMap.get(destinationQuai).getDistance() < minDistance) {
					distanceMapMin = distanceMap;
					minDistance = distanceMap.get(destinationQuai).getDistance();
					originQuaiMin = originQuai;
					destinationQuaiMin = destinationQuai;
				}
			}
		}

		return MeilleurChemin(distanceMapMin, originQuaiMin, destinationQuaiMin);
	}

	public static Chemin MeilleurCheminCO2(Station origin, Station destination) {
		Service service = Service.getInstance();
		Graphe graph = service.getGrapheCO2();

		Map<Quai, DistanceAntecedants> distanceMapMin = Dijkstra.getDistanceAntecedantsMap(graph, origin.getQuais().get(0));
		double minDistance = distanceMapMin.get(destination.getQuais().get(0)).getDistance();
		Quai originQuaiMin = origin.getQuais().get(0);
		Quai destinationQuaiMin = destination.getQuais().get(0);

		for (var originQuai : origin.getQuais()) {
			Map<Quai, DistanceAntecedants> distanceMap = Dijkstra.getDistanceAntecedantsMap(graph, originQuai);
			for (var destinationQuai : destination.getQuais()) {
				if (distanceMap.get(destinationQuai).getDistance() < minDistance) {
					distanceMapMin = distanceMap;
					minDistance = distanceMap.get(destinationQuai).getDistance();
					originQuaiMin = originQuai;
					destinationQuaiMin = destinationQuai;
				}
			}
		}

		return MeilleurChemin(distanceMapMin, originQuaiMin, destinationQuaiMin);
	}
}
