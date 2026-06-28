package frontend.ui.views;

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
	private static final Color DEFAULT_COLOR = Color.web("#E10613",0.5);
	private static final Color HOVER_COLOR = Color.web("#FF8C8C",0.5);
	private static final Color SELECTED_COLOR = Color.GOLD;
	private StationPopup popup;
	private boolean labelVisibleByZoom;

	public StationView(Station station, double x, double y) {
		this.station = station;


		circle = new Circle(RADIUS);
		circle.setFill(DEFAULT_COLOR);
		circle.setStroke(Color.web("000000",0.5));
		circle.setCenterX(RADIUS);
		circle.setCenterY(RADIUS);

		label = new Label(station.getNom());
		label.setMouseTransparent(true);
		label.setLayoutX(RADIUS * 2 + 8);
		label.setLayoutY(RADIUS - 10);
		label.setVisible(false);
		getChildren().addAll(circle, label);

		setPrefSize(200, RADIUS * 2 + 10);
		setLayoutX(x - RADIUS);
		setLayoutY(y - RADIUS);

		int nbLignes = station.getQuais().size();
		Tooltip tooltip = new Tooltip(station.getNom() + " (" + nbLignes + " ligne(s))");
		Tooltip.install(circle, tooltip);

		setupInteractions();
	}

	private void setupInteractions() {
		circle.setOnMouseEntered(e -> {
			if (!selected) circle.setFill(HOVER_COLOR);
			setCursor(Cursor.HAND);
			label.setVisible(true);
		});

		circle.setOnMouseExited(e -> {
			if (!selected) circle.setFill(DEFAULT_COLOR);
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
		circle.setFill(selected ? SELECTED_COLOR : DEFAULT_COLOR);
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
}