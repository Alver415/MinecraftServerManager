package application.create;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import application.MinecraftServerManager;
import application.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import model.ServerConfig;

public class CreateDialogController implements Initializable {

	private Set<ServerConfig> serverConfigs = new HashSet<>();
	private Path defaultServerDirectory = Paths.get("servers");

	@FXML
	private Dialog<ButtonType> dialog;

	@FXML
	private TextField serverNameInput;

	@FXML
	private TextField serverDirectoryInput;

	@FXML
	private Text validationMessage;

	@FXML
	private Hyperlink eulaHyperlink;

	@FXML
	private ButtonType acceptButtonType;

	public void setServerConfigs(Set<ServerConfig> serverConfigs) {
		this.serverConfigs = serverConfigs;
	}

	public void setDefaultServerDirectory(Path defaultServerDirectory) {
		this.defaultServerDirectory = defaultServerDirectory;
		setServerDirectoryInputPrompt();
	}

	private void setServerDirectoryInputPrompt() {
		String serverName = serverNameInput.getText();
		Path path = defaultServerDirectory.resolve(serverName.trim());
		serverDirectoryInput.setPromptText(path.toAbsolutePath().toString());
	}

	public String getServerName() {
		return serverNameInput.getText();
	}

	public Path getServerDirectory() {
		return Paths.get(Utils.getValueWithDefault(serverDirectoryInput)).toAbsolutePath();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setServerDirectoryInputPrompt();

		serverNameInput.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				setServerDirectoryInputPrompt();
			}
		});

		eulaHyperlink.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				MinecraftServerManager.INSTANCE.getHostServices().showDocument(eulaHyperlink.getText());
			}
		});

		Button acceptButton = (Button) dialog.getDialogPane().lookupButton(acceptButtonType);
		acceptButton.addEventFilter(ActionEvent.ACTION, event -> {
			if (StringUtils.isEmpty(getServerName())) {
				validationMessage.setText("Server Name is a required field.");
				event.consume();
			}

			Path serverDirectory = getServerDirectory();
			boolean duplicateDirectory = serverConfigs.stream().map(serverConfig -> serverConfig.getServerDirectory())
					.anyMatch(existingDirectory -> Objects.equals(existingDirectory, serverDirectory));
			if (duplicateDirectory) {
				validationMessage.setText("That directory is already in use.");
				event.consume();
			}
		});
	}
}
