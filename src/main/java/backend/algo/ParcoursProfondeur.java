package backend.algo;

import backend.models.Arete;
import backend.models.Quai;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParcoursProfondeur {


    public boolean estConnexe(List<Quai> tousLesQuais) {
        if (tousLesQuais == null || tousLesQuais.isEmpty() || tousLesQuais.size() == 1) {
            return true;
        }

        Set<Quai> visites = new HashSet<>();

        Quai depart = tousLesQuais.getFirst();
        dfs(depart, visites);

        return visites.size() == new HashSet<>(tousLesQuais).size();
    }

    private void dfs(Quai courant, Set<Quai> visites) {
        visites.add(courant);

        for (Arete arete : courant.getVoisins()) {
            Quai voisin = arete.getDestination();

            if (!visites.contains(voisin)) {
                dfs(voisin, visites);
            }
        }
    }
}
