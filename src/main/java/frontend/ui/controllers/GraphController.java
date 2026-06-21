package frontend.ui.controllers;

import backend.models.*;
import frontend.ui.views.ArreteView;
import frontend.ui.views.GeoProjector;
import frontend.ui.views.StationPopup;
import frontend.ui.views.StationView;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphController {

	@FXML
	private Pane graphPane;

	private static final double PANE_WIDTH = 800;
	private static final double PANE_HEIGHT = 600;
	private static final double PADDING = 60;

	private final Map<String, StationView> stationNodes = new HashMap<>();


	private final Map<String, ArreteView> arreteViews = new HashMap<>();

	private Graphe graphe;


	public void initialize() {
		graphPane.setPrefSize(PANE_WIDTH, PANE_HEIGHT);
		this.graphe = buildSampleGraphe();

		renderGraphe(this.graphe);
		highlightPath(List.of("Q1", "Q2"));
	}

	public void addSommet(Station station, double x, double y) {
		StationView node = new StationView(station, x, y);
		node.setOnStationClicked(this::handlePopup);
		stationNodes.put(station.getId(), node);
		graphPane.getChildren().add(node);
	}

	private void addArrete(Arete arete) {
		Quai source = arete.getSource();
		Quai destination = arete.getDestination();
		StationView nodeA = stationNodes.get(source.getStation().getId());
		StationView nodeB = stationNodes.get(destination.getStation().getId());

		if (nodeA == null || nodeB == null) {
			return;
		}

		ArreteView arrete;
		if (arete.getLigne()==null) {
			arrete= new ArreteView(
					arete,
					nodeA.getCenterX(), nodeA.getCenterY(),
					nodeB.getCenterX(), nodeB.getCenterY()
			);

		}else{
			arrete = new ArreteView(
					arete,
					nodeA.getCenterX(), nodeA.getCenterY(),
					nodeB.getCenterX(), nodeB.getCenterY(),
					arete.getLigne()
			);
		}

		String key = arreteKey(source.getId(), destination.getId());
		arreteViews.put(key, arrete);
		graphPane.getChildren().add(0, arrete);
	}

	private void handlePopup(StationView node) {
		StationPopup popup = node.getPopup();
		if(node.getPopup() == null || !node.getPopup().isVisible()) {
			System.out.println("Clic sur : " + node.getStation().getNom());
			if (popup == null) {
				popup = new StationPopup(node.getStation(),node.getCenterX(), node.getCenterY());
			}
			System.out.println(popup.isVisible());
			popup.setVisible(true);
			graphPane.getChildren().add(popup);
		}
		else{
			if (popup != null) {
				popup.setVisible(false);
			}
		}
		node.setPopup(popup);
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
			addArrete(arete);
		}
	}

	private Graphe buildSampleGraphe() {
		Graphe g = new Graphe();

		Ligne ligne1 = new Ligne("L1", "Ligne 1", "#FFCD00");
		Ligne ligne2 = new Ligne("L2", "Ligne 2", "#00ADEF");
		g.addLigne(ligne1);
		g.addLigne(ligne2);

		Station chatelet = new Station("S1", "Châtelet", 2.3470, 48.8583);
		Station bastille  = new Station("S2", "Bastille", 2.3691, 48.8531);
		Station nation    = new Station("S3", "Nation", 2.3958, 48.8484);
		Station test = new Station("S4", "Test", 2.4030, 48.8350);

		g.addStation(chatelet);
		g.addStation(bastille);
		g.addStation(nation);
		g.addStation(test);

		Quai qChatelet = new Quai("Q1", ligne1, chatelet);
		Quai qBastille = new Quai("Q2", ligne1, bastille);
		Quai qBastille2 = new Quai("Q3", ligne2, bastille);
		Quai qNation   = new Quai("Q4", ligne1, nation);
		Quai qTest   = new Quai("Q5", ligne2, test);

		chatelet.addQuai(qChatelet);
		bastille.addQuai(qBastille);
		bastille.addQuai(qBastille2);
		nation.addQuai(qNation);
		test.addQuai(qTest);

		g.addQuai(qChatelet);
		g.addQuai(qBastille);
		g.addQuai(qBastille2);
		g.addQuai(qNation);
		g.addQuai(qTest);

		Arete a1 = new Arete(qChatelet, qBastille, 3, ligne1, Arete.Type.metro);
		Arete a2 = new Arete(qBastille, qNation, 4, ligne1, Arete.Type.metro);
		Arete a3 = new Arete(qBastille, qBastille2, 2, null, Arete.Type.pied);
		Arete a4 = new Arete(qBastille2, qTest, 5, ligne2, Arete.Type.metro);

		g.addArete(a1);
		g.addArete(a2);
		g.addArete(a3);
		g.addArete(a4);

		return g;
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
				ArreteView arrete = arreteViews.get(arreteKey(prevQuaiId, quaiId));
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


}
