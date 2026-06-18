package backend.models;

public class Arete {
    Quai source;
    Quai destination;
    int poid;
    Ligne ligne;
    public enum Type { pied, metro }
    Type type;

    public Arete(Quai source, Quai destination, int poid, Ligne ligne, Type type){
        this.source = source;
        this.destination = destination;
        this.poid = poid;
        this.ligne = ligne;
        this.type = type;
    }

    public Quai getSource()      { return source; }
    public Quai getDestination() { return destination; }
    public int getPoid()         { return poid; }
    public Ligne getLigne()      { return ligne; }
    public Type getType()        { return type; }
}