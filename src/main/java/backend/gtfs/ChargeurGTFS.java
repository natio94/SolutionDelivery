package backend.gtfs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Path;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class ChargeurGTFS {
	private static final int TYPE_METRO = 1;

	private String[] splitCsv(String line) {
		List<String> fields = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		boolean inQuotes = false;
		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);
			if (ch == '"') {
				inQuotes = !inQuotes;
			} else if (ch == ',' && !inQuotes) {
				fields.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(ch);
			}
		}
		fields.add(sb.toString());
		return fields.toArray(new String[0]);
	}

	public Map<String, RouteGTFS> lireRoutes(Path chemin){
		try {
			return lireRoutes(Files.newInputStream(chemin));
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture routes.txt impossible", e);
		}
	}

	public Map<String, RouteGTFS> lireRoutes(InputStream is){
		Map<String, RouteGTFS> routes = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				String[] c = splitCsv(line);
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
		try {
		return lireTrips(Files.newInputStream(chemin), routesMetro);}
		catch (IOException e) {
			throw new UncheckedIOException("Lecture trips.txt impossible", e);
		}
	}

	public Map<String, TripGTFS> lireTrips(InputStream is, Set<String> routesMetro) {
		Map<String, TripGTFS> trips = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))){
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null){
				if (firstLine) {
				firstLine = false;
				continue;
				}
				String[] c = splitCsv(line);
				if (routesMetro.contains(c[0])){
					trips.put(c[2], new TripGTFS(c[2], c[0], c[1], parseInt(c[5]), c[3], parseInt(c[8]) == 1, parseInt(c[9]) == 1));
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture trips.txt impossible", e);
		}
		return trips;
	}

	public Map<String, StopGTFS> lireStops(Path chemin) {
		try {
			return lireStops(Files.newInputStream(chemin));
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture stops.txt impossible", e);
		}
	}

	public Map<String, StopGTFS> lireStops(InputStream is) {
		Map<String, StopGTFS> stops = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				String[] c = splitCsv(line);
				int zoneId = c[6].trim().isEmpty() ? 0 : parseInt(c[6].trim());
				int locationType = c[8].trim().isEmpty() ? 0 : parseInt(c[8].trim());
				boolean wheelchair = c.length > 12 && !c[12].trim().isEmpty() && parseInt(c[12].trim()) == 1;
				stops.put(c[0], new StopGTFS(c[0], c[2], parseDouble(c[5]), parseDouble(c[4]), zoneId, locationType, c[9], wheelchair));
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture stops.txt impossible", e);
		}
		return stops;
	}

	public Map<String, List<StopTimeGTFS>> lireStopsTime(Path chemin) {
		try {
			return lireStopsTime(Files.newInputStream(chemin));
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture stop_times.txt impossible", e);
		}
	}

	public Map<String, List<StopTimeGTFS>> lireStopsTime(InputStream is) {
		Map<String, List<StopTimeGTFS>> stopsTime = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				String[] c = splitCsv(line);
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
		try{
			return lireTransfers(Files.newInputStream(chemin));
		}
		catch (IOException e) {
			throw new UncheckedIOException("Lecture transfers.txt impossible", e);
		}

	}

	public List<TransferGTFS> lireTransfers(InputStream is) {
		List<TransferGTFS> transferts = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			String line;
			boolean firstLine = true;
			while ((line = br.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				String[] c = splitCsv(line);
				int minTime = (c.length > 3 && !c[3].trim().isEmpty()) ? parseInt(c[3].trim()) : 0;
				transferts.add(new TransferGTFS(c[0], c[1], parseInt(c[2]), minTime));
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Lecture transfers.txt impossible", e);
		}
		return transferts;
	}
}