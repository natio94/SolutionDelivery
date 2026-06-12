package backend;

import java.util.ArrayList;

public class Graphe {
    ArrayList<Quai> quais;
    ArrayList<Station> stations;
    ArrayList<Arete> aretes;
    ArrayList<Ligne> lignes;

    Graphe(){
        this.quais = new ArrayList<Quai>();
        this.stations = new ArrayList<Station>();
        this.aretes = new ArrayList<Arete>();
        this.lignes = new ArrayList<Ligne>();
    }

    public void addStation(Station station){
        this.stations.add(station);
    }
}
