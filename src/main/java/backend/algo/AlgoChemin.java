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
		List<Arete> aretes = chemin.cheminArete();
		double poid = 0.0;
		int i = 0;
		while (i < aretes.size()) {
			if (aretes.get(i).getType() != Arete.Type.pied) {
				i++;
				continue;
			}

			Quai quaiDebut = aretes.get(i).getSource();
			Station stationDebut = quaiDebut.getStation();
			double dureeTotal = 0;
			int j = i;
			while (j < aretes.size()
					&& aretes.get(j).getType() == Arete.Type.pied
					&& aretes.get(j).getDestination().getStation().getId().equals(stationDebut.getId())) {
				dureeTotal += aretes.get(j).getPoid();
				j++;
			}

			if (j > i) {
				Quai quaiFin = aretes.get(j - 1).getDestination();
				boolean changementLigne = !quaiDebut.getLigne().equals(quaiFin.getLigne());
				if (changementLigne || dureeTotal > 0) {
					poid += 1.0;
				}
				i = j;
			} else {
				i++; // marche qui quitte directement la station : pas une correspondance
			}
		}
		return poid;
	}
}
