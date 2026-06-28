package backend;

public class Main {
    public static void main(String[] args) {
        System.out.println("Chargement du graphe...");
        Service service = new Service();

        System.out.println("Stations chargées : " + service.getGraphe().getStations().size());
        System.out.println("Quais chargés     : " + service.getGraphe().getQuais().size());
        System.out.println("Aretes chargées   : " + service.getGraphe().getAretes().size());

        System.out.println("Réseau connexe (quais)    : " + service.estConnexe());
        System.out.println("Réseau connexe (stations) : " + service.estConnexeStations());

    }
}