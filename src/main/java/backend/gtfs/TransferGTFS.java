package backend.gtfs;

public record TransferGTFS(String fromStopId, String toStopId,
                    int type, int minTransferTime) {}