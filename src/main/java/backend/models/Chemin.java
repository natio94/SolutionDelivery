package backend.models;

import java.util.*;

public record Chemin(
		List<Quai> cheminQuai,
		List<Arete> cheminArete,
		int poid // pour MeilleurCheminTemps, c'est le temps de trajet, pour MeilleurCheminCorrespondances, c'est le nombre de correspondances, pour MeilleurCheminCO2, c'est l'équivalent CO2 du trajet en grammes
		) {}
