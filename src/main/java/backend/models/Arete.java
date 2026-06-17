package backend.models;

public class Arete {
    Quai source;
    Quai destination;
    int poid;
    Ligne ligne;
    enum Type {pied, metro};
    Type type;

    Arete(Quai source, Quai destination, int poid, Ligne ligne, Type type){
        this.source = source;
        this.destination = destination;
        this.poid = poid;
        this.ligne = ligne;
        this.type = type;
    }
}
