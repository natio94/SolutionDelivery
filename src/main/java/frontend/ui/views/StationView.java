package frontend.ui.views;

import backend.models.Station;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.function.Consumer;

public class StationView extends StackPane {
	private final Station station;
	private final Circle circle;
	private final Label label;
	private Consumer<StationView> onClickHandler;
	private boolean selected = false;
	private static final double RADIUS = 12.0;
	private static final Color DEFAULT_COLOR = Color.web("#E10613");
	private static final Color HOVER_COLOR = Color.web("#FF8C8C");
	private static final Color SELECTED_COLOR = Color.GOLD;
	private StationPopup popup;


	public StationView(Station station, double x, double y) {
		this.station = station;

		circle = new Circle(RADIUS);
		circle.setFill(DEFAULT_COLOR);
		circle.setStroke(Color.BLACK);

		label = new Label(station.getNom());
		label.setMouseTransparent(true);
		label.setTranslateY(RADIUS + 14);

		getChildren().addAll(circle, label);

		setLayoutX(x - RADIUS);
		setLayoutY(y - RADIUS);
		int nbLignes = station.getQuais().size();
		Tooltip tooltip = new Tooltip(station.getNom() + " (" + nbLignes + " ligne(s))");
		Tooltip.install(this, tooltip);
		setupInteractions();

	}

	private void setupInteractions() {
		setOnMouseEntered(e -> {
			if (!selected) circle.setFill(HOVER_COLOR);
			setCursor(Cursor.HAND);
		});

		setOnMouseExited(e -> {
			if (!selected) circle.setFill(DEFAULT_COLOR);
			setCursor(Cursor.DEFAULT);
		});

		setOnMouseClicked(e -> {
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
