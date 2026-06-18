package backend.models;

import java.util.ArrayList;

public class Quai {
    String id;
    Station station;
    Ligne ligne;
    int directionId;
    ArrayList<Arete> voisins;

    public Quai(String id, Ligne ligne, Station station){
        this.id = id;
        this.ligne = ligne;
        this.voisins = new ArrayList<Arete>();
        this.station = station;
    }

    public String getId()            { return this.id; }
    public Station getStation()      { return this.station; }
    public Ligne getLigne()          { return this.ligne; }
    public ArrayList<Arete> getVoisins() { return this.voisins; }
    public void addVoisin(Arete a)   { this.voisins.add(a); }
}
