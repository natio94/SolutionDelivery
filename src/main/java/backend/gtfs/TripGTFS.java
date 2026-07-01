package backend.gtfs;

public record TripGTFS(String tripId, String routeId, String serviceId,
                       int directionId, String headsign, boolean wheelchair, boolean bike) {
}