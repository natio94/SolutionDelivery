package backend;

import backend.algo.*;
import backend.models.*;
import backend.gtfs.ConstructeurGraphe;

import java.util.ArrayList;
import java.util.List;

public class Service {

	private static volatile Service instance = null; // Service is a lazy singleton

    private final Graphe graphe;
    private final Graphe grapheCorrespondances;
    private final Graphe grapheCO2;

    private Service() {
	ConstructeurGraphe constructeur = new ConstructeurGraphe();
        this.graphe = constructeur.buildGraph();
        this.grapheCorrespondances = constructeur.buildGraphCorrespondances();
        this.grapheCO2 = constructeur.buildGraphCO2();
    }

    // Service is a lazy singleton
    public static Service getInstance() {
	    if (instance == null) {
		    synchronized (Service.class) {
			    if (instance == null) {
				    instance = new Service();
			    }
		    }
	    }
	    return instance;
    }

    // --- Accès au graphe ---
    public Graphe getGraphe() {
        return graphe;
    }
    public Graphe getGrapheCorrespondances() {
        return grapheCorrespondances;
    }
    public Graphe getGrapheCO2() {
        return grapheCO2;
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
    public Graphe getACPM() { return new Kruskal().getACPM(this.graphe); }

    public boolean estConnexeStations() {
        return AnalyseGraph.estConnexeStations(graphe.getStations());
    }

    public static Chemin MeilleurCheminTemps(Station origin, Station destination) {
	    return MeilleurChemin.MeilleurCheminTemps(origin, destination);
    }
    public static Chemin MeilleurCheminCorrespondances(Station origin, Station destination) {
	    return MeilleurChemin.MeilleurCheminCorrespondances(origin, destination);
    }
    public static Chemin MeilleurCheminCO2(Station origin, Station destination) {
	    return MeilleurChemin.MeilleurCheminCO2(origin, destination);
    }
}
