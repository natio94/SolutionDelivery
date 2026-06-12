package backend;

import java.util.ArrayList;

public class Station {
    int id;
    String nom;
    ArrayList<Quai> quais;
    double longitude;
    double latitude;
    int posX;
    int posY;

    Station(int id, String nom, double longitude, double latitude){
        this.id = id;
        this.nom = nom;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
