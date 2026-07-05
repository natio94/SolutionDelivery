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
		System.out.println("République - Villejuif - Louis Aragon");
		Station republique = service.getGraphe().getStationParNom("République");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");
		Chemin pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminCO2(republique, villejuif);
		System.out.println("Temps du trajet " + AlgoChemin.getCheminTemps(pathRepubliqueVillejuif));
	}

	@Test
	void checkAlgoCheminCorrespondances() {
		Service service = Service.getInstance();
		System.out.println("checkAlgoCheminCorrespondances");
		System.out.println("République - Villejuif - Louis Aragon");
		Station republique = service.getGraphe().getStationParNom("République");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");
		Chemin pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminTemps(republique, villejuif);
		System.out.println("Nombre de correspondances du trajet " + AlgoChemin.getCheminNbCorrespondances(pathRepubliqueVillejuif));
		assertEquals(AlgoChemin.getCheminNbCorrespondances(pathRepubliqueVillejuif), 1.0);

		System.out.println("Créteil - L'Échat - Villejuif - Louis Aragon");
		Station creteil = service.getGraphe().getStationParNom("Créteil - L'Échat");
		Chemin pathCreteilVillejuif = MeilleurChemin.MeilleurCheminTemps(creteil, villejuif);
		System.out.println("Nombre de correspondances du trajet " + AlgoChemin.getCheminNbCorrespondances(pathCreteilVillejuif));
		assertEquals(AlgoChemin.getCheminNbCorrespondances(pathCreteilVillejuif), 2.0);

		System.out.println("République - Bastille");
		Station bastille = service.getGraphe().getStationParNom("Bastille");
		Chemin pathRepubliqueBastille = MeilleurChemin.MeilleurCheminTemps(republique, bastille);
		System.out.println("Nombre de correspondances du trajet " + AlgoChemin.getCheminNbCorrespondances(pathRepubliqueBastille));
		assertEquals(AlgoChemin.getCheminNbCorrespondances(pathRepubliqueBastille), 0.0);
	}

	@Test
	void checkAlgoCheminCO2() {
		Service service = Service.getInstance();
		System.out.println("checkAlgoCheminCO2");
		System.out.println("République - Villejuif - Louis Aragon");
		Station republique = service.getGraphe().getStationParNom("République");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");
		Chemin pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminTemps(republique, villejuif);
		System.out.println("Equivalent CO2 du trajet " + AlgoChemin.getCheminCO2(pathRepubliqueVillejuif));
	}
}
