package backend;

import backend.algo.AnalyseGraph;
import backend.algo.Kruskal;
import backend.gtfs.ConstructeurGraphe;
import backend.models.Arete;
import backend.models.Graphe;
import backend.models.Station;

import java.util.ArrayList;
import java.util.List;

public class Service {

    private final Graphe graphe;

    public Service() {
        this.graphe = new ConstructeurGraphe().buildGraph();
    }

    // --- Accès au graphe ---
    public Graphe getGraphe() {
        return graphe;
    }

    // --- Recherche de stations ---
    public Station chercherStationExacte(String nom) {
        return graphe.getStationParNom(nom);
    }
    public List<Station> chercherStations(String fragment) {
        return graphe.rechercherStations(fragment);
    }

    // --- Connexité ---
    public boolean estConnexe() {
        return AnalyseGraph.estConnexe(new ArrayList<>(graphe.getQuais()));
    }

    // --- ACPM ---
    public Graphe getACPM(Graphe g) { return new Kruskal().getACPM(g); }
}