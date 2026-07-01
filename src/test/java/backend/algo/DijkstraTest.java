package backend;

import backend.algo.Dijkstra;
import backend.models.*;

import java.util.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {

	private Station creerStation(String id) {
		return new Station(id, "Station " + id, 0.0, 0.0);
	}

	private Quai creerQuai(String id, Station station) {
		return new Quai(id, new Ligne("L1", "Ligne 1", "blue"), station);
	}

	@Test
	void checkDistancesGraphe1() {
		// Graphe :   Q1 --60-- Q2 --90-- Q3
		//             \---------120-------/
		Quai q1 = creerQuai("Q1", creerStation("S1"));
		Quai q2 = creerQuai("Q2", creerStation("S2"));
		Quai q3 = creerQuai("Q3", creerStation("S3"));

		Arete a1 = new Arete(q1, q2, 60, null, Arete.Type.metro);
		Arete a2 = new Arete(q2, q3, 90, null, Arete.Type.metro);
		Arete a3 = new Arete(q1, q3, 120, null, Arete.Type.metro);

		q1.addVoisin(a1);
		q2.addVoisin(a2);
		q1.addVoisin(a3);

		Graphe testGraphe = new Graphe();
		testGraphe.addQuai(q1);
		testGraphe.addQuai(q2);
		testGraphe.addQuai(q3);
		testGraphe.addArete(a1);
		testGraphe.addArete(a2);
		testGraphe.addArete(a3);

		Map<Quai, DistanceAntecedants> results = Dijkstra.getDistanceAntecedantsMap(testGraphe, q1);
		assertEquals(results.get(q1).getDistance(), 0);
		assertEquals(results.get(q2).getDistance(), 60);
		assertEquals(results.get(q3).getDistance(), 120);
	}

	@Test
	void checkDistancesGraphe2() {
		// Graphe du TD3 graphes mastercamp
		Quai q1 = creerQuai("Q1", creerStation("S1"));
		Quai q2 = creerQuai("Q2", creerStation("S2"));
		Quai q3 = creerQuai("Q3", creerStation("S3"));
		Quai q4 = creerQuai("Q4", creerStation("S4"));
		Quai q5 = creerQuai("Q5", creerStation("S5"));
		Quai q6 = creerQuai("Q6", creerStation("S6"));
		List<Quai> listQuai = List.of(q1, q2, q3, q4, q5, q6);

		Arete a1 = new Arete(q1, q2, 4, null, Arete.Type.metro);
		Arete a2 = new Arete(q1, q3, 8, null, Arete.Type.metro);
		Arete a3 = new Arete(q1, q5, 11, null, Arete.Type.metro);
		Arete a4 = new Arete(q2, q3, 3, null, Arete.Type.metro);
		Arete a5 = new Arete(q2, q4, 2, null, Arete.Type.metro);
		Arete a6 = new Arete(q3, q4, 1, null, Arete.Type.metro);
		Arete a7 = new Arete(q3, q5, 2, null, Arete.Type.metro);
		Arete a8 = new Arete(q4, q3, 2, null, Arete.Type.metro);
		Arete a9 = new Arete(q4, q5, 5, null, Arete.Type.metro);
		Arete a10 = new Arete(q4, q6, 7, null, Arete.Type.metro);
		Arete a11 = new Arete(q5, q6, 3, null, Arete.Type.metro);
		List<Arete> listArete = List.of(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11);

		q1.addVoisin(a1);
		q1.addVoisin(a2);
		q1.addVoisin(a3);
		q2.addVoisin(a4);
		q2.addVoisin(a5);
		q3.addVoisin(a6);
		q3.addVoisin(a7);
		q4.addVoisin(a8);
		q4.addVoisin(a9);
		q4.addVoisin(a10);
		q5.addVoisin(a11);

		Graphe testGraphe = new Graphe();
		for (var quai : listQuai) {
			testGraphe.addQuai(quai);
		}
		for (var arete : listArete) {
			testGraphe.addArete(arete);
		}

		Map<Quai, DistanceAntecedants> results = Dijkstra.getDistanceAntecedantsMap(testGraphe, q1);
		assertEquals(results.get(q1).getDistance(), 0);
		assertEquals(results.get(q2).getDistance(), 4);
		assertEquals(results.get(q3).getDistance(), 7);
		assertEquals(results.get(q4).getDistance(), 6);
		assertEquals(results.get(q5).getDistance(), 9);
		assertEquals(results.get(q6).getDistance(), 12);
	}

	@Test
	void checkNoInfiniteLoop() {
		System.out.println("Dijkstra quai infinite loop check through elapsed time");
		Service service = new Service();
		long startTime = System.nanoTime();
		Quai republique = service.getGraphe().getStationParNom("République").getQuais().get(0);
		Dijkstra.getDistanceAntecedantsMap(service.getGraphe(), republique);
		long stopTime = System.nanoTime();
		System.out.println("Dijkstra quai elapsed time: " + (stopTime - startTime));
	}

	@Test
	void grapheUnSeulNoeud_distanceZero() {
		Quai q1 = creerQuai("Q1", creerStation("S1"));
		Graphe g = new Graphe();
		g.addQuai(q1);

		Map<Quai, DistanceAntecedants> results = Dijkstra.getDistanceAntecedantsMap(g, q1);

		assertEquals(0, results.get(q1).getDistance());
		assertEquals(1, results.size());
	}

	@Test
	void noeudNonAtteignable_absenceDeResultat() {
		// Q1 ↔ Q2 connectés, Q3 complètement isolé
		Quai q1 = creerQuai("Q1", creerStation("S1"));
		Quai q2 = creerQuai("Q2", creerStation("S2"));
		Quai q3 = creerQuai("Q3", creerStation("S3"));

		q1.addVoisin(new Arete(q1, q2, 60, null, Arete.Type.metro));
		q2.addVoisin(new Arete(q2, q1, 60, null, Arete.Type.metro));

		Graphe g = new Graphe();
		g.addQuai(q1); g.addQuai(q2); g.addQuai(q3);

		Map<Quai, DistanceAntecedants> results = Dijkstra.getDistanceAntecedantsMap(g, q1);

		assertNotNull(results.get(q1));
		assertNotNull(results.get(q2));
		assertNull(results.get(q3));
	}

	@Test
	void deuxCheminsEquidistants_deuxAntecedants() {
		// Q1 → Q3 direct (coût 10)
		// Q1 → Q2 (coût 5) → Q3 (coût 5) : même coût total = 10
		// Q3 doit avoir Q1 ET Q2 comme antécédants
		Quai q1 = creerQuai("Q1", creerStation("S1"));
		Quai q2 = creerQuai("Q2", creerStation("S2"));
		Quai q3 = creerQuai("Q3", creerStation("S3"));

		q1.addVoisin(new Arete(q1, q2, 5,  null, Arete.Type.metro));
		q1.addVoisin(new Arete(q1, q3, 10, null, Arete.Type.metro));
		q2.addVoisin(new Arete(q2, q3, 5,  null, Arete.Type.metro));

		Graphe g = new Graphe();
		g.addQuai(q1); g.addQuai(q2); g.addQuai(q3);

		Map<Quai, DistanceAntecedants> results = Dijkstra.getDistanceAntecedantsMap(g, q1);

		assertEquals(10, results.get(q3).getDistance());
		List<Quai> antecedants = results.get(q3).getAntecedants();
		assertEquals(2, antecedants.size());
		assertTrue(antecedants.contains(q1));
		assertTrue(antecedants.contains(q2));
	}

	@Test
	void cheminIndirect_plusCourt() {
		// Q1 → Q3 direct (coût 100)
		// Q1 → Q2 (coût 1) → Q3 (coût 1) : total 2, chemin indirect optimal
		Quai q1 = creerQuai("Q1", creerStation("S1"));
		Quai q2 = creerQuai("Q2", creerStation("S2"));
		Quai q3 = creerQuai("Q3", creerStation("S3"));

		q1.addVoisin(new Arete(q1, q2,   1, null, Arete.Type.metro));
		q1.addVoisin(new Arete(q1, q3, 100, null, Arete.Type.metro));
		q2.addVoisin(new Arete(q2, q3,   1, null, Arete.Type.metro));

		Graphe g = new Graphe();
		g.addQuai(q1); g.addQuai(q2); g.addQuai(q3);

		Map<Quai, DistanceAntecedants> results = Dijkstra.getDistanceAntecedantsMap(g, q1);

		assertEquals(2, results.get(q3).getDistance());
		assertTrue(results.get(q3).getAntecedants().contains(q2));
	}

	@Test
	void grapheReel_toutesStationsAtteignables() {
		// Sur le vrai graphe, toutes les stations doivent être atteignables depuis n'importe quelle origine
		Service service = new Service();
		Quai origine = service.getGraphe().getStationParNom("Châtelet").getQuais().get(0);

		Map<Quai, DistanceAntecedants> results = Dijkstra.getDistanceAntecedantsMap(service.getGraphe(), origine);

		int stationsAtteignables = 0;
		for (backend.models.Station s : service.getGraphe().getStations()) {
			boolean atteinte = s.getQuais().stream().anyMatch(q -> results.containsKey(q));
			if (atteinte) stationsAtteignables++;
		}
		assertEquals(service.getGraphe().getStations().size(), stationsAtteignables);
	}
}
