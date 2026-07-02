package frontend.ui.views;

import backend.models.Station;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.stream.Collectors;


public class StationPopup extends VBox {
	private Text correspText;
	public StationPopup(Station station,double x, double y ) {
		super();

		this.setStyle("-fx-border-color: #333; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #f9f9f9;");
		this.setPrefWidth(180);

		Label nameLabel = new Label(station.getNom());
		Text ligneText = new Text("Lignes de la station :\n"+
				station.getQuais().stream()
						.map(quai -> quai.getLigne().getNom()).distinct()
						.collect(Collectors.joining(", "))
		);

		this.getChildren().addAll(nameLabel,ligneText);

		this.setLayoutX(x+20);
		this.setLayoutY(y-70);
	}

	public void setCorresp(String corresp){
		if (corresp != null && !corresp.isEmpty()) {
			if (correspText != null) {
				this.getChildren().remove(correspText);
			}
			correspText = new Text("Correspondances : " + corresp);
			this.getChildren().add(correspText);
		} else {
			if (correspText != null) {
				this.getChildren().remove(correspText);
				correspText = null;
			}
		}
	}

}
