package backend;

import backend.algo.Dijkstra;
import backend.algo.MeilleurChemin;
import backend.models.*;

import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeilleurCheminTest {

	@Test
	void checkMeilleurCheminQuai() {
		System.out.println("MeilleurChemin Quai test");
		Service service = Service.getInstance();
		Quai republique = service.getGraphe().getStationParNom("République").getQuais().get(0);
		Quai bastille = service.getGraphe().getStationParNom("Bastille").getQuais().get(0);
		Quai villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon").getQuais().get(0);
		System.out.println("quai villejuif " + villejuif);
		Map<Quai, DistanceAntecedants> distanceMapRepublique = Dijkstra.getDistanceAntecedantsMap(service.getGraphe(), republique);
		Chemin pathRepubliqueBastille = MeilleurChemin.MeilleurChemin(distanceMapRepublique, republique, bastille);

		System.out.println("République - Bastille");
		for (var quai : pathRepubliqueBastille.cheminQuai()) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
		System.out.println("poid total du trajet " + pathRepubliqueBastille.poid());

		System.out.println("République - Villejuif Louis Aragon");
		Chemin pathRepubliqueVillejuif = MeilleurChemin.MeilleurChemin(distanceMapRepublique, republique, villejuif);
		for (var quai : pathRepubliqueVillejuif.cheminQuai()) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
		System.out.println("poid total du trajet " + pathRepubliqueVillejuif.poid());
	}

	@Test
	void checkMeilleurCheminTemps() {
		Service service = Service.getInstance();
		System.out.println("MeilleurChemin Temp test");
		Station republique = service.getGraphe().getStationParNom("République");
		Station bastille = service.getGraphe().getStationParNom("Bastille");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");

		System.out.println("République - Bastille");
		Chemin pathRepubliqueBastille = MeilleurChemin.MeilleurCheminTemps(republique, bastille);
		for (var quai : pathRepubliqueBastille.cheminQuai()) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
		System.out.println("Temps total du trajet " + pathRepubliqueBastille.poid());

		System.out.println("République - Villejuif Louis Aragon");
		Chemin pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminTemps(republique, villejuif);
		for (var quai : pathRepubliqueVillejuif.cheminQuai()) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
		System.out.println("Temps total du trajet " + pathRepubliqueVillejuif.poid());
	}

	@Test
	void checkMeilleurCheminCorrespondances() {
		Service service = Service.getInstance();
		System.out.println("MeilleurChemin Correspondances test");

		Station republique = service.getGraphe().getStationParNom("République");
		Station bastille = service.getGraphe().getStationParNom("Bastille");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");

		System.out.println("République - Bastille");
		Chemin pathRepubliqueBastille = MeilleurChemin.MeilleurCheminCorrespondances(republique, bastille);
		for (var quai : pathRepubliqueBastille.cheminQuai()) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
		System.out.println("Nombre de correspondances du trajet " + pathRepubliqueBastille.poid());

		System.out.println("République - Villejuif Louis Aragon");
		Chemin pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminCorrespondances(republique, villejuif);
		for (var quai : pathRepubliqueVillejuif.cheminQuai()) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
		System.out.println("Nombre de correspondances du trajet " + pathRepubliqueVillejuif.poid());
	}

	@Test
	void checkMeilleurCheminCO2() {
		Service service = Service.getInstance();
		System.out.println("MeilleurChemin CO2 test");

		Station republique = service.getGraphe().getStationParNom("République");
		Station bastille = service.getGraphe().getStationParNom("Bastille");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");

		System.out.println("République - Bastille");
		Chemin pathRepubliqueBastille = MeilleurChemin.MeilleurCheminCO2(republique, bastille);
		for (var quai : pathRepubliqueBastille.cheminQuai()) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
		System.out.println("Equivalent CO2 du trajet " + pathRepubliqueBastille.poid());

		System.out.println("République - Villejuif Louis Aragon");
		Chemin pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminCO2(republique, villejuif);
		for (var quai : pathRepubliqueVillejuif.cheminQuai()) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
		System.out.println("Equivalent CO2 du trajet " + pathRepubliqueVillejuif.poid());
	}
}
