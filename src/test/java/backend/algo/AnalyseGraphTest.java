package backend.algo;

import backend.models.Arete;
import backend.models.Ligne;
import backend.models.Quai;
import backend.models.Station;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalyseGraphTest {

    // Helpers pour créer des objets de test sans charger les fichiers GTFS
    private Station creerStation(String id) {
        return new Station(id, "Station " + id, 0.0, 0.0);
    }

    private Quai creerQuai(String id, Station station) {
        return new Quai(id, new Ligne("L1", "Ligne 1", "blue"), station);
    }

    @Test
    void grapheVide_estConnexe() {
        assertTrue(AnalyseGraph.estConnexe(new ArrayList<>()));
    }

    @Test
    void grapheUnSeulQuai_estConnexe() {
        Quai q = creerQuai("Q1", creerStation("S1"));
        assertTrue(AnalyseGraph.estConnexe(List.of(q)));
    }

    @Test
    void grapheLineaire_estConnexe() {
        // Q1 → Q2 → Q3 et Q3 → Q2 → Q1 (bidirectionnel)
        Quai q1 = creerQuai("Q1", creerStation("S1"));
        Quai q2 = creerQuai("Q2", creerStation("S2"));
        Quai q3 = creerQuai("Q3", creerStation("S3"));

        q1.addVoisin(new Arete(q1, q2, 60, null, Arete.Type.metro));
        q2.addVoisin(new Arete(q2, q3, 60, null, Arete.Type.metro));
        q3.addVoisin(new Arete(q3, q2, 60, null, Arete.Type.metro));
        q2.addVoisin(new Arete(q2, q1, 60, null, Arete.Type.metro));

        assertTrue(AnalyseGraph.estConnexe(List.of(q1, q2, q3)));
    }

    @Test
    void grapheOrienteSansRetour_nonConnexe() {
        // Q1 → Q2 seulement, pas de retour → non fortement connexe
        Quai q1 = creerQuai("Q1", creerStation("S1"));
        Quai q2 = creerQuai("Q2", creerStation("S2"));

        q1.addVoisin(new Arete(q1, q2, 60, null, Arete.Type.metro));

        assertFalse(AnalyseGraph.estConnexe(List.of(q1, q2)));
    }

    @Test
    void grapheDeconnecte_nonConnexe() {
        // Q1 → Q2, Q3 isolé
        Quai q1 = creerQuai("Q1", creerStation("S1"));
        Quai q2 = creerQuai("Q2", creerStation("S2"));
        Quai q3 = creerQuai("Q3", creerStation("S3"));

        q1.addVoisin(new Arete(q1, q2, 60, null, Arete.Type.metro));
        q2.addVoisin(new Arete(q2, q1, 60, null, Arete.Type.metro));

        assertFalse(AnalyseGraph.estConnexe(List.of(q1, q2, q3)));
    }
}