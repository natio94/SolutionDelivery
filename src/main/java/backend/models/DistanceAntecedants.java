package backend.models;

import backend.models.Quai;

import java.util.*;

public class DistanceAntecedants {
	private Double distance;
	private List<Quai> antecedantsQuai;
	private List<Arete> antecedantsArete;

	public DistanceAntecedants(Double distance, Quai antecedantQuai, Arete antecedantArete) {
		this.distance = distance;
		this.antecedantsQuai = new ArrayList<>();
		this.antecedantsArete = new ArrayList<>();
		this.antecedantsQuai.add(antecedantQuai);
		this.antecedantsArete.add(antecedantArete);
	}
	public DistanceAntecedants(Double distance, List<Quai> antecedantsQuai, List<Quai> antecedantsArete) {
		this.distance = distance;
		this.antecedantsQuai = new ArrayList(antecedantsQuai);
		this.antecedantsArete = new ArrayList(antecedantsArete);
	}

	public Double getDistance() {
		return this.distance;
	}
	public List<Quai> getAntecedantsQuai() {
		return this.antecedantsQuai;
	}
	public List<Arete> getAntecedantsArete() {
		return this.antecedantsArete;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public void setAntecedants(Quai antecedantQuai, Arete antecedantArete) {
		this.antecedantsQuai.clear();
		this.antecedantsArete.clear();
		this.antecedantsQuai.add(antecedantQuai);
		this.antecedantsArete.add(antecedantArete);
	}
}
