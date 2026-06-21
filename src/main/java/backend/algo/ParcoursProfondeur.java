package backend.algo;

import backend.models.Arete;
import backend.models.Quai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParcoursProfondeur {

    public List<Quai> trouverChemin(Quai depart, Quai arrivee) {
        Set<Quai> visites = new HashSet<>();
        List<Quai> chemin = new ArrayList<>();

        if (parcoursEnProfondeur(depart, arrivee, visites, chemin)) {
            return  chemin;
        }else{
            return new ArrayList<>();
        }
    }

    private boolean parcoursEnProfondeur(Quai courant, Quai arrivee, Set<Quai> visites, List<Quai> chemin) {
        visites.add(courant);
        chemin.add(courant);

        if (courant.equals(arrivee)) {
            return true;
        }

        for (Arete arete : courant.getVoisins()) {
            Quai voisin = arete.getDestination();

            if (!visites.contains(voisin)) {
                if (parcoursEnProfondeur(voisin, arrivee, visites, chemin)) {
                    return true;
                }
            }
        }


        chemin.removeLast();
        return false;
    }
}
