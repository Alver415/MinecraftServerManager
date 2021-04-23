package application.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import application.ServerConfigurationController;
import application.Utils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.ServerConfig;

public class ServerPageController implements Initializable {

	private static final String NEW_LINE = System.lineSeparator();

	private static final String IN = "[IN]  ";
	private static final String OUT = "[OUT] ";
	private static final String ERR = "[ERR] ";

	private ServerConfig serverConfig;

	private Process process;
	private List<String> commandHistory = new ArrayList<>();;
	private int commandHistoryIndex;

	@FXML
	private Button startButton;

	@FXML
	private Button stopButton;

	@FXML
	private TextArea console;

	@FXML
	private TextField input;


	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		updateUI();
		setupHotkeys();
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public void openServerConfiguration() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerConfiguration.fxml"));
			Stage stage = loader.load();
			ServerConfigurationController controller = loader.getController();
			stage.show();
			controller.setServerConfig(serverConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() throws IOException {
		Path serverDirectory = serverConfig.getServerDirectory();
		if (!Files.exists(serverDirectory)) {
			Files.createDirectories(serverDirectory);
		}

		List<String> command = buildCommand(serverConfig);
		printToConsole(IN, String.join(" ", command));

		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		builder.directory(serverDirectory.toFile());
		process = builder.start();

		InputStream inputStream = process.getInputStream();
		pipeInputStreamToConsole(OUT, inputStream);
		InputStream errorStream = process.getErrorStream();
		pipeInputStreamToConsole(ERR, errorStream);

		process.onExit().thenRun(() -> {
			updateUI();
		});

		updateUI();
	}

	private List<String> buildCommand(ServerConfig model) {
		Path minecraftServerJar = model.getMinecraftServerJar();
		String xmx = model.getMaximumMemory();
		String xms = model.getInitialMemory();

		List<String> strings = new ArrayList<String>();
		strings.add("java");
		if (xmx != null) {
			strings.add("-Xmx" + xmx);
		}
		if (xms != null) {
			strings.add("-Xms" + xms);
		}
		strings.add("-jar");
		strings.add("\"" + minecraftServerJar.toAbsolutePath() + "\"");
		strings.add("--nogui");
		return strings;
	}

	public void newServer() {

	}

	public void openServer() {

	}

	public void clear() {
		Utils.runFX(() -> console.clear());
	}

	public void stop() {
		writeToProcess("stop");
	}

	public boolean isProcessRunning() {
		return process != null && process.isAlive();
	}

	private void updateUI() {
		boolean running = isProcessRunning();
		Utils.runFX(() -> {
			startButton.setDisable(running);
			stopButton.setDisable(!running);
			input.setDisable(!running);
		});
	}

	public void submit() {
		writeToProcess(input.getText());
		input.clear();

	}

	private void writeToProcess(final String command) {
		try {
			commandHistory.add(command);
			commandHistoryIndex = commandHistory.size();
			OutputStream os = process.getOutputStream();
			os.write((command + NEW_LINE).getBytes());
			printToConsole(IN, command);
			os.flush();
		} catch (Exception e) {
			printToConsole(ERR, ExceptionUtils.getStackTrace(e));
		}
	}

	private void printToConsole(String prefix, String string) {
		Utils.runFX(() -> {
			String line = prefix + string + NEW_LINE;
			if (Objects.equals(ERR, prefix)) {
				System.err.print(line);
			} else {
				System.out.print(line);
			}
			console.appendText(line);
			console.setScrollLeft(Double.MIN_VALUE);
			console.setScrollTop(Double.MAX_VALUE);
		});
	}

	private void setupHotkeys() {
		input.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (CollectionUtils.isEmpty(commandHistory)) {
					return;
				}
				Utils.runFX(() -> {
					switch (event.getCode()) {
					case UP:
						commandHistoryIndex--;
						break;
					case DOWN:
						commandHistoryIndex++;
						break;
					default:
						// Return early and don't do anything else
						return;
					}

					commandHistoryIndex = Math.min(Math.max(0, commandHistoryIndex), commandHistory.size());

					if (commandHistoryIndex >= commandHistory.size()) {
						input.setText("");
					} else {
						String command = commandHistory.get(commandHistoryIndex);
						input.setText(command);
					}
				});
			}
		});
	}

	private void pipeInputStreamToConsole(String prefix, InputStream inputStream) {
		Utils.startThread(() -> {
			try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				while ((line = reader.readLine()) != null) {
					printToConsole(prefix, line);
				}
			} catch (IOException e) {
				printToConsole(ERR, ExceptionUtils.getStackTrace(e));
			}

		});
	}

}
