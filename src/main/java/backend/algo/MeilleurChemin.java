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

		Quai previous_quai, next_quai;
		// eliminate the walks between Quai in the starting station
		if (cheminQuai.size() > 1) {
			current_quai = cheminQuai.get(0);
			next_quai = cheminQuai.get(1);
			while (cheminQuai.size() > 1 && current_quai.getStation().equals(next_quai.getStation())) {
				cheminQuai.remove(current_quai);
				poid = poid - cheminArete.get(0).getPoid();
				cheminArete.remove(0);
				current_quai = cheminQuai.get(0);
				if (cheminQuai.size() > 1) {
					next_quai = cheminQuai.get(1);
				}
			}
		}
		// eliminate the walks between Quai in the ending station
		if (cheminQuai.size() > 1) {
			current_quai = cheminQuai.get(cheminQuai.size()-1);
			previous_quai = cheminQuai.get(cheminQuai.size()-2);
			while (cheminQuai.size() > 1 && current_quai.getStation().equals(previous_quai.getStation())) {
				cheminQuai.remove(current_quai);
				poid = poid - cheminArete.get(cheminQuai.size() - 1).getPoid();
				cheminArete.remove(cheminQuai.size() - 1);
				current_quai = cheminQuai.get(cheminQuai.size()-1);
				if (cheminQuai.size() > 1) {
					previous_quai = cheminQuai.get(cheminQuai.size()-2);
				}
			}
		}

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

		Chemin resultCheminCorrespondances = MeilleurChemin(distanceMapMin, originQuaiMin, destinationQuaiMin);

		// change the aretes so that their weight is the time, not 0 or 1 meaning there is a transfer / no transfer
		List<Arete> cheminArete = new ArrayList<>();
		Graphe graphTemps = service.getGraphe();
		Map<Quai, DistanceAntecedants> distanceMapTemps = Dijkstra.getDistanceAntecedantsMap(graph, resultCheminCorrespondances.cheminQuai().get(0));
		int i = 0;
		Quai currentQuai = graphTemps.getQuai(resultCheminCorrespondances.cheminQuai().get(i).getId());
		Quai lastQuai = graphTemps.getQuai(resultCheminCorrespondances.cheminQuai().get(resultCheminCorrespondances.cheminQuai().size() - 1).getId());
		Arete aretePoid = null;
		while (!currentQuai.equals(lastQuai)) {
			for (var arete : currentQuai.getVoisins()) {
				if (arete.getDestination().equals(resultCheminCorrespondances.cheminQuai().get(i+1))) {
					aretePoid = arete;
					break;
				}
			}
			cheminArete.add(aretePoid);
			i = i + 1;
			currentQuai = aretePoid.getDestination();
		}

		return new Chemin(resultCheminCorrespondances.cheminQuai(), cheminArete, resultCheminCorrespondances.poid());
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

		Chemin resultCheminCorrespondances = MeilleurChemin(distanceMapMin, originQuaiMin, destinationQuaiMin);

		// change the aretes so that their weight is the time, not the CO2
		List<Arete> cheminArete = new ArrayList<>();
		Graphe graphTemps = service.getGraphe();
		Map<Quai, DistanceAntecedants> distanceMapTemps = Dijkstra.getDistanceAntecedantsMap(graph, resultCheminCorrespondances.cheminQuai().get(0));
		int i = 0;
		Quai currentQuai = graphTemps.getQuai(resultCheminCorrespondances.cheminQuai().get(i).getId());
		Quai lastQuai = graphTemps.getQuai(resultCheminCorrespondances.cheminQuai().get(resultCheminCorrespondances.cheminQuai().size() - 1).getId());
		Arete aretePoid = null;
		while (!currentQuai.equals(lastQuai)) {
			for (var arete : currentQuai.getVoisins()) {
				if (arete.getDestination().equals(resultCheminCorrespondances.cheminQuai().get(i+1))) {
					aretePoid = arete;
					break;
				}
			}
			cheminArete.add(aretePoid);
			i = i + 1;
			currentQuai = aretePoid.getDestination();
		}

		return new Chemin(resultCheminCorrespondances.cheminQuai(), cheminArete, resultCheminCorrespondances.poid());
	}
}
