package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import org.alver415.javafx.scene.control.LabeledTextField;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import application.input.ConversionException;
import application.input.ServerProperty;
import application.input.domain.IntegerDomain;
import application.input.domain.StringDomain;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import model.ServerConfig;

public class Utils {

	private static final ObjectMapper JSON_MAPPER;
	private static final ObjectWriter JSON_WRITER;
	static {
		JSON_MAPPER = new ObjectMapper();
		JSON_MAPPER.registerSubtypes(StringDomain.class, IntegerDomain.class);
		JSON_WRITER = JSON_MAPPER.writerWithDefaultPrettyPrinter();
	}
	public static FXMLLoader fxmlLoader(Class<?> clazz) {
		return fxmlLoader(clazz, null);
	}

	public static FXMLLoader fxmlLoader(Class<?> clazz, ResourceBundle resourceBundle) {
		return new FXMLLoader(clazz.getResource(clazz.getSimpleName().replace("Controller", "") + ".fxml"),
				resourceBundle);
	}

	public static void runFX(Runnable runnable) {
		Platform.runLater(runnable);
	}

	public static void startThread(Runnable runnable) {
		new Thread(runnable).start();
	}

	public static String getValueWithDefault(LabeledTextField textField) {
		return !StringUtils.isEmpty(textField.getText()) ? textField.getText() : textField.getPromptText();
	}

	public static String getValueWithDefault(TextField textField) {
		return !StringUtils.isEmpty(textField.getText()) ? textField.getText() : textField.getPromptText();
	}

	public static void openFile(Path path) {
		MinecraftServerManager.INSTANCE.getHostServices().showDocument(path.toAbsolutePath().toString());
	}

	public static void renameFile(Path path) throws IOException {
		TextInputDialog dialog = new TextInputDialog(path.getFileName().toString());
		dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.contains("\\") || newValue.contains("/")) {
				((StringProperty) observable).setValue(oldValue);
			}
		});
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			Files.move(path, path.resolveSibling(result.get()));
		}
	}

	public static void deleteFile(Path path) throws IOException {
		Alert dialog = new Alert(AlertType.WARNING, "Are you sure you want to delete?");
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.isPresent()) {
			if (Files.isDirectory(path)) {
				FileUtils.deleteDirectory(path.toFile());
			} else {
				Files.delete(path);
			}
		}
	}

	public static Properties loadProperties(Path path) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		if (Files.exists(path)) {
			properties.load(new FileInputStream(path.toFile()));
		}
		return properties;
	}

	public static void storeProperties(Properties props, Path path) {
		storeProperties(props, path, null);
	}

	public static void storeProperties(Properties props, Path path, String comment) {
		try {
			if (!Files.exists(path)) {
				Path directory = path.toAbsolutePath().getParent();
				if (!Files.exists(directory)) {
					Files.createDirectories(directory);
				}
				Files.createFile(path);
			}
			props.store(new FileWriter(path.toFile()), comment);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Collection<ServerConfig> loadServerConfigs() {
		List<ServerConfig> list = new ArrayList<>();
		try {
			Path path = Paths.get("servers.json");
			if (Files.exists(path)) {
				ServerConfig[] array = JSON_MAPPER.readValue(path.toFile(), ServerConfig[].class);
				list.addAll(Arrays.asList(array));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void saveServerConfigs(Collection<ServerConfig> serverConfig) {
		try {
			File file = Paths.get("servers.json").toFile();
			JSON_WRITER.writeValue(file, serverConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Collection<ServerProperty<?>> loadServerProperties() {
		List<ServerProperty<?>> list = new ArrayList<>();
		try {
			Path path = Paths.get("properties.json");
			if (Files.exists(path)) {
				ServerProperty<?>[] array = JSON_MAPPER.readValue(path.toFile(), ServerProperty[].class);
				list.addAll(Arrays.asList(array));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void saveServerProperties(Collection<ServerProperty<?>> serverProperties) {
		try {
			File file = Paths.get("properties.json").toFile();
			JSON_WRITER.writeValue(file, serverProperties);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(Object input, Class<T> type) throws ConversionException {
		if (input == null || input.toString().isEmpty()) {
			return null;
		}
		try {
			String string = input.toString();
			if (String.class.isAssignableFrom(type)) {
				return (T) string;
			} else if (Boolean.class.isAssignableFrom(type)) {
				return (T) (Boolean) Boolean.parseBoolean(string);
			} else if (Integer.class.isAssignableFrom(type)) {
				return (T) (Integer) Integer.parseInt(string);
			} else if (Double.class.isAssignableFrom(type)) {
				return (T) (Double) Double.parseDouble(string);
			}
			throw new ConversionException("Unmapped type '" + type + "'.");
		} catch (Exception e) {
			throw new ConversionException("Failed to convert input '" + input + "' into type '" + type + "'.", e);
		}
	}

}
