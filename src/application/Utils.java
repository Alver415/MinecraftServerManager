package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import model.ServerConfig;

public class Utils {

	public static FXMLLoader fxmlLoader(Class<?> clazz) {
		return fxmlLoader(clazz, null);
	}

	public static FXMLLoader fxmlLoader(Class<?> clazz, ResourceBundle resourceBundle) {
		return new FXMLLoader(clazz.getResource(clazz.getSimpleName().replace("Controller", "") + ".fxml"),
				resourceBundle);
	}

	public static String getValueWithDefault(TextField textField) {
		return !StringUtils.isEmpty(textField.getText()) ? textField.getText() : textField.getPromptText();
	}

	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	private static final ObjectWriter JSON_WRITER = JSON_MAPPER.writerWithDefaultPrettyPrinter();

	public static Properties loadProperties(Path path) {
		Properties properties = new Properties();
		try {
			if (Files.exists(path)) {
				properties.load(new FileInputStream(path.toFile()));
			}
		} catch (Exception e) {
			e.printStackTrace();
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
}
