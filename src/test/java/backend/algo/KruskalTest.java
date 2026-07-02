package backend.algo;

import backend.models.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KruskalTest {

    private Station creerStation(String id) {
        return new Station(id, "Station " + id, 0.0, 0.0);
    }

    private Quai creerQuai(String id, Station station) {
        return new Quai(id, new Ligne("L1", "Ligne 1", "blue"), station, 0, 0);
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

        q1.addVoisin(a1); q2.addVoisin(a2); q1.addVoisin(a3);

        Graphe g = new Graphe();
        g.addQuai(q1); g.addQuai(q2); g.addQuai(q3);
        g.addArete(a1); g.addArete(a2); g.addArete(a3);
        return g;
    }

    @Test
    void acpm_contientTousLesQuais() {
        Graphe g = creerGrapheSimple();
        Graphe acpm = new Kruskal().getACPM(g);
        assertEquals(g.getQuais().size(), acpm.getQuais().size());
    }

    @Test
    void acpm_contientVMoinsUneAretes() {
        Graphe g = creerGrapheSimple();
        Graphe acpm = new Kruskal().getACPM(g);
        // Un arbre couvrant de 3 stations a toujours 2 arêtes
        assertEquals(2, acpm.getAretes().size());
    }

    @Test
    void acpm_exclutLAreteLaPlusChere() {
        Graphe g = creerGrapheSimple();
        Graphe acpm = new Kruskal().getACPM(g);
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

    @Test
    void acpm_areteIntraStation_ignoree() {
        // Deux quais dans la MÊME station reliés entre eux + une autre station
        // L'arête intra-station ne doit pas apparaître dans l'ACPM (même composante)
        Station s1 = creerStation("S1");
        Station s2 = creerStation("S2");
        Quai q1a = creerQuai("Q1A", s1); // quai ligne A dans S1
        Quai q1b = creerQuai("Q1B", s1); // quai ligne B dans S1
        Quai q2  = creerQuai("Q2",  s2);

        Arete intra    = new Arete(q1a, q1b, 10,  null, Arete.Type.pied);
        Arete externe  = new Arete(q1a, q2,  50,  null, Arete.Type.metro);
        Arete externe2 = new Arete(q1b, q2,  80,  null, Arete.Type.metro);

        q1a.addVoisin(intra); q1a.addVoisin(externe); q1b.addVoisin(externe2);

        Graphe g = new Graphe();
        g.addQuai(q1a); g.addQuai(q1b); g.addQuai(q2);
        g.addArete(intra); g.addArete(externe); g.addArete(externe2);

        Graphe acpm = new Kruskal().getACPM(g);

        // L'ACPM ne relie que 2 stations → 1 arête inter-stations
        assertEquals(1, acpm.getAretes().size());
        // Ce doit être l'arête de coût 50 (la moins chère entre les deux stations)
        assertEquals(50, acpm.getAretes().get(0).getPoid());
    }

    @Test
    void acpm_grapheDeuxStations_uneArete() {
        // Cas minimal : 2 stations, 1 arête → l'ACPM = ce graphe
        Quai q1 = creerQuai("Q1", creerStation("S1"));
        Quai q2 = creerQuai("Q2", creerStation("S2"));
        Arete a = new Arete(q1, q2, 42, null, Arete.Type.metro);
        q1.addVoisin(a);

        Graphe g = new Graphe();
        g.addQuai(q1); g.addQuai(q2); g.addArete(a);

        Graphe acpm = new Kruskal().getACPM(g);

        assertEquals(1, acpm.getAretes().size());
        assertEquals(42, acpm.getAretes().get(0).getPoid());
    }

    @Test
    void acpm_grapheAcinq_poidsMinimal() {
        // Graphe complet à 4 stations — vérifie que Kruskal choisit les arêtes minimales
        //  S1 --1-- S2
        //  |  \     |
        //  4    3   2
        //  |      \ |
        //  S4 --5-- S3
        Station s1 = creerStation("S1"), s2 = creerStation("S2");
        Station s3 = creerStation("S3"), s4 = creerStation("S4");
        Quai q1 = creerQuai("Q1", s1), q2 = creerQuai("Q2", s2);
        Quai q3 = creerQuai("Q3", s3), q4 = creerQuai("Q4", s4);

        Arete a12 = new Arete(q1, q2, 1, null, Arete.Type.metro);
        Arete a23 = new Arete(q2, q3, 2, null, Arete.Type.metro);
        Arete a13 = new Arete(q1, q3, 3, null, Arete.Type.metro);
        Arete a14 = new Arete(q1, q4, 4, null, Arete.Type.metro);
        Arete a34 = new Arete(q3, q4, 5, null, Arete.Type.metro);

        q1.addVoisin(a12); q2.addVoisin(a23); q1.addVoisin(a13);
        q1.addVoisin(a14); q3.addVoisin(a34);

        Graphe g = new Graphe();
        g.addQuai(q1); g.addQuai(q2); g.addQuai(q3); g.addQuai(q4);
        g.addArete(a12); g.addArete(a23); g.addArete(a13);
        g.addArete(a14); g.addArete(a34);

        Graphe acpm = new Kruskal().getACPM(g);

        // ACPM optimal : a12(1) + a23(2) + a14(4) = 7
        assertEquals(3, acpm.getAretes().size());
        int total = acpm.getAretes().stream().mapToInt(Arete::getPoid).sum();
        assertEquals(7, total);
    }
}
