package frontend.ui.controllers;

import backend.Service;
import backend.models.*;
import frontend.ui.views.*;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.util.Duration;
import javafx.geometry.Side;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class GraphController {

	@FXML private Button menuButton;
	private CheckMenuItem acpmMenuItem;
	@FXML
	private ToggleButton toggleAPCM;


	@FXML
	private Button creditsButton;

	@FXML
	private Button checkConnexite;

	@FXML
	private VBox creditsPane;

	// Panels


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
	private VBox optionsBox;

	@FXML
	private Label optionsVideLabel;

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
	private final List<AreteView> piedViews = new ArrayList<>();
	private Service service;
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

		try {
			this.service = Service.getInstance();
			this.graphe = service.getGraphe();
		} catch (Exception e) {
			afficherErreurChargement(e);
			return; // stopper initialize() proprement, l'UI restera vide mais stable
		}


		panZoomHandler = new PanZoomHandler(graphPane);
		panZoomHandler.addZoomListener(this::onZoomChanged);

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

		toggleAPCM.setOnAction(e -> showACPM(toggleAPCM.isSelected()));
		checkConnexite.setOnAction(e -> verifConnexite());
		creditsButton.setOnAction(e->showCredits());

		chargerHistorique();
		rafraichirHistoriqueUI();

	}

	private void ajusterClip() {
		Rectangle clip = new Rectangle(viewportPane.getWidth(), viewportPane.getHeight());
		viewportPane.setClip(clip);
	}


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

	private record ItineraireResultat(Chemin chemin, double dureeSecondes, int changements, double co2Grammes) {
	    List<Quai> quais() { return chemin.cheminQuai(); }
	}
	private ItineraireResultat creerResultat(Chemin chemin) {
		double duree = Service.getCheminTemps(chemin);
		int changements = (int) Math.round(Service.getCheminNbCorrespondances(chemin));
		double co2 = Service.getCheminCO2(chemin);
		return new ItineraireResultat(chemin, duree, changements, co2);
	}

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


		rechercherButton.setDisable(true);
		rechercherButton.setText("Calcul...");

		javafx.concurrent.Task<List<ItineraireResultat>> tache = new javafx.concurrent.Task<>() {
			@Override
			protected List<ItineraireResultat> call() {
				Quai origineQuai = depart.getQuais().get(0);
				Map<Quai, DistanceAntecedants> distances =
						backend.algo.Dijkstra.getDistanceAntecedantsMap(graphe, origineQuai);

				Quai destQuai = arrivee.getQuais().get(0);
				DistanceAntecedants infoDestination = distances.get(destQuai);
				if (infoDestination == null) {
					return null; // aucun chemin trouve
				}
				List<ItineraireResultat> itineraires = new ArrayList<>();
				itineraires.add(creerResultat(Service.MeilleurCheminTemps(depart, arrivee)));
				itineraires.add(creerResultat(Service.MeilleurCheminCorrespondances(depart, arrivee)));
				itineraires.add(creerResultat(Service.MeilleurCheminCO2(depart, arrivee)));
				return itineraires;
			}
		};

		tache.setOnSucceeded(e -> {
			rechercherButton.setDisable(false);
			rechercherButton.setText("Calculer");

			List<ItineraireResultat> resultat = tache.getValue();
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

	// ---------- Options d'itinéraires ----------


	private void afficherOptions(Station depart, Station arrivee,
	                             ItineraireResultat plusRapide,
	                             ItineraireResultat moinsChangements,
	                             ItineraireResultat moinsCO2) {

		optionsBox.getChildren().clear();

		optionsBox.getChildren().add(creerCarteOption(
				"Plus rapide", plusRapide, depart, arrivee, true));
		optionsBox.getChildren().add(creerCarteOption(
				"Moins de changements", moinsChangements, depart, arrivee, false));
		optionsBox.getChildren().add(creerCarteOption(
				"Moins de CO₂", moinsCO2, depart, arrivee, false));

		optionsVideLabel.setVisible(false);
		optionsVideLabel.setManaged(false);
		optionsBox.setVisible(true);
		optionsBox.setManaged(true);

		if (plusRapide != null) {
			List<String> ids = plusRapide.quais().stream()
					.map(Quai::getId).collect(Collectors.toList());
			highlightPath(ids);
		}
	}

	private javafx.scene.layout.VBox creerCarteOption(
			String titre, ItineraireResultat resultat,
			Station depart, Station arrivee,
			boolean selectionneParDefaut) {

		javafx.scene.layout.VBox carte = new javafx.scene.layout.VBox(4);
		carte.getStyleClass().add("option-carte");
		if (selectionneParDefaut) {
			carte.getStyleClass().add("option-carte--selectionnee");
		}

		javafx.scene.control.Label labelTitre = new javafx.scene.control.Label(titre);
		labelTitre.getStyleClass().add("option-titre");

		carte.getChildren().add(labelTitre);

		if (resultat == null) {
			javafx.scene.control.Label labelNA = new javafx.scene.control.Label("Bientôt disponible");
			labelNA.getStyleClass().add("option-na");
			carte.getChildren().add(labelNA);
		} else {
			int minutes = Math.toIntExact(Math.round(resultat.dureeSecondes() / 60f));

			javafx.scene.layout.HBox metriques = new javafx.scene.layout.HBox(10);

			javafx.scene.control.Label lTemps = new javafx.scene.control.Label("🕐 " + minutes + " min");
			lTemps.getStyleClass().add("option-metrique");

			javafx.scene.control.Label lChangements = new javafx.scene.control.Label(
					"🔁 " + resultat.changements() + " chgt");
			lChangements.getStyleClass().add("option-metrique");

			javafx.scene.control.Label lCo2 = new javafx.scene.control.Label(
					"🌿 ~" + Math.round(resultat.co2Grammes()) + " g");
			lCo2.getStyleClass().add("option-metrique");

			metriques.getChildren().addAll(lTemps, lChangements, lCo2);
			carte.getChildren().add(metriques);

			// Clic sur la carte = afficher ce trajet sur la carte
			carte.setOnMouseClicked(e -> {
				// Retirer la selection de toutes les cartes
				optionsBox.getChildren().forEach(c ->
						c.getStyleClass().remove("option-carte--selectionnee"));
				carte.getStyleClass().add("option-carte--selectionnee");

				List<String> ids = resultat.quais().stream()
						.map(Quai::getId).collect(Collectors.toList());
				highlightPath(ids);

				int min = Math.toIntExact(Math.round(resultat.dureeSecondes() / 60f));
				resultatTempsLabel.setText(min + " min");
				resultatDetailLabel.setText(depart.getNom() + " → " + arrivee.getNom()
						+ "  ·  " + resultat.changements() + " changement(s)");
				resultatBox.setVisible(true);
				resultatBox.setManaged(true);
			});
		}

		return carte;
	}

	private void afficherResultat(Station depart, Station arrivee, List<ItineraireResultat> itineraires) {
		if (itineraires == null || itineraires.isEmpty()) {
			afficherErreur("Aucun itinéraire trouvé entre ces deux stations.");
			return;
		}

		ItineraireResultat resultat = itineraires.get(0);
		ItineraireResultat moinsChangements = itineraires.size() > 1 ? itineraires.get(1) : null;
		ItineraireResultat moinsCO2 = itineraires.size() > 2 ? itineraires.get(2) : null;


		afficherOptions(depart, arrivee, resultat, moinsChangements, moinsCO2);

		int minutes = Math.toIntExact(Math.round(resultat.dureeSecondes() / 60f));
		resultatTempsLabel.setText(minutes + " min");
		resultatDetailLabel.setText(depart.getNom() + " → " + arrivee.getNom()
				+ "  ·  " + resultat.changements() + " changement(s)");
		resultatBox.setVisible(true);
		resultatBox.setManaged(true);
	}

	private void afficherErreurChargement(Exception e) {
		Throwable cause = e.getCause() != null ? e.getCause() : e;

		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
				javafx.scene.control.Alert.AlertType.ERROR);
		alert.setTitle("Erreur de chargement");
		alert.setHeaderText("Impossible de charger les données du réseau");
		alert.setContentText(cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName());

		java.io.StringWriter sw = new java.io.StringWriter();
		cause.printStackTrace(new java.io.PrintWriter(sw));
		javafx.scene.control.TextArea detail = new javafx.scene.control.TextArea(sw.toString());
		detail.setEditable(false);
		detail.setWrapText(true);
		detail.setPrefRowCount(12);
		alert.getDialogPane().setExpandableContent(detail);

		alert.showAndWait();
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


	public void showACPM(boolean acpm) {
		departField.setDisable(acpm);
		arriveField.setDisable(acpm);
		rechercherButton.setDisable(acpm);
		historiqueBox.setDisable(acpm);
		echangerButton.setDisable(acpm);
		lineChoice.setDisable(acpm);
		if (acpm) {
			renderGraphe(service.getACPM());
		} else {
			renderGraphe(graphe);
		}

	}
	public void verifConnexite(){
		if(service.estConnexe()){
			Popup popup = new Popup();
			popup.getContent().add(new Label("Le graphe est connexe."));
			popup.setAutoHide(true);
			popup.show(graphPane.getScene().getWindow());
		}
	}

	public void showCredits() {
		if (creditsPane == null) return;
		if (creditsPane.isVisible()) {
			TranslateTransition tt = new TranslateTransition(Duration.millis(240), creditsPane);
			tt.setFromX(0);
			tt.setToX(creditsPane.getWidth() > 0 ? creditsPane.getWidth() : 320);
			tt.setOnFinished(e -> { creditsPane.setVisible(false);
				creditsPane.setManaged(false); creditsPane.setTranslateX(0);
			});
			tt.play();
		} else {
			creditsPane.setManaged(true);
			creditsPane.setVisible(true);
			double w = creditsPane.getPrefWidth() > 0 ? creditsPane.getPrefWidth() : 320;
			creditsPane.setTranslateX(w);
			TranslateTransition tt = new TranslateTransition(Duration.millis(240), creditsPane);
			tt.setFromX(w);
			tt.setToX(0);
			tt.play();
		} }




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
		}
	}

	private void sauvegarderHistorique() {
		try {
			Files.createDirectories(FICHIER_HISTORIQUE.getParent());
			Files.write(FICHIER_HISTORIQUE, historique,
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException ignored) {
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
		if (nodeA == null || nodeB == null) {return;}
		String key=arreteKey(source.getId(), destination.getId());



		if (arete.getType() != Arete.Type.pied) {
			if (arreteViews.containsKey(key)) {return;}

			AreteView arrete = new AreteView(
					arete,
					nodeA.getCenterX(), nodeA.getCenterY(),
					nodeB.getCenterX(), nodeB.getCenterY(),
					arete.getLigne()
			);

			arreteViews.put(key, arrete);
			graphPane.getChildren().add(0, arrete);
		}
		else{
			if (corresp.containsKey(key)) return;
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
		corresp.clear();
		piedViews.clear();

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

		resetHighlight();
		if (quaiIdsOrdonnes == null || quaiIdsOrdonnes.isEmpty()) return;

		Quai quaiDebut = graphe.getQuai(quaiIdsOrdonnes.get(0));
		Quai quaiFin   = graphe.getQuai(quaiIdsOrdonnes.get(quaiIdsOrdonnes.size() - 1));
		Map<StationView,List<Arete>> correspMap = new HashMap<>();
		for (int i = 0; i < quaiIdsOrdonnes.size(); i++) {
			String quaiId = quaiIdsOrdonnes.get(i);
			Quai quai = graphe.getQuai(quaiId);
			if (quai == null) continue;
			StationView node = stationNodes.get(quai.getStation().getId());
			if (node != null) node.setSelected(true);
			if (i == 0) continue;
			String prevQuaiId = quaiIdsOrdonnes.get(i - 1);
			Quai prevQuai = graphe.getQuai(prevQuaiId);
			if (prevQuai == null) continue;

			String key = arreteKey(prevQuaiId, quaiId);
			AreteView arrete = arreteViews.get(key);

//Distinguer trajet a pied et trajet normal
			if (arrete != null) {
				arrete.setHighlighted(true);
			} else {

				//Trajet a pied : distinguer correspondance et trajet entre stations

				Arete aretePied = corresp.get(key);
				if (aretePied == null) continue;

				Station stationSrc  = prevQuai.getStation();
				Station stationDest = quai.getStation();

				if (stationSrc.getId().equals(stationDest.getId())) {
					//correspondance: vérifier que ce sont bien des lignes différentes

					StationView stNode = stationNodes.get(stationSrc.getId());

					if (stNode != null)

						correspMap.computeIfAbsent(stNode, k -> new ArrayList<>()).add(aretePied);

				} else {
					//trajet entre stations
					StationView nodeA = stationNodes.get(stationSrc.getId());
					StationView nodeB = stationNodes.get(stationDest.getId());
					if (nodeA != null && nodeB != null) {
						AreteView piedView = new AreteView(
								aretePied,
								nodeA.getCenterX(), nodeA.getCenterY(),
								nodeB.getCenterX(), nodeB.getCenterY(),
								true
						);
						piedViews.add(piedView);
						graphPane.getChildren().add(1, piedView);
						AreteView arete = arreteViews.get(key);
						if (arete != null) arete.setVisible(false);

						setCorresp(nodeA, nodeB, aretePied);
					}
				}
			}
		}
		correspMap.forEach(this::setCorresp);


		if (quaiDebut != null) {
			StationView debut = stationNodes.get(quaiDebut.getStation().getId());
			if (debut != null) {debut.setSelected(false);debut.setStart(true);}
		}
		if (quaiFin != null) {
			StationView fin = stationNodes.get(quaiFin.getStation().getId());
			if (fin != null) { fin.setEnd(true); fin.setSelected(false); }
		}
	}

	private void setCorresp(StationView node, List<Arete> aretes) {
		ouvrirOuMAJPopup(node, parseCorresp(aretes));
	}

	private void setCorresp(StationView nodeA, StationView nodeB, Arete arete) {
		double poids = arete.getPoid();
		String duree = poids >= 60 ? Math.round(poids / 60f) + " min" : poids + " s";
		String texte = "Marche de " + nodeA.getStation().getNom()
				+ " a " + nodeB.getStation().getNom() + " : " + duree;
		ouvrirOuMAJPopup(nodeA, texte);
		ouvrirOuMAJPopup(nodeB, texte);
	}

	private void ouvrirOuMAJPopup(StationView node, String texte) {
		StationPopup popup = node.getPopup();
		if (popup == null) {
			popup = new StationPopup(node.getStation(), node.getCenterX(), node.getCenterY());
			graphPane.getChildren().add(popup);
			node.setPopup(popup);
		}
		popup.setVisible(true);
		popup.setCorresp(texte);
		node.setCorresp(texte);
	}


	private String arreteKey(String sourceQuaiId, String destQuaiId) {
		return sourceQuaiId.compareTo(destQuaiId) <= 0 ? sourceQuaiId + "<>" + destQuaiId : destQuaiId + "<>" + sourceQuaiId;
	}

	public void highlightLine(String line) {
		resetHighlight();
		arreteViews.values().forEach(e -> e.setHighlighted(
				e.getArete().getLigne() != null && e.getArete().getLigne().getId().equals(line)));
	}

	public String parseCorresp(List<Arete> corresps) {

		double poids = corresps.stream().mapToDouble(Arete::getPoid).sum();
		String duree = poids >= 60 ? Math.round(poids / 60f) + " min" : poids + " s";

		java.util.Set<String> lignes = new java.util.LinkedHashSet<>();
		for (Arete a : corresps) {
			if (a.getSource().getLigne() != null)
				lignes.add(a.getSource().getLigne().getNom());
			if (a.getDestination().getLigne() != null)
				lignes.add(a.getDestination().getLigne().getNom());
		}
		List<String> lignesList = new ArrayList<>(lignes);
		String depart = lignesList.get(0);
		String arrivee = lignesList.get(lignesList.size() - 1);
		return depart + " → " + arrivee + " : " + duree;
	}


	public void resetHighlight(){
		stationNodes.values().forEach(n -> {n.setSelected(false); n.setStart(false);n.setEnd(false);n.setCorresp("");});
		arreteViews.values().forEach(e -> {
			e.setHighlighted(false);
			e.setVisible(true);
		});
		piedViews.forEach(p -> graphPane.getChildren().remove(p));
		piedViews.clear();
	}



}