package backend.gtfs;

public record StopGTFS(String stopId, String name, double lat, double lon, int zoneId,
                       int locationType, String parentStation, boolean wheelchair) {
}