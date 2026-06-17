package backend.gtfs;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Path;

public class ChargeurGTFS {

	public Map<String, RouteGTFS> lireRoutes(Path chemin){
		Map<String, RouteGTFS> routes = new HashMap<>();
		try (BufferedReader br = Files.newBufferedReader(chemin, StandardCharsets.UTF_8)) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return routes;
	}
}
