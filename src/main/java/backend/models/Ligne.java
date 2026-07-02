package backend.models;

import java.util.Objects;

public class Ligne {
    String id;
    String nom;
    String couleur;

    public Ligne(String id, String nom, String couleur){
        this.id = id;
        this.nom = nom;
        this.couleur = couleur;
    }

    public String getNom() {
        return nom;
    }

    public String getCouleur() {
        return couleur;
    }

    public String getId(){ return this.id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ligne autre = (Ligne) o;
        return Objects.equals(this.id, autre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
