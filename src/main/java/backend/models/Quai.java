package backend.models;

import java.util.ArrayList;
import java.util.Objects;

public class Quai {
    String id;
    Station station;
    Ligne ligne;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quai autre = (Quai) o;
        return Objects.equals(this.id, autre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
