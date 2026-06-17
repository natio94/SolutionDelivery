package backend.gtfs;

record TransferGTFS(String fromStopId, String toStopId,
                    int type, int minTransferTime) {}