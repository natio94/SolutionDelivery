package frontend.ui.views;
import backend.models.Station;

import java.util.Collection;

public class GeoProjector {

	private final double minLat, maxLat, minLon, maxLon;
	private final double paneWidth, paneHeight;
	private final double padding;
	private final double lonCorrectionFactor;

	public GeoProjector(double minLat, double maxLat, double minLon, double maxLon,
						double paneWidth, double paneHeight, double padding) {
		this.minLat = minLat;
		this.maxLat = maxLat;
		this.minLon = minLon;
		this.maxLon = maxLon;
		this.paneWidth = paneWidth;
		this.paneHeight = paneHeight;
		this.padding = padding;

		this.lonCorrectionFactor = Math.cos(Math.toRadians((minLat + maxLat) / 2.0));
	}

	public static GeoProjector fitTo(Collection<Station> stations,
									 double paneWidth, double paneHeight,
									 double padding) {
		double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
		double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

		for (Station s : stations) {
			minLat = Math.min(minLat, s.getLatitude());
			maxLat = Math.max(maxLat, s.getLatitude());
			minLon = Math.min(minLon, s.getLongitude());
			maxLon = Math.max(maxLon, s.getLongitude());
		}

		return new GeoProjector(minLat, maxLat, minLon, maxLon, paneWidth, paneHeight, padding);
	}


	public double[] project(double lat, double lon) {
		double usableWidth = (paneWidth - 2 * padding);
		double usableHeight = (paneHeight - 2 * padding);

		double lonRange = (maxLon - minLon);
		double latRange = (maxLat - minLat);

		double normX = lonRange == 0 ? 0.5 : (lon - minLon) / lonRange;
		double normY = latRange == 0 ? 0.5 : (lat - minLat) / latRange;

		double x = padding + normX * usableWidth * lonCorrectionFactor
				+ (usableWidth * (1 - lonCorrectionFactor) / 2.0);

		double y = padding + (1 - normY) * usableHeight;

		return new double[]{x, y};
	}
}