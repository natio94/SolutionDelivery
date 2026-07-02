package frontend.ui.views;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.List;


public class PanZoomHandler {

	private final Pane pane;

	private final Translate translate = new Translate(0, 0);
	private final Scale scale = new Scale(1, 1, 0, 0);

	private double zoomFactor = 1.0;

	private double lastScreenX = 0.0;
	private double lastScreenY = 0.0;

	private static final double ZOOM_MIN = 0.25;
	private static final double ZOOM_MAX = 5.0;
	private static final double ZOOM_DELTA = 1.1;

	private final List<ZoomListener> zoomListeners = new ArrayList<>();

	public interface ZoomListener {
		void onZoomChanged(double zoomFactor);
	}

	public PanZoomHandler(Pane pane) {
		this.pane = pane;
		pane.getTransforms().addAll(scale, translate);
		attachHandlers();
	}

	private void attachHandlers() {
		pane.setOnScroll(this::handleScroll);
		pane.setOnMousePressed(this::handleMousePressed);
		pane.setOnMouseDragged(this::handleMouseDragged);
		//pane.setOn
	}

	private void handleScroll(ScrollEvent event) {
		event.consume();
		double localX = event.getX();
		double localY = event.getY();
		double screenX = localX * scale.getX() + translate.getX();
		double screenY = localY * scale.getY() + translate.getY();

		double oldZoom = zoomFactor;
		double newZoom = zoomFactor * ((event.getDeltaY() > 0) ? ZOOM_DELTA : (1.0 / ZOOM_DELTA));
		newZoom = Math.max(ZOOM_MIN, Math.min(ZOOM_MAX, newZoom));

		if (newZoom == oldZoom) return;

		zoomFactor = newZoom;
		scale.setX(zoomFactor);
		scale.setY(zoomFactor);

		translate.setX(screenX - localX * zoomFactor);
		translate.setY(screenY - localY * zoomFactor);

		notifyZoomListeners();
	}

	private void handleMousePressed(MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {
			event.consume();
			lastScreenX = event.getScreenX();
			lastScreenY = event.getScreenY();
		}
		if (event.getButton()==MouseButton.MIDDLE){
			reset();
		}
	}

	private void handleMouseDragged(MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {
			event.consume();

			double deltaX = event.getScreenX() - lastScreenX;
			double deltaY = event.getScreenY() - lastScreenY;

			translate.setX(translate.getX() + deltaX);
			translate.setY(translate.getY() + deltaY);

			lastScreenX = event.getScreenX();
			lastScreenY = event.getScreenY();
		}
	}

	private void notifyZoomListeners() {
		for (ZoomListener listener : zoomListeners) {
			listener.onZoomChanged(zoomFactor);
		}
	}

	public void addZoomListener(ZoomListener listener) {
		zoomListeners.add(listener);
	}

	public void removeZoomListener(ZoomListener listener) {
		zoomListeners.remove(listener);
	}

	public void reset() {
		zoomFactor = 1.0/3.0;
		scale.setX(zoomFactor);
		scale.setY(zoomFactor);
		translate.setX(0.0);
		translate.setY(0.0);
		notifyZoomListeners();
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(double zoom) {
		this.zoomFactor = Math.max(ZOOM_MIN, Math.min(ZOOM_MAX, zoom));
		scale.setX(this.zoomFactor);
		scale.setY(this.zoomFactor);
	}
}