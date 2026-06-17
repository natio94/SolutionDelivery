package backend;

import backend.gtfs.ChargeurGTFS;
import backend.gtfs.RouteGTFS;

import java.nio.file.Path;
import java.util.Map;

public class Main {
	public static void main(String[] args) {
		ChargeurGTFS c = new ChargeurGTFS();
		Map<String, RouteGTFS> routes = c.lireRoutes(Path.of("Datas/routes.txt"));
	}
}
