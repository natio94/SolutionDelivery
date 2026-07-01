package backend.models;

import java.util.ArrayList;
import java.util.Objects;

public class Station {
    String id;
    String nom;
    ArrayList<Quai> quais;
    double longitude;
    double latitude;

    public Station(String id, String nom, double longitude, double latitude){
        this.id = id;
        this.nom = nom;
        this.longitude = longitude;
        this.latitude = latitude;
        this.quais = new ArrayList<>();
    }

    public String getId(){ return this.id; }

    public String getNom() {
        return nom;
    }

    public ArrayList<Quai> getQuais() {
        return quais;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void addQuai(Quai q){
        this.quais.add(q);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station autre = (Station) o;
        return Objects.equals(this.id, autre.id);
    }
}
