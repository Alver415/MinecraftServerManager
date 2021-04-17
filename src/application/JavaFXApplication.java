package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXApplication extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private void exit() {
		Platform.exit();
		System.exit(0);
	}

	@Override
	public void start(Stage primaryStage) throws IOException, InterruptedException {
		primaryStage.setOnCloseRequest(e -> exit());
		primaryStage.setTitle("JavaFX Application");
		
		Parent serverPage = new FXMLLoader(getClass().getResource("ServerPage.fxml")).load();
		Scene scene = new Scene(serverPage);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
