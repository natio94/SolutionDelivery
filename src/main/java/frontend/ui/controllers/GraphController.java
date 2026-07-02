package frontend.ui.controllers;

import backend.Service;
import backend.models.*;
import frontend.ui.views.*;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphController {

	@FXML
	private Pane graphPane;

	@FXML
	private Pane viewportPane;

	@FXML
	private ChoiceBox<String> lineChoice;

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

	private final Service service = new Service();
	private final Map<String, StationView> stationNodes = new HashMap<>();
	private final Map<String, String> lineNameToId = new HashMap<>();
	private final Map<String, AreteView> arreteViews = new HashMap<>();
	private final Map<String, Arete> corresp = new HashMap<>();
	private PanZoomHandler panZoomHandler;
	private Graphe graphe;
	private final List<String> historique = new ArrayList<>();


	public void initialize() {

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

		if (arete.getType() != Arete.Type.pied) {
			AreteView arrete = new AreteView(
					arete,
					nodeA.getCenterX(), nodeA.getCenterY(),
					nodeB.getCenterX(), nodeB.getCenterY(),
					arete.getLigne()
			);

			String key = arreteKey(source.getId(), destination.getId());
			arreteViews.put(key, arrete);
			graphPane.getChildren().add(0, arrete);
		}
		else{

			corresp.put(arreteKey(source.getId(), destination.getId()), arete);
		}
	}

	private void handlePopup(StationView node) {
		StationPopup popup = node.getPopup();
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
	}

	public void renderGraphe(Graphe graphe) {
		graphPane.getChildren().clear();
		stationNodes.clear();
		arreteViews.clear();

		GeoProjector projector = GeoProjector.fitTo(
				graphe.getStations(), paneWidth, paneHeight, PADDING
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
		Map<StationView,List<Arete>> correspMap = new HashMap<>();
		resetHighlight();
		Quai quaiDebut = graphe.getQuai(quaiIdsOrdonnes.get(0));
		Quai quaiFin = graphe.getQuai(quaiIdsOrdonnes.get(quaiIdsOrdonnes.size() - 1));

		for (int i = 1; i < quaiIdsOrdonnes.size(); i++) {
			String quaiId = quaiIdsOrdonnes.get(i);
			Quai quai = graphe.getQuai(quaiId);
			if (quai == null) continue;

			StationView node = stationNodes.get(quai.getStation().getId());
			if (node != null) node.setSelected(true);


			String prevQuaiId = quaiIdsOrdonnes.get(i - 1);
			AreteView arrete = arreteViews.get(arreteKey(prevQuaiId, quaiId));

			if (arrete == null) {
				arrete = arreteViews.get(arreteKey(quaiId, prevQuaiId));
			}

			if (arrete != null){
				arrete.setHighlighted(true);
			}
			else
				correspMap.computeIfAbsent(node, k -> new ArrayList<>()).add(corresp.get(arreteKey(prevQuaiId, quaiId)));


		}
		correspMap.forEach((station, arretes) -> {
			StationPopup  popup=station.getPopup();
			if (popup == null) {
				popup = new StationPopup(station.getStation(), station.getCenterX(), station.getCenterY());
				graphPane.getChildren().add(popup);
				station.setPopup(popup);
			}
			String correspString = parseCorresp(arretes);
			station.setCorresp(correspString);
		});
		stationNodes.get(quaiDebut.getStation().getId()).setStart(true);
		stationNodes.get(quaiFin.getStation().getId()).setEnd(true);
		stationNodes.get(quaiFin.getStation().getId()).setSelected(false);
	}

	private String arreteKey(String sourceQuaiId, String destQuaiId) {
		return sourceQuaiId + "->" + destQuaiId;
	}

	public void highlightLine(String line) {
		resetHighlight();
		arreteViews.values().forEach(e -> e.setHighlighted(
				e.getArete().getLigne() != null && e.getArete().getLigne().getId().equals(line)));
	}

	public String parseCorresp(List<Arete> corresps){
		Quai depart=corresps.get(0).getSource();
		Quai arrive=corresps.get(corresps.size()-1).getDestination();

		int poids = corresps.stream().mapToInt(Arete::getPoid).sum();

		if (poids>=60)poids=Math.round(poids / 60f);
		return depart.getLigne().getNom()+"->"+arrive.getLigne().getNom()+": "+poids+"min";
	}

	public void resetHighlight(){
		stationNodes.values().forEach(n -> {n.setSelected(false); n.setStart(false);n.setEnd(false);n.setCorresp("");});
		arreteViews.values().forEach(e -> e.setHighlighted(false));

	}
}