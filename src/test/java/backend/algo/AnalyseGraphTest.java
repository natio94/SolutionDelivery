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

    private Station creerStation(String id) {
        return new Station(id, "Station " + id, 0.0, 0.0);
    }

    private Quai creerQuai(String id, Station station) {
        return new Quai(id, new Ligne("L1", "Ligne 1", "blue"), station,0,0);
    }

    private void relier(Quai from, Quai to) {
        from.addVoisin(new Arete(from, to, 60, null, Arete.Type.metro));
    }

    // ── estConnexe (quais) ──────────────────────────────────────────────────

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

        relier(q1, q2); relier(q2, q3);
        relier(q3, q2); relier(q2, q1);

        assertTrue(AnalyseGraph.estConnexe(List.of(q1, q2, q3)));
    }

    @Test
    void grapheOrienteSansRetour_nonConnexe() {
        // Q1 → Q2 seulement → non fortement connexe
        Quai q1 = creerQuai("Q1", creerStation("S1"));
        Quai q2 = creerQuai("Q2", creerStation("S2"));

        relier(q1, q2);

        assertFalse(AnalyseGraph.estConnexe(List.of(q1, q2)));
    }

    @Test
    void grapheDeconnecte_nonConnexe() {
        // Q1 ↔ Q2, Q3 isolé
        Quai q1 = creerQuai("Q1", creerStation("S1"));
        Quai q2 = creerQuai("Q2", creerStation("S2"));
        Quai q3 = creerQuai("Q3", creerStation("S3"));

        relier(q1, q2); relier(q2, q1);

        assertFalse(AnalyseGraph.estConnexe(List.of(q1, q2, q3)));
    }

    // ── estConnexeStations ─────────────────────────────────────────────────

    @Test
    void estConnexeStations_grapheVide() {
        assertTrue(AnalyseGraph.estConnexeStations(new ArrayList<>()));
    }

    @Test
    void estConnexeStations_uneStation() {
        Station s1 = creerStation("S1");
        assertTrue(AnalyseGraph.estConnexeStations(List.of(s1)));
    }

    @Test
    void estConnexeStations_deuxStationsConnexes() {
        Station s1 = creerStation("S1");
        Station s2 = creerStation("S2");
        Quai q1 = creerQuai("Q1", s1);
        Quai q2 = creerQuai("Q2", s2);
        s1.addQuai(q1);
        s2.addQuai(q2);

        relier(q1, q2); relier(q2, q1);

        assertTrue(AnalyseGraph.estConnexeStations(List.of(s1, s2)));
    }

    @Test
    void estConnexeStations_stationSansQuais_nonConnexe() {
        // S1 ↔ S2 connectées, S3 n'a aucun quai → ne peut être visitée
        Station s1 = creerStation("S1");
        Station s2 = creerStation("S2");
        Station s3 = creerStation("S3"); // aucun quai
        Quai q1 = creerQuai("Q1", s1);
        Quai q2 = creerQuai("Q2", s2);
        s1.addQuai(q1);
        s2.addQuai(q2);

        relier(q1, q2); relier(q2, q1);

        assertFalse(AnalyseGraph.estConnexeStations(List.of(s1, s2, s3)));
    }

    @Test
    void estConnexeStations_orienteSansRetour_nonConnexe() {
        // Q1 → Q2 seulement : S2 ne peut pas atteindre S1
        Station s1 = creerStation("S1");
        Station s2 = creerStation("S2");
        Quai q1 = creerQuai("Q1", s1);
        Quai q2 = creerQuai("Q2", s2);
        s1.addQuai(q1);
        s2.addQuai(q2);

        relier(q1, q2); // sens unique

        assertFalse(AnalyseGraph.estConnexeStations(List.of(s1, s2)));
    }

    @Test
    void estConnexeStations_correspondanceRelieLesStations() {
        // S1 (Q1_L1) et S2 (Q2_L1, Q2_L2) :
        // Q1_L1 → Q2_L1 (métro), Q2_L1 ↔ Q2_L2 (correspondance pied), Q2_L2 → Q1_L1 (métro retour)
        Station s1 = creerStation("S1");
        Station s2 = creerStation("S2");
        Quai q1L1 = creerQuai("Q1_L1", s1);
        Quai q2L1 = creerQuai("Q2_L1", s2);
        Quai q2L2 = creerQuai("Q2_L2", s2);
        s1.addQuai(q1L1);
        s2.addQuai(q2L1);
        s2.addQuai(q2L2);

        q1L1.addVoisin(new Arete(q1L1, q2L1, 60,  null, Arete.Type.metro));
        q2L1.addVoisin(new Arete(q2L1, q2L2, 120, null, Arete.Type.pied));
        q2L2.addVoisin(new Arete(q2L2, q2L1, 120, null, Arete.Type.pied));
        q2L2.addVoisin(new Arete(q2L2, q1L1, 60,  null, Arete.Type.metro));

        assertTrue(AnalyseGraph.estConnexeStations(List.of(s1, s2)));
    }

    @Test
    void estConnexeStations_troisStationsChaine_connexe() {
        // S1 ↔ S2 ↔ S3 : connexité forte via chaîne
        Station s1 = creerStation("S1");
        Station s2 = creerStation("S2");
        Station s3 = creerStation("S3");
        Quai q1 = creerQuai("Q1", s1);
        Quai q2 = creerQuai("Q2", s2);
        Quai q3 = creerQuai("Q3", s3);
        s1.addQuai(q1); s2.addQuai(q2); s3.addQuai(q3);

        relier(q1, q2); relier(q2, q1);
        relier(q2, q3); relier(q3, q2);

        assertTrue(AnalyseGraph.estConnexeStations(List.of(s1, s2, s3)));
    }
}