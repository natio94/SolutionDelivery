package frontend.ui.controllers;

import backend.Service;
import backend.models.*;
import frontend.ui.views.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class GraphController {

	@FXML
	private Pane graphPane;

	@FXML
	private Pane viewportPane;

	@FXML
	private ChoiceBox<String> lineChoice;

	private static final double VIEWPORT_WIDTH = 1000;
	private static final double VIEWPORT_HEIGHT = 800;
	private static final double CANVAS_SCALE_FACTOR = 3.0;
	private static final double PANE_WIDTH = VIEWPORT_WIDTH * CANVAS_SCALE_FACTOR;
	private static final double PANE_HEIGHT = VIEWPORT_HEIGHT * CANVAS_SCALE_FACTOR;
	private static final double PADDING = 60;
	private static final double ZOOM_TRESHOLD =1.5;


	private Service service = new Service();
	private final Map<String, StationView> stationNodes = new HashMap<>();
	private final Map<String,String> lineNameToId = new HashMap<>();
	private final Map<String, AreteView> arreteViews = new HashMap<>();
	private PanZoomHandler	panZoomHandler;
	private Graphe graphe;


	public void initialize() {

		graphPane.setPrefSize(PANE_WIDTH, PANE_HEIGHT);
		graphPane.setMinSize(PANE_WIDTH,PANE_HEIGHT);
		graphPane.setStyle("-fx-background-color: #F0F0F0; -fx-border-color: black; -fx-border-width: 1px;");

		viewportPane.setPrefSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		viewportPane.setMinSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		viewportPane.setMaxSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		Rectangle clip = new Rectangle(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		viewportPane.setClip(clip);

		this.graphe= service.getGraphe();
		panZoomHandler = new PanZoomHandler(graphPane);
		panZoomHandler.addZoomListener(this::onZoomChanged);
		renderGraphe(this.graphe);

		double initialZoom = VIEWPORT_WIDTH / PANE_WIDTH;
		panZoomHandler.setZoomFactor(initialZoom);


		graphe.getLignes().forEach((ligne) -> lineNameToId.put(ligne.getNom(), ligne.getId()));

		lineChoice.getItems().add("Aucune lignes");
		lineChoice.setValue("Aucune lignes");
		graphe.getLignes().stream()
				.sorted(Comparator.comparing(Ligne::getId))
				.forEach(ligne -> lineChoice.getItems().add(ligne.getNom()));
		lineChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {highlightLine(lineNameToId.get(newValue));});

		//highlightLine("IDFM:C01377");

	}

	private void onZoomChanged(double zoomFactor) {
		System.out.println(	"Zoom factor: " + zoomFactor);
		if (zoomFactor < ZOOM_TRESHOLD) {
			stationNodes.values().forEach(node -> node.setLabelVisible(false));
		} else {
			stationNodes.values().forEach(node -> node.setLabelVisible(true));
		}

	}

	public void addSommet(Station station, double x, double y) {
		StationView node = new StationView(station, x, y);
		node.setOnStationClicked(this::handlePopup);

		stationNodes.put(station.getId(), node);
		graphPane.getChildren().add(node);
	}

	private void addArete(Arete arete) {
		Quai source = arete.getSource();
		Quai destination = arete.getDestination();
		StationView nodeA = stationNodes.get(source.getStation().getId());
		StationView nodeB = stationNodes.get(destination.getStation().getId());

		if (nodeA == null || nodeB == null) {
			return;
		}

		AreteView arrete;
		if (arete.getType()	!= Arete.Type.pied)
			{
			arrete = new AreteView(
					arete,
					nodeA.getCenterX(), nodeA.getCenterY(),
					nodeB.getCenterX(), nodeB.getCenterY(),
					arete.getLigne()
			);

				String key = arreteKey(source.getId(), destination.getId());
				arreteViews.put(key, arrete);
				graphPane.getChildren().add(0, arrete);
		}

	}

	private void handlePopup(StationView node) {
		StationPopup popup = node.getPopup();
		System.out.println(popup);
		if(node.getPopup() == null || !node.getPopup().isVisible()) {
			System.out.println("Clic sur : " + node.getStation().getNom());
			if (popup == null) {
				popup = new StationPopup(node.getStation(),node.getCenterX(), node.getCenterY());

				graphPane.getChildren().add(popup);

			}
			popup.setVisible(true);
		}
		else{
			if (popup != null) {
				popup.setVisible(false);
			}
		}
		node.setPopup(popup);
		System.out.println(node.getPopup());
	}

	public void renderGraphe(Graphe graphe) {
		graphPane.getChildren().clear();
		stationNodes.clear();
		arreteViews.clear();

		GeoProjector projector = GeoProjector.fitTo(
				graphe.getStations(), PANE_WIDTH, PANE_HEIGHT, PADDING
		);

		for (Station station : graphe.getStations()) {
			double[] pos = projector.project(station.getLatitude(), station.getLongitude());
			addSommet(station, pos[0], pos[1]);
		}

		for (Arete arete : graphe.getAretes()) {
			addArete(arete);
		}
	}


	public void highlightPath(List<String> quaiIdsOrdonnes) {
		stationNodes.values().forEach(n -> n.setSelected(false));
		arreteViews.values().forEach(e -> e.setHighlighted(false));
		for (int i = 0; i < quaiIdsOrdonnes.size(); i++) {
			String quaiId = quaiIdsOrdonnes.get(i);
			Quai quai = graphe.getQuai(quaiId);
			if (quai == null) continue;

			StationView node = stationNodes.get(quai.getStation().getId());
			if (node != null) node.setSelected(true);

			if (i > 0) {
				String prevQuaiId = quaiIdsOrdonnes.get(i - 1);
				AreteView arrete = arreteViews.get(arreteKey(prevQuaiId, quaiId));
				if (arrete == null) {
					arrete = arreteViews.get(arreteKey(quaiId, prevQuaiId));
				}
				if (arrete != null) arrete.setHighlighted(true);
			}
		}
	}

	private String arreteKey(String sourceQuaiId, String destQuaiId) {
		return sourceQuaiId + "->" + destQuaiId;
	}


	public void highlightLine(String line) {
		stationNodes.values().forEach(n -> n.setSelected(false));
		arreteViews.values().forEach(e -> e.setHighlighted(e.getArete().getLigne() != null && e.getArete().getLigne().getId().equals(line)));

	}

}