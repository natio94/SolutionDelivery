package backend.gtfs;

public record StopTimeGTFS(String tripId, String stopId, String time, int stop_sequence) {
	public String getId(){ return this.tripId; }
}