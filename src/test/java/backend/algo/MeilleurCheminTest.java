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
		List<Quai> pathRepubliqueBastille = MeilleurChemin.MeilleurChemin(distanceMapRepublique, republique, bastille);

		System.out.println("République - Bastille");
		for (var quai : pathRepubliqueBastille) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}

		System.out.println("République - Villejuif Louis Aragon");
		List<Quai> pathRepubliqueVillejuif = MeilleurChemin.MeilleurChemin(distanceMapRepublique, republique, villejuif);
		for (var quai : pathRepubliqueVillejuif) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
	}

	@Test
	void checkMeilleurCheminStation() {
		System.out.println("MeilleurChemin Station test");
		Service service = Service.getInstance();
		Station republique = service.getGraphe().getStationParNom("République");
		Station bastille = service.getGraphe().getStationParNom("Bastille");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");
		System.out.println("station villejuif " + villejuif);
		Map<Quai, DistanceAntecedants> distanceMapRepublique = Dijkstra.getDistanceAntecedantsMap(service.getGraphe(), republique.getQuais().get(0));
		List<Quai> pathRepubliqueBastille = MeilleurChemin.MeilleurChemin(distanceMapRepublique, republique, bastille);

		System.out.println("République - Bastille");
		for (var quai : pathRepubliqueBastille) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}

		System.out.println("République - Villejuif Louis Aragon");
		List<Quai> pathRepubliqueVillejuif = MeilleurChemin.MeilleurChemin(distanceMapRepublique, republique, villejuif);
		for (var quai : pathRepubliqueVillejuif) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
	}

	@Test
	void checkMeilleurCheminTemps() {
		Service service = Service.getInstance();
		System.out.println("MeilleurChemin Temp test");
		Station republique = service.getGraphe().getStationParNom("République");
		Station bastille = service.getGraphe().getStationParNom("Bastille");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");

		System.out.println("République - Bastille");
		List<Quai> pathRepubliqueBastille = MeilleurChemin.MeilleurCheminTemps(republique, bastille);
		for (var quai : pathRepubliqueBastille) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}

		System.out.println("République - Villejuif Louis Aragon");
		List<Quai> pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminTemps(republique, villejuif);
		for (var quai : pathRepubliqueVillejuif) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
	}

	@Test
	void checkMeilleurCheminCorrespondances() {
		Service service = Service.getInstance();
		System.out.println("MeilleurChemin Correspondances test");

		Station republique = service.getGraphe().getStationParNom("République");
		Station bastille = service.getGraphe().getStationParNom("Bastille");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");

		System.out.println("République - Bastille");
		List<Quai> pathRepubliqueBastille = MeilleurChemin.MeilleurCheminCorrespondances(republique, bastille);
		for (var quai : pathRepubliqueBastille) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}

		System.out.println("République - Villejuif Louis Aragon");
		List<Quai> pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminCorrespondances(republique, villejuif);
		for (var quai : pathRepubliqueVillejuif) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
	}

	@Test
	void checkMeilleurCheminCO2() {
		Service service = Service.getInstance();
		System.out.println("MeilleurChemin CO2 test");

		Station republique = service.getGraphe().getStationParNom("République");
		Station bastille = service.getGraphe().getStationParNom("Bastille");
		Station villejuif = service.getGraphe().getStationParNom("Villejuif - Louis Aragon");

		System.out.println("République - Bastille");
		List<Quai> pathRepubliqueBastille = MeilleurChemin.MeilleurCheminCO2(republique, bastille);
		for (var quai : pathRepubliqueBastille) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}

		System.out.println("République - Villejuif Louis Aragon");
		List<Quai> pathRepubliqueVillejuif = MeilleurChemin.MeilleurCheminCO2(republique, villejuif);
		for (var quai : pathRepubliqueVillejuif) {
			System.out.println("id: " + quai.getId() + " ligne: " + quai.getLigne().getNom() + " station: " + quai.getStation().getNom());
		}
	}
}
