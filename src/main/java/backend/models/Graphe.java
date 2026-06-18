package backend.models;

import java.util.*;

public class Graphe {
    private final Map<String, Quai> quais = new HashMap<>();
    private final Map<String, Station> stations = new HashMap<>();
    private final Map<String, Ligne> lignes = new HashMap<>();
    private final List<Arete> aretes = new ArrayList<>();


    public void addQuai(Quai q)       { quais.put(q.getId(), q); }
    public void addStation(Station s) { stations.put(s.getId(), s); }
    public void addLigne(Ligne l)     { lignes.put(l.getId(), l); }
    public void addArete(Arete a)     { aretes.add(a); }

    public Quai getQuai(String id)        { return quais.get(id); }
    public Station getStation(String id)  { return stations.get(id); }
    public Ligne getLigne(String id)      { return lignes.get(id); }

    public Collection<Quai> getQuais()       { return quais.values(); }
    public Collection<Station> getStations() { return stations.values(); }
    public Collection<Ligne> getLignes()     { return lignes.values(); }
    public List<Arete> getAretes()           { return aretes; }
}
