package backend.models;

public class Arete {
    Quai source;
    Quai destination;
    double poid;
    Ligne ligne;
    public enum Type { pied, metro }
    Type type;

    public Arete(Quai source, Quai destination, double poid, Ligne ligne, Type type){
        this.source = source;
        this.destination = destination;
        this.poid = poid;
        this.ligne = ligne;
        this.type = type;
    }

    public Quai getSource()      { return source; }
    public Quai getDestination() { return destination; }
    public double getPoid()         { return poid; }
    public Ligne getLigne()      { return ligne; }
    public Type getType()        { return type; }

    public void setPoid(double poid) {
	    this.poid = poid;
    }
}
