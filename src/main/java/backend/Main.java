package backend;

import backend.gtfs.ConstructeurGraphe;

public class Main {
	public static void main(String[] args) {
		ConstructeurGraphe cg = new ConstructeurGraphe();
		cg.buildGraph();
	}
}
