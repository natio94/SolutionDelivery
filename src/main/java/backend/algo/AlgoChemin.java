package backend.algo;

import backend.models.*;
import backend.algo.Dijkstra;
import backend.Service;

import java.util.*;

public class AlgoChemin {
	public static double getCheminTemps(Chemin chemin) {
		Service service = Service.getInstance();
		Graphe graph = service.getGraphe();
		Map<Quai, DistanceAntecedants> distanceMap = Dijkstra.getDistanceAntecedantsMap(graph, chemin.cheminQuai().get(0));

		double poid = 0.0;
		int i = 0;
		Quai currentQuai = graph.getQuai(chemin.cheminQuai().get(i).getId());
		Quai lastQuai = graph.getQuai(chemin.cheminQuai().get(chemin.cheminQuai().size() - 1).getId());
		Arete aretePoid = null;
		while (!currentQuai.equals(lastQuai)) {
			for (var arete : currentQuai.getVoisins()) {
				if (arete.getDestination().equals(chemin.cheminQuai().get(i+1))) {
					aretePoid = arete;
					break;
				}
			}
			poid = poid + aretePoid.getPoid();
			i = i + 1;
			currentQuai = aretePoid.getDestination();
		}

		return poid;
	}
	public static double getCheminCO2(Chemin chemin) {
		Service service = Service.getInstance();
		Graphe graph = service.getGrapheCO2();
		Map<Quai, DistanceAntecedants> distanceMap = Dijkstra.getDistanceAntecedantsMap(graph, chemin.cheminQuai().get(0));

		double poid = 0.0;
		int i = 0;
		Quai currentQuai = graph.getQuai(chemin.cheminQuai().get(i).getId());
		Quai lastQuai = graph.getQuai(chemin.cheminQuai().get(chemin.cheminQuai().size() - 1).getId());
		Arete aretePoid = null;
		while (!currentQuai.equals(lastQuai)) {
			for (var arete : currentQuai.getVoisins()) {
				if (arete.getDestination().equals(chemin.cheminQuai().get(i+1))) {
					aretePoid = arete;
					break;
				}
			}
			poid = poid + aretePoid.getPoid();
			i = i + 1;
			currentQuai = aretePoid.getDestination();
		}

		return poid;
	}
	public static double getCheminNbCorrespondances(Chemin chemin) {
		Service service = Service.getInstance();
		Graphe graph = service.getGrapheCorrespondances();
		Map<Quai, DistanceAntecedants> distanceMap = Dijkstra.getDistanceAntecedantsMap(graph, chemin.cheminQuai().get(0));

		double poid = 0.0;
		int i = 0;
		Quai currentQuai = graph.getQuai(chemin.cheminQuai().get(i).getId());
		Quai lastQuai = graph.getQuai(chemin.cheminQuai().get(chemin.cheminQuai().size() - 1).getId());
		Arete aretePoid = null;
		while (!currentQuai.equals(lastQuai)) {
			for (var arete : currentQuai.getVoisins()) {
				if (arete.getDestination().equals(chemin.cheminQuai().get(i+1))) {
					aretePoid = arete;
					break;
				}
			}
			poid = poid + aretePoid.getPoid();
			i = i + 1;
			currentQuai = aretePoid.getDestination();
		}

		return poid;
	}
}
