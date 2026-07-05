package frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Appli extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Appli.fxml"));
		stage.getIcons().add(new Image(
				Objects.requireNonNull(getClass().getResourceAsStream("/images/logo.png"))
		));
		Scene scene = new Scene(loader.load(), 1320, 820);
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		stage.setTitle("Carpe");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}