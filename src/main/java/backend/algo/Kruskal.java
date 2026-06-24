package backend.algo;

import backend.models.Arete;
import backend.models.Graphe;
import backend.models.Quai;

import java.util.*;

public class Kruskal {

    private static class UnionFind {
        private final Map<Quai, Quai> parent = new HashMap<>();
        private final Map<Quai, Integer> rang = new HashMap<>();

        UnionFind(Collection<Quai> quais) {
            for (Quai q : quais) {
                parent.put(q, q);   // au départ, chaque quai est son propre représentant
                rang.put(q, 0);
            }
        }

        Quai find(Quai q) {
            Quai p = parent.get(q);
            if (!p.equals(q)) {
                p = find(p);
                parent.put(q, p);   // compression de chemin (optimisation)
            }
            return p;
        }

        /** Fusionne les deux ensembles. Renvoie false s'ils étaient DÉJÀ reliés. */
        boolean union(Quai a, Quai b) {
            Quai ra = find(a), rb = find(b);
            if (ra.equals(rb)) return false;          // même composante -> cycle
            if (rang.get(ra) < rang.get(rb)) { Quai t = ra; ra = rb; rb = t; }
            parent.put(rb, ra);
            if (rang.get(ra).equals(rang.get(rb))) rang.put(ra, rang.get(ra) + 1);
            return true;
        }
    }


    public Graphe getACPM(Graphe g) {
        Graphe acpm = new Graphe();     // Crée un nouveau Graph
        for (Quai q : g.getQuais()) {   // Remet tous les anciens quais dedans vu qu'un ACPM possèdes tous les noeuds
            acpm.addQuai(q);
        }

        // Trie toutes les aretes par leur poid
        List<Arete> aretesTriees = g.getAretes().stream()
                .sorted(Comparator.comparingInt(Arete::getPoid))
                .toList();

        UnionFind uf = new UnionFind(g.getQuais());    // On crée la classe qui va nous permettre de faire des groupes, des parents et de regrouper les groupes

        // On garde une arete seulement si elle elle ne cré pas de boucle
        for (Arete a : aretesTriees) {
            if (uf.union(a.getSource(), a.getDestination())) {
                acpm.addArete(a);
            }
        }

        return acpm;
    }
}