package backend.algo;

import backend.models.*;
import backend.algo.Dijkstra;
import backend.Service;

import java.util.*;

public class MeilleurChemin {
	public static List<Quai> MeilleurChemin(Map<Quai, DistanceAntecedants> distanceAntecedantsMap, Quai origin, Quai destination) {
		List<Quai> path = new ArrayList<>();
		Quai current_quai = destination;
		while (!current_quai.equals(origin)) {
			path.add(current_quai);
			current_quai = distanceAntecedantsMap.get(current_quai).getAntecedants().get(0);
		}
		path.add(current_quai);
		Collections.reverse(path);
		return path;
	}

	public static List<Quai> MeilleurChemin(Map<Quai, DistanceAntecedants> distanceAntecedantsMap, Station origin, Station destination) {
		List<Quai> path = MeilleurChemin(distanceAntecedantsMap, origin.getQuais().get(0), destination.getQuais().get(0));

		Quai previous_quai, current_quai, next_quai;
		// eliminate the walks between Quai in the starting station
		if (path.size() > 1) {
			current_quai = path.get(0);
			next_quai = path.get(1);
			while (path.size() > 1 && current_quai.getStation().equals(next_quai.getStation())) {
				path.remove(current_quai);
				current_quai = path.get(0);
				if (path.size() > 1) {
					next_quai = path.get(1);
				}
			}
		}
		// eliminate the walks between Quai in the ending station
		if (path.size() > 1) {
			current_quai = path.get(path.size()-1);
			previous_quai = path.get(path.size()-2);
			while (path.size() > 1 && current_quai.getStation().equals(previous_quai.getStation())) {
				path.remove(current_quai);
				current_quai = path.get(path.size()-1);
				if (path.size() > 1) {
					previous_quai = path.get(path.size()-2);
				}
			}
		}

		return path;
	}

	public static List<Quai> MeilleurCheminTemps(Station origin, Station destination) {
		Service service = Service.getInstance();
		Graphe graph = service.getGraphe();

		Map<Quai, DistanceAntecedants> distanceMapMin = Dijkstra.getDistanceAntecedantsMap(graph, origin.getQuais().get(0));
		int minDistance = distanceMapMin.get(destination.getQuais().get(0)).getDistance();
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

	public static List<Quai> MeilleurCheminCorrespondances(Station origin, Station destination) {
		Service service = Service.getInstance();
		Graphe graph = service.getGrapheCorrespondances();

		Map<Quai, DistanceAntecedants> distanceMapMin = Dijkstra.getDistanceAntecedantsMap(graph, origin.getQuais().get(0));
		int minDistance = distanceMapMin.get(destination.getQuais().get(0)).getDistance();
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

	public static List<Quai> MeilleurCheminCO2(Station origin, Station destination) {
		Service service = Service.getInstance();
		Graphe graph = service.getGrapheCO2();

		Map<Quai, DistanceAntecedants> distanceMapMin = Dijkstra.getDistanceAntecedantsMap(graph, origin.getQuais().get(0));
		int minDistance = distanceMapMin.get(destination.getQuais().get(0)).getDistance();
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
