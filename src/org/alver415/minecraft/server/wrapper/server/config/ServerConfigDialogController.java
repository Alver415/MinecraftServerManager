package org.alver415.minecraft.server.wrapper.server.config;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.alver415.minecraft.server.wrapper.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class ServerConfigDialogController implements Initializable {

	@FXML
	private Dialog<ButtonType> dialog;

	@FXML
	private TextField defaultServerDirectoryInput;

	@FXML
	private TextField defaultMinecraftJarInput;

	@FXML
	private TextField defaultMaximumMemoryInput;

	@FXML
	private TextField defaultInitialMemoryInput;

	@FXML
	private Text validationMessage;

	@FXML
	private ButtonType acceptButtonType;

	public Path getDefaultServerDirectory() {
		return Paths.get(Utils.getValueWithDefault(defaultServerDirectoryInput));
	}

	public Path getDefaultMinecraftJar() {
		return Paths.get(Utils.getValueWithDefault(defaultMinecraftJarInput));
	}

	public String getDefaultMaximumMemory() {
		return Utils.getValueWithDefault(defaultMaximumMemoryInput);
	}

	public String getDefaultInitialMemory() {
		return Utils.getValueWithDefault(defaultInitialMemoryInput);
	}

	public void setDefaultServerDirectory(Path defaultServerDirectory) {
		defaultServerDirectoryInput.setText(defaultServerDirectory.toString());
	}

	public void setDefaultMinecraftJar(Path defaultMinecraftJar) {
		defaultMinecraftJarInput.setText(defaultMinecraftJar.toString());
	}

	public void setDefaultMaximumMemory(String defaultMaximumMemory) {
		defaultMaximumMemoryInput.setText(defaultMaximumMemory);
	}

	public void setDefaultInitialMemory(String defaultInitialMemory) {
		defaultInitialMemoryInput.setText(defaultInitialMemory);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Button acceptButton = (Button) dialog.getDialogPane().lookupButton(acceptButtonType);
		acceptButton.addEventFilter(ActionEvent.ACTION, event -> {
			Path defaultMinecraftJar = getDefaultMinecraftJar();
			if (!Files.exists(defaultMinecraftJar)) {
				validationMessage.setText("Minecraft jar doesn't exist.");
				event.consume();
			} else if (!Files.isExecutable(defaultMinecraftJar)) {
				validationMessage.setText("Can't execute jar.");
				event.consume();

			}
		});
	}

}
