package backend.algo;

import backend.models.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KruskalTest {

    private Station creerStation(String id) {
        return new Station(id, "Station " + id, 0.0, 0.0);
    }

    private Quai creerQuai(String id, Station station) {
        return new Quai(id, new Ligne("L1", "Ligne 1", "blue"), station);
    }

    private Graphe creerGrapheSimple() {
        // Graphe :   Q1 --60-- Q2 --90-- Q3
        //             \---------120-------/
        // ACPM attendu : Q1-Q2 (60) + Q2-Q3 (90) = 150
        Quai q1 = creerQuai("Q1", creerStation("S1"));
        Quai q2 = creerQuai("Q2", creerStation("S2"));
        Quai q3 = creerQuai("Q3", creerStation("S3"));

        Arete a1 = new Arete(q1, q2, 60, null, Arete.Type.metro);
        Arete a2 = new Arete(q2, q3, 90, null, Arete.Type.metro);
        Arete a3 = new Arete(q1, q3, 120, null, Arete.Type.metro);

        q1.addVoisin(a1);
        q2.addVoisin(a2);
        q1.addVoisin(a3);

        Graphe g = new Graphe();
        g.addQuai(q1);
        g.addQuai(q2);
        g.addQuai(q3);
        g.addArete(a1);
        g.addArete(a2);
        g.addArete(a3);
        return g;
    }

    @Test
    void acpm_contientTousLesQuais() {
        Graphe g = creerGrapheSimple();
        Graphe acpm = new Kruskal().getACPM(g);
        // L'ACPM garde tous les noeuds
        assertEquals(g.getQuais().size(), acpm.getQuais().size());
    }

    @Test
    void acpm_contientVMoinsUneAretes() {
        Graphe g = creerGrapheSimple();
        Graphe acpm = new Kruskal().getACPM(g);
        // Un arbre couvrant de 3 noeuds a toujours 2 arêtes
        assertEquals(2, acpm.getAretes().size());
    }

    @Test
    void acpm_exclutLAreteLaPlusChere() {
        Graphe g = creerGrapheSimple();
        Graphe acpm = new Kruskal().getACPM(g);
        // L'arête de 120 doit être exclue car elle crée un cycle
        boolean contient120 = acpm.getAretes().stream().anyMatch(a -> a.getPoid() == 120);
        assertFalse(contient120);
    }

    @Test
    void acpm_poidsTotalCorrect() {
        Graphe g = creerGrapheSimple();
        Graphe acpm = new Kruskal().getACPM(g);
        int total = acpm.getAretes().stream().mapToInt(Arete::getPoid).sum();
        assertEquals(150, total);
    }
}