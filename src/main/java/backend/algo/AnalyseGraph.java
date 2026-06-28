package backend.algo;

import backend.models.Arete;
import backend.models.Quai;
import backend.models.Station;

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

    public static boolean estConnexeStations(Collection<Station> toutesLesStations) {
        if (toutesLesStations == null || toutesLesStations.isEmpty() || toutesLesStations.size() == 1) {
            return true;
        }

        // On récupère une station de départ
        Station depart = toutesLesStations.iterator().next();

        // Passe 1 : depuis la station de départ, peut-on atteindre toutes les autres stations ?
        Set<Station> visitesPasse1 = new HashSet<>();
        dfsStations(depart, visitesPasse1);
        if (visitesPasse1.size() != toutesLesStations.size()) {
            return false;
        }

        // Passe 2 : sur le graphe inversé des stations, depuis depart, peut-on encore tout atteindre ?
        Map<Station, List<Station>> grapheInverse = construireGrapheInverseStations(toutesLesStations);
        Set<Station> visitesPasse2 = new HashSet<>();
        dfsInverseStations(depart, grapheInverse, visitesPasse2);

        return visitesPasse2.size() == toutesLesStations.size();
    }

    // DFS itératif sur les stations à travers leurs quais et arêtes
    private static void dfsStations(Station depart, Set<Station> visites) {
        Deque<Station> pile = new ArrayDeque<>();
        pile.push(depart);

        while (!pile.isEmpty()) {
            Station courante = pile.pop();
            if (visites.contains(courante)) continue;
            visites.add(courante);

            // Pour chaque quai de la station actuelle, on regarde ses voisins
            for (Quai q : courante.getQuais()) {
                for (Arete a : q.getVoisins()) {
                    Station destination = a.getDestination().getStation();
                    if (destination != null && !visites.contains(destination)) {
                        pile.push(destination);
                    }
                }
            }
        }
    }

    // Construit un graphe inversé de stations (Station A -> Station B devient B -> A)
    private static Map<Station, List<Station>> construireGrapheInverseStations(Collection<Station> toutesLesStations) {
        Map<Station, List<Station>> inverse = new HashMap<>();
        for (Station s : toutesLesStations) {
            inverse.putIfAbsent(s, new ArrayList<>());
            for (Quai q : s.getQuais()) {
                for (Arete a : q.getVoisins()) {
                    Station destination = a.getDestination().getStation();
                    if (destination != null && !destination.equals(s)) {
                        inverse.computeIfAbsent(destination, k -> new ArrayList<>()).add(s);
                    }
                }
            }
        }
        return inverse;
    }

    // DFS itératif sur le graphe inversé des stations
    private static void dfsInverseStations(Station depart, Map<Station, List<Station>> grapheInverse, Set<Station> visites) {
        Deque<Station> pile = new ArrayDeque<>();
        pile.push(depart);

        while (!pile.isEmpty()) {
            Station courante = pile.pop();
            if (visites.contains(courante)) continue;
            visites.add(courante);

            for (Station voisin : grapheInverse.getOrDefault(courante, Collections.emptyList())) {
                if (!visites.contains(voisin)) {
                    pile.push(voisin);
                }
            }
        }
    }
}