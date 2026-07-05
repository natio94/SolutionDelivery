package backend.models;

import backend.models.Quai;

import java.util.*;

public class DistanceAntecedants {
	private Integer distance;
	private List<Quai> antecedantsQuai;
	private List<Arete> antecedantsArete;

	public DistanceAntecedants(Integer distance, Quai antecedantQuai, Arete antecedantArete) {
		this.distance = distance;
		this.antecedantsQuai = new ArrayList<>();
		this.antecedantsArete = new ArrayList<>();
		this.antecedantsQuai.add(antecedantQuai);
		this.antecedantsArete.add(antecedantArete);
	}
	public DistanceAntecedants(Integer distance, List<Quai> antecedantsQuai, List<Quai> antecedantsArete) {
		this.distance = distance;
		this.antecedantsQuai = new ArrayList(antecedantsQuai);
		this.antecedantsArete = new ArrayList(antecedantsArete);
	}

	public Integer getDistance() {
		return this.distance;
	}
	public List<Quai> getAntecedantsQuai() {
		return this.antecedantsQuai;
	}
	public List<Arete> getAntecedantsArete() {
		return this.antecedantsArete;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	public void setAntecedants(Quai antecedantQuai, Arete antecedantArete) {
		this.antecedantsQuai.clear();
		this.antecedantsArete.clear();
		this.antecedantsQuai.add(antecedantQuai);
		this.antecedantsArete.add(antecedantArete);
	}
}
