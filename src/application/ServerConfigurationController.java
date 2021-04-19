package application;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.ServerConfig;

public class ServerConfigurationController {

	private static final String SERVER_PROPERTIES_COMMENT = "Minecraft server properties";
	private static final Properties SERVER_PROPERTIES_DEFAULT = Utils
			.loadProperties(Paths.get("server.properties.default"));

	private ServerConfig model;

	@FXML
	private Stage stage;

	@FXML
	private TextField serverDirectoryInput;

	@FXML
	private TextField minecraftServerJarInput;

	@FXML
	private TextField maximumMemoryInput;

	@FXML
	private TextField initialMemoryInput;

	@FXML
	private GridPane serverPropertiesPane;

	public void submit() {
		Path serverDirectory = Paths.get(serverDirectoryInput.getText());
		Path minecraftServerJar = Paths.get(minecraftServerJarInput.getText());
		String maximumMemory = maximumMemoryInput.getText();
		String initialMemory = initialMemoryInput.getText();
		Properties serverProperties = new Properties(SERVER_PROPERTIES_DEFAULT);

		Set<Node> textFieldNodes = serverPropertiesPane.lookupAll(TextField.class.getSimpleName());
		for (Node node : textFieldNodes) {
			if (node instanceof TextField) {
				TextField textField = (TextField) node;
				String key = textField.getId();
				String userValue = textField.getText();
				String defaultValue = SERVER_PROPERTIES_DEFAULT.getProperty(key);
				String value = !StringUtils.isEmpty(userValue) ? userValue : defaultValue;
				serverProperties.setProperty(key, value);
			}
		}

		model.setServerDirectory(serverDirectory);
		model.setMinecraftServerJar(minecraftServerJar);
		model.setMaximumMemory(maximumMemory);
		model.setInitialMemory(initialMemory);
		model.setServerProperties(serverProperties);

		Path serverPropertiesPath = Paths.get(serverDirectory + "/server.properties");
		Utils.storeProperties(serverProperties, serverPropertiesPath, SERVER_PROPERTIES_COMMENT);

		stage.close();
	}

	public void cancel() {
		stage.close();
	}

	public void setModel(ServerConfig model) {
		this.model = model;
		String serverDirectory = model.getServerDirectory().toString();
		String minecraftServerJar = model.getMinecraftServerJar().toString();
		String maximumMemory = model.getMaximumMemory();
		String initialMemory = model.getInitialMemory();

		Path serverPropertiesPath = Paths.get(model.getServerDirectory() + "/server.properties");
		Properties serverProperties = Utils.loadProperties(serverPropertiesPath);

		serverDirectoryInput.setText(serverDirectory);
		minecraftServerJarInput.setText(minecraftServerJar);
		maximumMemoryInput.setText(maximumMemory);
		initialMemoryInput.setText(initialMemory);

		populateGridPane(serverPropertiesPane, serverProperties);
	}

	private void populateGridPane(GridPane gridPane, Properties properties) {
		int row = 0;
		for (Entry<Object, Object> entry : SERVER_PROPERTIES_DEFAULT.entrySet()) {
			String key = Objects.toString(entry.getKey());
			String defaultValue = Objects.toString(entry.getValue());
			String currentValue = properties.getProperty(key);
			addField(gridPane, row, key, defaultValue, currentValue);
			row++;
		}
	}

	private void addField(GridPane gridPane, int row, String key, String defaultValue, String currentValue) {
		Label keyLabel = new Label(key + " : ");
		gridPane.add(keyLabel, 0, row);

		TextField valueField = new TextField();
		if (!Objects.equals(currentValue, defaultValue)) {
			valueField.setText(currentValue);
		}
		valueField.setPromptText(defaultValue);
		valueField.setId(key);
		gridPane.add(valueField, 1, row);
	}

}
