package backend.algo;

import backend.models.*;

import java.util.*;

public class MeilleurChemin {
	public static List<Quai> MeilleurChemin(Map<Quai, DistanceAntecedants> distanceAntecedantsMap, Quai origin, Quai destination) {
		List<Quai> path = new ArrayList<>();
		Quai current_quai = destination;
		while (current_quai != origin) {
			path.add(current_quai);
			current_quai = distanceAntecedantsMap.get(current_quai).getAntecedants().get(0);
		}
		path.add(current_quai);
		Collections.reverse(path);
		return path;
	}

	public static List<Quai> MeilleurChemin(Map<Quai, DistanceAntecedants> distanceAntecedantsMap, Station origin, Station destination) {
		List<Quai> path = MeilleurChemin(distanceAntecedantsMap, origin.getQuais().get(0), destination.getQuais().get(0));

		Quai previous_quai, current_quai, next_quai;
		// eliminate the walks between Quai in the starting station
		if (path.size() > 1) {
			current_quai = path.get(0);
			next_quai = path.get(1);
			while (path.size() > 1 && current_quai.getStation().equals(next_quai.getStation())) {
				path.remove(current_quai);
				current_quai = path.get(0);
				if (path.size() > 1) {
					next_quai = path.get(1);
				}
			}
		}
		// eliminate the walks between Quai in the ending station
		if (path.size() > 1) {
			current_quai = path.get(path.size()-1);
			previous_quai = path.get(path.size()-2);
			while (path.size() > 1 && current_quai.getStation().equals(previous_quai.getStation())) {
				path.remove(current_quai);
				current_quai = path.get(path.size()-1);
				if (path.size() > 1) {
					previous_quai = path.get(path.size()-2);
				}
			}
		}

		return path;
	}
}
