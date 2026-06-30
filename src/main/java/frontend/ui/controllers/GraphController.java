package frontend.ui.controllers;

import backend.Service;
import backend.models.*;
import frontend.ui.views.*;
<<<<<<< Updated upstream
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

=======
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
>>>>>>> Stashed changes
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
<<<<<<< Updated upstream
import java.util.function.BiFunction;
=======
import java.util.stream.Collectors;
>>>>>>> Stashed changes

public class GraphController {

	@FXML
	private Pane graphPane;

	@FXML
	private Pane viewportPane;

	@FXML
	private ChoiceBox<String> lineChoice;

<<<<<<< Updated upstream
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
=======
	@FXML
	private TextField departField;

	@FXML
	private TextField arriveField;

	@FXML
	private Button rechercherButton;

	@FXML
	private Button echangerButton;

	@FXML
	private Label erreurLabel;

	@FXML
	private javafx.scene.layout.HBox resultatBox;

	@FXML
	private Label resultatTempsLabel;

	@FXML
	private Label resultatDetailLabel;

	@FXML
	private VBox historiqueBox;

	@FXML
	private Label historiqueVideLabel;

	private static final double CANVAS_SCALE_FACTOR = 3.0;
	private static final double PADDING = 60;
	private static final double ZOOM_TRESHOLD = 1.5;
	private static final int HISTORIQUE_MAX = 8;
	private static final Path FICHIER_HISTORIQUE =
			Path.of(System.getProperty("user.home"), ".carpe", "historique.txt");

	private double paneWidth;
	private double paneHeight;

	private Service service = new Service();
	private final Map<String, StationView> stationNodes = new HashMap<>();
	private final Map<String, String> lineNameToId = new HashMap<>();
	private final Map<String, AreteView> arreteViews = new HashMap<>();
	private PanZoomHandler panZoomHandler;
	private Graphe graphe;
	private final List<String> historique = new ArrayList<>();
>>>>>>> Stashed changes


	public void initialize() {

<<<<<<< Updated upstream
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
=======
		viewportPane.widthProperty().addListener((o, a, b) -> ajusterClip());
		viewportPane.heightProperty().addListener((o, a, b) -> ajusterClip());

		this.graphe = service.getGraphe();
		panZoomHandler = new PanZoomHandler(graphPane);
		panZoomHandler.addZoomListener(this::onZoomChanged);

		// On attend que le viewport ait sa vraie taille (apres layout) pour dimensionner la carte.
		Platform.runLater(() -> {
			paneWidth = Math.max(viewportPane.getWidth(), 800) * CANVAS_SCALE_FACTOR;
			paneHeight = Math.max(viewportPane.getHeight(), 600) * CANVAS_SCALE_FACTOR;
			graphPane.setPrefSize(paneWidth, paneHeight);
			graphPane.setMinSize(paneWidth, paneHeight);
			graphPane.getStyleClass().add("carte-fond");

			renderGraphe(this.graphe);

			double initialZoom = viewportPane.getWidth() / paneWidth;
			panZoomHandler.setZoomFactor(initialZoom);
			ajusterClip();
		});

		graphe.getLignes().forEach((ligne) -> lineNameToId.put(ligne.getNom(), ligne.getId()));

		lineChoice.getItems().add("Filtrer par ligne");
		lineChoice.setValue("Filtrer par ligne");
		graphe.getLignes().stream()
				.sorted(Comparator.comparing(Ligne::getId))
				.forEach(ligne -> lineChoice.getItems().add(ligne.getNom()));
		lineChoice.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> highlightLine(lineNameToId.get(newValue)));

		setupRecherche(departField);
		setupRecherche(arriveField);
		rechercherButton.setOnAction(e -> calculerItineraire());
		echangerButton.setOnAction(e -> echangerDepartArrivee());

		chargerHistorique();
		rafraichirHistoriqueUI();
	}

	private void ajusterClip() {
		Rectangle clip = new Rectangle(viewportPane.getWidth(), viewportPane.getHeight());
		viewportPane.setClip(clip);
	}

	/**
	 * Branche l'autocomplétion sur un champ : un Popup flottant (et non un
	 * noeud du layout) affiche les suggestions, ce qui evite tout souci de
	 * visibilite/redimensionnement lie a l'imbrication des conteneurs.
	 */
	private void setupRecherche(TextField champ) {
		ListView<String> liste = new ListView<>();
		liste.getStyleClass().add("liste-suggestions");
		liste.setPrefWidth(280);
		liste.setMaxHeight(180);

		javafx.stage.Popup popup = new javafx.stage.Popup();
		popup.setAutoHide(true);
		popup.getContent().add(liste);

		champ.textProperty().addListener((obs, ancienTexte, texte) -> {
			if (texte == null || texte.trim().length() < 2) {
				popup.hide();
				return;
			}
			List<String> noms = service.chercherStations(texte.trim()).stream()
					.map(Station::getNom)
					.distinct()
					.sorted()
					.limit(8)
					.collect(Collectors.toList());

			if (noms.isEmpty()) {
				popup.hide();
				return;
			}

			liste.getItems().setAll(noms);
			liste.setPrefHeight(Math.min(36 * noms.size() + 4, 180));

			if (!popup.isShowing()) {
				var bounds = champ.localToScreen(champ.getBoundsInLocal());
				popup.show(champ, bounds.getMinX(), bounds.getMaxY() + 2);
			}
		});

		liste.setOnMouseClicked(e -> {
			String selection = liste.getSelectionModel().getSelectedItem();
			if (selection != null) {
				champ.setText(selection);
				popup.hide();
				champ.positionCaret(selection.length());
			}
		});

		champ.focusedProperty().addListener((obs, ancien, focus) -> {
			if (!focus) popup.hide();
		});
	}

	private void echangerDepartArrivee() {
		String depart = departField.getText();
		departField.setText(arriveField.getText());
		arriveField.setText(depart);
	}

	/** Resultat local du calcul d'itineraire (pas besoin d'une classe dediee cote backend). */
	private record ItineraireResultat(List<Quai> quais, int distanceTotale) {
		int nombreChangements() {
			int changements = 0;
			for (int i = 1; i < quais.size(); i++) {
				Ligne a = quais.get(i).getLigne();
				Ligne b = quais.get(i - 1).getLigne();
				if (a != null && b != null && !a.getId().equals(b.getId())) {
					changements++;
				}
			}
			return changements;
		}
	}

	/** Calcule et affiche le plus court chemin entre les deux stations saisies. */
	private void calculerItineraire() {
		afficherErreur(null);

		Station depart = service.chercherStationExacte(departField.getText().trim());
		Station arrivee = service.chercherStationExacte(arriveField.getText().trim());

		if (depart == null || arrivee == null) {
			afficherErreur("Choisissez une station valide pour le départ et l'arrivée (utilisez les suggestions).");
			return;
		}
		if (depart.getId().equals(arrivee.getId())) {
			afficherErreur("Le départ et l'arrivée sont identiques.");
			return;
		}

		// Le calcul tourne sur un thread a part : meme s'il prend du temps,
		// l'interface reste reactive (sinon la fenetre semble "ne pas repondre").
		rechercherButton.setDisable(true);
		rechercherButton.setText("Calcul...");

		javafx.concurrent.Task<ItineraireResultat> tache = new javafx.concurrent.Task<>() {
			@Override
			protected ItineraireResultat call() {
				Quai origineQuai = depart.getQuais().get(0);
				Map<Quai, DistanceAntecedants> distances =
						backend.algo.Dijkstra.getDistanceAntecedantsMap(graphe, origineQuai);

				Quai destQuai = arrivee.getQuais().get(0);
				DistanceAntecedants infoDestination = distances.get(destQuai);
				if (infoDestination == null) {
					return null; // aucun chemin trouve
				}

				List<Quai> chemin = backend.algo.MeilleurChemin.MeilleurChemin(distances, depart, arrivee);
				return new ItineraireResultat(chemin, infoDestination.getDistance());
			}
		};

		tache.setOnSucceeded(e -> {
			rechercherButton.setDisable(false);
			rechercherButton.setText("Calculer");
			ItineraireResultat resultat = tache.getValue();
			afficherResultat(depart, arrivee, resultat);
			if (resultat != null) {
				ajouterAHistorique(depart.getNom(), arrivee.getNom());
			}
		});

		tache.setOnFailed(e -> {
			rechercherButton.setDisable(false);
			rechercherButton.setText("Calculer");
			afficherErreur("Une erreur est survenue pendant le calcul.");
			tache.getException().printStackTrace();
		});

		Thread thread = new Thread(tache, "calcul-itineraire");
		thread.setDaemon(true);
		thread.start();
	}

	private void afficherResultat(Station depart, Station arrivee, ItineraireResultat resultat) {
		if (resultat == null) {
			afficherErreur("Aucun itinéraire trouvé entre ces deux stations.");
			return;
		}

		List<String> quaiIds = resultat.quais().stream().map(Quai::getId).collect(Collectors.toList());
		highlightPath(quaiIds);

		int minutes = Math.round(resultat.distanceTotale() / 60f);
		resultatTempsLabel.setText(minutes + " min");
		resultatDetailLabel.setText(depart.getNom() + " → " + arrivee.getNom()
				+ "  ·  " + resultat.nombreChangements() + " changement(s)");
		resultatBox.setVisible(true);
		resultatBox.setManaged(true);
	}

	private void afficherErreur(String message) {
		boolean affiche = message != null;
		erreurLabel.setText(affiche ? message : "");
		erreurLabel.setVisible(affiche);
		erreurLabel.setManaged(affiche);
		if (affiche) {
			resultatBox.setVisible(false);
			resultatBox.setManaged(false);
		}
	}

	// ---------- Historique ----------

	private void ajouterAHistorique(String nomDepart, String nomArrivee) {
		String entree = nomDepart + " → " + nomArrivee;
		historique.remove(entree);
		historique.add(0, entree);
		while (historique.size() > HISTORIQUE_MAX) {
			historique.remove(historique.size() - 1);
		}
		rafraichirHistoriqueUI();
		sauvegarderHistorique();
	}

	private void rafraichirHistoriqueUI() {
		historiqueBox.getChildren().clear();
		for (String entree : historique) {
			Button bouton = new Button(entree);
			bouton.getStyleClass().add("historique-item");
			bouton.setMaxWidth(Double.MAX_VALUE);
			bouton.setOnAction(e -> rejouerHistorique(entree));
			historiqueBox.getChildren().add(bouton);
		}
		boolean vide = historique.isEmpty();
		historiqueVideLabel.setVisible(vide);
		historiqueVideLabel.setManaged(vide);
	}

	private void rejouerHistorique(String entree) {
		String[] parties = entree.split(" → ", 2);
		if (parties.length != 2) return;
		departField.setText(parties[0]);
		arriveField.setText(parties[1]);
		calculerItineraire();
	}

	private void chargerHistorique() {
		try {
			if (Files.exists(FICHIER_HISTORIQUE)) {
				historique.addAll(Files.readAllLines(FICHIER_HISTORIQUE));
			}
		} catch (IOException ignored) {
			// pas grave si l'historique ne peut pas etre lu : on repart simplement de zero
		}
	}

	private void sauvegarderHistorique() {
		try {
			Files.createDirectories(FICHIER_HISTORIQUE.getParent());
			Files.write(FICHIER_HISTORIQUE, historique,
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException ignored) {
			// pas grave si l'historique ne peut pas etre sauvegarde
		}
	}

	// ---------- Rendu de la carte ----------

	private void onZoomChanged(double zoomFactor) {
>>>>>>> Stashed changes
		if (zoomFactor < ZOOM_TRESHOLD) {
			stationNodes.values().forEach(node -> node.setLabelVisible(false));
		} else {
			stationNodes.values().forEach(node -> node.setLabelVisible(true));
		}
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
		AreteView arrete;
		if (arete.getType()	!= Arete.Type.pied)
			{
			arrete = new AreteView(
=======
		if (arete.getType() != Arete.Type.pied) {
			AreteView arrete = new AreteView(
>>>>>>> Stashed changes
					arete,
					nodeA.getCenterX(), nodeA.getCenterY(),
					nodeB.getCenterX(), nodeB.getCenterY(),
					arete.getLigne()
			);

<<<<<<< Updated upstream
				String key = arreteKey(source.getId(), destination.getId());
				arreteViews.put(key, arrete);
				graphPane.getChildren().add(0, arrete);
		}

=======
			String key = arreteKey(source.getId(), destination.getId());
			arreteViews.put(key, arrete);
			graphPane.getChildren().add(0, arrete);
		}
>>>>>>> Stashed changes
	}

	private void handlePopup(StationView node) {
		StationPopup popup = node.getPopup();
<<<<<<< Updated upstream
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
=======
		if (node.getPopup() == null || !node.getPopup().isVisible()) {
			if (popup == null) {
				popup = new StationPopup(node.getStation(), node.getCenterX(), node.getCenterY());
				graphPane.getChildren().add(popup);
			}
			popup.setVisible(true);
		} else {
			popup.setVisible(false);
		}
		node.setPopup(popup);
>>>>>>> Stashed changes
	}

	public void renderGraphe(Graphe graphe) {
		graphPane.getChildren().clear();
		stationNodes.clear();
		arreteViews.clear();

		GeoProjector projector = GeoProjector.fitTo(
<<<<<<< Updated upstream
				graphe.getStations(), PANE_WIDTH, PANE_HEIGHT, PADDING
=======
				graphe.getStations(), paneWidth, paneHeight, PADDING
>>>>>>> Stashed changes
		);

		for (Station station : graphe.getStations()) {
			double[] pos = projector.project(station.getLatitude(), station.getLongitude());
			addSommet(station, pos[0], pos[1]);
		}

		for (Arete arete : graphe.getAretes()) {
			addArete(arete);
		}
	}

<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream

	public void highlightLine(String line) {
		stationNodes.values().forEach(n -> n.setSelected(false));
		arreteViews.values().forEach(e -> e.setHighlighted(e.getArete().getLigne() != null && e.getArete().getLigne().getId().equals(line)));

	}

}
=======
	public void highlightLine(String line) {
		stationNodes.values().forEach(n -> n.setSelected(false));
		arreteViews.values().forEach(e -> e.setHighlighted(
				e.getArete().getLigne() != null && e.getArete().getLigne().getId().equals(line)));
	}

}
>>>>>>> Stashed changes
