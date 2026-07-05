package backend;

import backend.algo.Dijkstra;
import backend.algo.MeilleurChemin;
import backend.algo.AlgoChemin;
import backend.models.*;

import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgoCheminTest {

	@Test
	void checkAlgoCheminTemps() {
		Service service = Service.getInstance();
		System.out.println("checkAlgoCheminCorrespondances");
		Station republique = service.getGraphe().getStationParNom("République");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");
		Chemin pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminCO2(republique, villejuif);
		System.out.println("Temps du trajet " + AlgoChemin.getCheminTemps(pathRepubliqueVillejuif));
	}

	@Test
	void checkAlgoCheminCorrespondances() {
		Service service = Service.getInstance();
		System.out.println("checkAlgoCheminCorrespondances");
		Station republique = service.getGraphe().getStationParNom("République");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");
		Chemin pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminTemps(republique, villejuif);
		System.out.println("Nombre de correspondances du trajet " + AlgoChemin.getCheminNbCorrespondances(pathRepubliqueVillejuif));
	}

	@Test
	void checkAlgoCheminCO2() {
		Service service = Service.getInstance();
		System.out.println("checkAlgoCheminCO2");
		Station republique = service.getGraphe().getStationParNom("République");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");
		Chemin pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminTemps(republique, villejuif);
		System.out.println("Equivalent CO2 du trajet " + AlgoChemin.getCheminCO2(pathRepubliqueVillejuif));
	}
}
