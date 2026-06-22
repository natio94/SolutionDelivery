package backend.algo;

import backend.models.Arete;
import backend.models.Quai;

import java.util.*;

public class AnalyseGraph {

    public static boolean estConnexe(List<Quai> tousLesQuais) {
        if (tousLesQuais == null || tousLesQuais.isEmpty() || tousLesQuais.size() == 1) {
            return true;
        }

        Quai depart = tousLesQuais.getFirst();

        // Passe 1 : depuis depart, peut-on atteindre tous les quais ?
        Set<Quai> visitesPasse1 = new HashSet<>();
        dfs(depart, visitesPasse1);
        if (visitesPasse1.size() != tousLesQuais.size()) return false;

        // Passe 2 : sur le graphe inversé, depuis depart, peut-on encore tout atteindre ?
        // Si oui, cela signifie que tous les quais peuvent atteindre depart dans le graphe normal.
        Map<Quai, List<Quai>> grapheInverse = construireGrapheInverse(tousLesQuais);
        Set<Quai> visitesPasse2 = new HashSet<>();
        dfsInverse(depart, grapheInverse, visitesPasse2);
        return visitesPasse2.size() == tousLesQuais.size();
    }

    // DFS itératif sur le graphe normal
    private static void dfs(Quai depart, Set<Quai> visites) {
        Deque<Quai> pile = new ArrayDeque<>();
        pile.push(depart);
        while (!pile.isEmpty()) {
            Quai courant = pile.pop();
            if (visites.contains(courant)) continue;
            visites.add(courant);
            for (Arete a : courant.getVoisins()) {
                if (!visites.contains(a.getDestination())) {
                    pile.push(a.getDestination());
                }
            }
        }
    }

    // Construit un graphe où chaque arête A→B devient B→A
    private static Map<Quai, List<Quai>> construireGrapheInverse(List<Quai> tousLesQuais) {
        Map<Quai, List<Quai>> inverse = new HashMap<>();
        for (Quai q : tousLesQuais) {
            inverse.putIfAbsent(q, new ArrayList<>());
            for (Arete a : q.getVoisins()) {
                inverse.computeIfAbsent(a.getDestination(), k -> new ArrayList<>()).add(q);
            }
        }
        return inverse;
    }

    // DFS itératif sur le graphe inversé
    private static void dfsInverse(Quai depart, Map<Quai, List<Quai>> grapheInverse, Set<Quai> visites) {
        Deque<Quai> pile = new ArrayDeque<>();
        pile.push(depart);
        while (!pile.isEmpty()) {
            Quai courant = pile.pop();
            if (visites.contains(courant)) continue;
            visites.add(courant);
            for (Quai voisin : grapheInverse.getOrDefault(courant, Collections.emptyList())) {
                if (!visites.contains(voisin)) {
                    pile.push(voisin);
                }
            }
        }
    }
}