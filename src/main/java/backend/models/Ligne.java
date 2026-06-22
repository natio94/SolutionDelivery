package backend.models;

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
}
