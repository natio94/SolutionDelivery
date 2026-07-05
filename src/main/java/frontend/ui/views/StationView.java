package frontend.ui.views;

import backend.models.Quai;
import backend.models.Station;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.function.Consumer;

public class StationView extends Pane {
	private final Station station;
	private final Circle circle;
	private final Label label;
	private Consumer<StationView> onClickHandler;
	private boolean selected = false;
	private static final double RADIUS = 3.0;
	private static final Color DEFAULT_COLOR = Color.web("#e6ed0e", 0.5);
	private static final Color SELECTED_COLOR = Color.GOLD;
	private Color color = DEFAULT_COLOR;
	private StationPopup popup;
	private boolean labelVisibleByZoom = false;
	private boolean start = false;
	private boolean end = false;

	public StationView(Station station, double x, double y) {
		this.station = station;
		circle = new Circle(RADIUS);
		circle.setFill(DEFAULT_COLOR);
		circle.setStroke(Color.web("000000", 0.5));
		circle.setCenterX(RADIUS);
		circle.setCenterY(RADIUS);
		label = new Label(station.getNom());
		label.setMouseTransparent(true);
		label.setLayoutX(RADIUS * 2 + 4);
		label.setLayoutY(-6);
		label.setVisible(false);

		getChildren().addAll(circle, label);

		setPrefSize(RADIUS * 2, RADIUS * 2);
		setLayoutX(x - RADIUS);
		setLayoutY(y - RADIUS);

		int nbLignes = station.getQuais().stream().map(Quai::getLigne).distinct().toList().size();
		Tooltip tooltip = new Tooltip(station.getNom() + " (" + nbLignes + " ligne(s))");
		Tooltip.install(circle, tooltip);

		setupInteractions();
	}

	private void setupInteractions() {
		circle.setOnMouseEntered(e -> {
			if (!selected) circle.setFill(lightenColor(color));
			setCursor(Cursor.HAND);
			label.setVisible(true);
		});

		circle.setOnMouseExited(e -> {
			if (!selected) circle.setFill(color);
			setCursor(Cursor.DEFAULT);
			label.setVisible(labelVisibleByZoom);
		});

		circle.setOnMouseClicked(e -> {
			if (onClickHandler != null) {
				onClickHandler.accept(this);
			}
		});
	}

	public double getCenterX() {
		return getLayoutX() + RADIUS;
	}

	public double getCenterY() {
		return getLayoutY() + RADIUS;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		circle.setFill(selected ? SELECTED_COLOR : color);
	}

	public void setLabelVisible(boolean visible) {
		this.labelVisibleByZoom = visible;
		label.setVisible(visible);
	}

	public boolean isSelected() {
		return selected;
	}

	public Station getStation() {
		return station;
	}

	public void setOnStationClicked(Consumer<StationView> handler) {
		this.onClickHandler = handler;
	}

	public StationPopup getPopup() {
		return popup;
	}

	public void setPopup(StationPopup popup) {
		this.popup = popup;
	}

	public void setStart(boolean start) {
		this.start = start;
		color = start ? Color.GREEN : DEFAULT_COLOR;
		circle.setFill(color);
		circle.setRadius(start ? RADIUS * 1.5 : RADIUS);
	}

	public void setEnd(boolean end) {
		this.end = end;
		color = end ? Color.RED : DEFAULT_COLOR;
		circle.setFill(color);
		circle.setRadius(end ? RADIUS * 1.5 : RADIUS);
	}

	public void setCorresp(String corresp){
		if(popup!=null) {
			if (!corresp.isBlank()) {
				popup.setCorresp(corresp);
				circle.setStrokeWidth(2);
				circle.setStroke(Color.web("000000"));
				popup.setVisible(true);
			} else {
				circle.setStrokeWidth(1);
				circle.setStroke(Color.web("000000", 0.5));
				popup.setVisible(false);
				popup.setCorresp("");
			}
		}

	}
	private Color lightenColor(Color c) {
		return c.interpolate(Color.WHITE, 0.4);
	}
}