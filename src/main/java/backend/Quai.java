package backend;

import java.util.ArrayList;

public class Quai {
    int id;
    Station station;
    Ligne ligne;
    int directionId;
    ArrayList<Arete> voisins;

    public Quai(int id, Ligne ligne, Station station){
        this.voisins = new ArrayList<Arete>();
        this.station = station;
    }
}
