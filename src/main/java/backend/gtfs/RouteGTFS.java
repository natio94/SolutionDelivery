package backend.gtfs;

public record RouteGTFS(String id, String shortName, int type, String color) {
	public String getId(){ return this.id; }
}
