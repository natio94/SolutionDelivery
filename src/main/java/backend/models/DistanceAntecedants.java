package backend.models;

import backend.models.Quai;

import java.util.*;

public class DistanceAntecedants {
	private Integer distance;
	private List<Quai> antecedants;

	public DistanceAntecedants(Integer distance, Quai antecedant) {
		this.distance = distance;
		this.antecedants = new ArrayList();
		this.antecedants.add(antecedant);
	}
	public DistanceAntecedants(Integer distance, List<Quai> antecedants) {
		this.distance = distance;
		this.antecedants = new ArrayList(antecedants);
	}

	public Integer getDistance() {
		return this.distance;
	}
	public List<Quai> getAntecedants() {
		return this.antecedants;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	public void setAntecedants(Quai antecedant) {
		this.antecedants.clear();
		this.antecedants.add(antecedant);
	}
}
