package backend.algo;

import backend.models.Arete;
import backend.models.Graphe;
import backend.models.Quai;
import backend.models.Station;

import java.util.*;

public class Kruskal {

    private static class UnionFind {
        private final Map<String, String> parent = new HashMap<>();
        private final Map<String, Integer> rang = new HashMap<>();

        UnionFind(Collection<String> ids) {
            for (String id : ids) {
                parent.put(id, id);
                rang.put(id, 0);
            }
        }

        String find(String id) {
            String p = parent.get(id);
            if (!p.equals(id)) {
                p = find(p);
                parent.put(id, p);
            }
            return p;
        }

        /** Fusionne les deux ensembles. Renvoie false s'ils étaient DÉJÀ reliés. */
        boolean union(String a, String b) {
            String ra = find(a), rb = find(b);
            if (ra.equals(rb)) return false;
            if (rang.get(ra) < rang.get(rb)) { String t = ra; ra = rb; rb = t; }
            parent.put(rb, ra);
            if (rang.get(ra).equals(rang.get(rb))) rang.put(ra, rang.get(ra) + 1);
            return true;
        }
    }


    public Graphe getACPM(Graphe g) {
        Graphe acpm = new Graphe();
        for (Quai q : g.getQuais()) {
            acpm.addQuai(q);
            if (q.getStation() != null) acpm.addStation(q.getStation());
            if (q.getLigne()   != null) acpm.addLigne(q.getLigne());
        }

        List<Arete> aretesTriees = g.getAretes().stream()
                .sorted(Comparator.comparingDouble(Arete::getPoid))
                .toList();

        // Union-Find sur les IDs de stations pour éviter de garder les deux sens d'une même ligne
        Set<String> stationIds = new HashSet<>();
        for (Quai q : g.getQuais()) {
            if (q.getStation() != null) stationIds.add(q.getStation().getId());
        }
        UnionFind uf = new UnionFind(stationIds);

        for (Arete a : aretesTriees) {
            Station src = a.getSource().getStation();
            Station dst = a.getDestination().getStation();
            if (src == null || dst == null || src.getId().equals(dst.getId())) continue;
            if (uf.union(src.getId(), dst.getId())) {
                acpm.addArete(a);
            }
        }

        return acpm;
    }
}
