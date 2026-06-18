package backend.gtfs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Path;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class ChargeurGTFS {
	private static final int TYPE_METRO = 1;

	public Map<String, RouteGTFS> lireRoutes(Path chemin){
		Map<String, RouteGTFS> routes = new HashMap<>();
		try (BufferedReader br = Files.newBufferedReader(chemin, StandardCharsets.UTF_8)) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				String[] c = line.split(",");
				if (parseInt(c[5]) == TYPE_METRO){
					routes.put(c[0], new RouteGTFS(c[0], c[2], parseInt(c[5]), c[7]));
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture routes.txt impossible", e);
		}
		return routes;
	}

	public Map<String, TripGTFS> lireTrips(Path chemin, Set<String> routesMetro) {
		Map<String, TripGTFS> trips = new HashMap<>();
		try (BufferedReader br = Files.newBufferedReader(chemin, StandardCharsets.UTF_8)){
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null){
				if (firstLine) {
				firstLine = false;
				continue;
				}
				String[] c = line.split(",");
				if (routesMetro.contains(c[0])){
					trips.put(c[2], new TripGTFS(c[2], c[0], c[1], parseInt(c[5]), c[3], parseBoolean(c[8]), parseBoolean(c[9])));
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture trips.txt impossible", e);
		}
		return trips;
	}

	public Map<String, StopGTFS> lireStops(Path chemin) {
		Map<String, StopGTFS> stops = new HashMap<>();
		try (BufferedReader br = Files.newBufferedReader(chemin, StandardCharsets.UTF_8)) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				String[] c = line.split(",");
				stops.put(c[0], new StopGTFS(c[0], c[2], parseDouble(c[4]), parseDouble(c[5]), parseInt(c[6]), parseInt(c[8]), c[9], parseBoolean(c[10])));
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture stops.txt impossible", e);
		}
		return stops;
	}

	public Map<String, List<StopTimeGTFS>> lireStopsTime(Path chemin) {
		Map<String, List<StopTimeGTFS>> stopsTime = new HashMap<>();
		try (BufferedReader br = Files.newBufferedReader(chemin, StandardCharsets.UTF_8)) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				String[] c = line.split(",");
				StopTimeGTFS st = new StopTimeGTFS(c[0], c[3], c[1], parseInt(c[4]));
				stopsTime.computeIfAbsent(c[0], k -> new ArrayList<>()).add(st);
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture stop_times.txt impossible", e);
		}
		for (List<StopTimeGTFS> liste : stopsTime.values()) {
			liste.sort(Comparator.comparingInt(StopTimeGTFS::stop_sequence));
		}
		return stopsTime;
	}

	public List<TransferGTFS> lireTransfers(Path chemin) {
		List<TransferGTFS> transferts = new ArrayList<>();
		try (BufferedReader br = Files.newBufferedReader(chemin, StandardCharsets.UTF_8)) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				String[] c = line.split(",");
				transferts.add(new TransferGTFS(c[0], c[1], parseInt(c[2]), parseInt(c[3])));
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture transfers.txt impossible", e);
		}
		return transferts;
	}
}
