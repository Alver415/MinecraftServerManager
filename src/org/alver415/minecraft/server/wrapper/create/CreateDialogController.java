package org.alver415.minecraft.server.wrapper.create;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.alver415.javafx.scene.control.input.InputTextField;
import org.alver415.minecraft.server.wrapper.model.Server;
import org.apache.commons.lang3.StringUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class CreateDialogController implements Initializable {

	@FXML
	private Dialog<ButtonType> dialog;
	@FXML
	private InputTextField nameInput;
	@FXML
	private InputTextField directoryInput;
	@FXML
	private InputTextField minecraftJarInput;
	@FXML
	private InputTextField initialMemoryInput;
	@FXML
	private InputTextField maximumMemoryInput;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nameInput.setValue("Minecraft Server");
		directoryInput.setValue(Paths.get(StringUtils.EMPTY).toAbsolutePath().toString());
		minecraftJarInput.setValue(Paths.get("minecraft_server.jar").toAbsolutePath().toString());
		initialMemoryInput.setValue("512M");
		maximumMemoryInput.setValue("2G");
	}

	public Server generateServerConfg() {
		Server serverConfig = new Server();
		serverConfig.setName(nameInput.getValue());
		serverConfig.setDirectory(Paths.get(directoryInput.getValue()));
		serverConfig.setMinecraftJar(Paths.get(minecraftJarInput.getValue()));
		serverConfig.setInitialMemory(initialMemoryInput.getValue());
		serverConfig.setMaximumMemory(maximumMemoryInput.getValue());
		return serverConfig;
	}
}
