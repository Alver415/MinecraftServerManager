package org.alver415.minecraft.server.wrapper;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MinecraftServerManager extends Application {

	public static MinecraftServerManager INSTANCE;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException, InterruptedException {
		INSTANCE = this;

		Scene scene = new FXMLLoader(getClass().getResource("MainScene.fxml")).load();
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(e -> exit());
		primaryStage.setTitle("Minecraft Server Wrapper");
		primaryStage.show();
	}
	
	private void exit() {
		Platform.exit();
		System.exit(0);
	}
}
