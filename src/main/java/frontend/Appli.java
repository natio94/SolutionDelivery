package frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Appli extends Application {

<<<<<<< Updated upstream

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Appli.fxml"));
		Scene scene = new Scene(loader.load(), 1200, 800);
		stage.setTitle("Transitus");
=======
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Appli.fxml"));
		Scene scene = new Scene(loader.load(), 1320, 820);
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		stage.setTitle("Carpe");
>>>>>>> Stashed changes
		stage.setScene(scene);
		stage.show();
	}

<<<<<<< Updated upstream
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
=======

>>>>>>> Stashed changes
	public static void main(String[] args) {
		launch(args);
	}
}
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
