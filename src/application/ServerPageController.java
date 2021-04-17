package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.ServerConfigurationModel;

public class ServerPageController implements Initializable {

	private static final String NEW_LINE = System.lineSeparator();

	private static final String EULA_TITLE = "End User License Agreement";
	private static final String EULA_COMMENT = "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).";
	private static final String EULA_MESSAGE = "By clicking 'Accept' you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).";

	private static final String IN = "[IN]  ";
	private static final String OUT = "[OUT] ";
	private static final String ERR = "[ERR] ";

	private ServerConfigurationModel model = new ServerConfigurationModel();
	private Process process;
	private List<String> history = new ArrayList<>();;
	private int historyIndex;

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

	public void openServerConfiguration() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerConfiguration.fxml"));
			Stage stage = loader.load();
			ServerConfigurationController controller = loader.getController();
			stage.show();
			controller.setModel(model);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void openEulaDialog() {
		Dialog<ButtonType> eulaDialog = new Dialog<>();
		eulaDialog.setTitle(EULA_TITLE);
		eulaDialog.setContentText(EULA_MESSAGE);

		ButtonType decline = new ButtonType("Decline", ButtonData.CANCEL_CLOSE);
		eulaDialog.getDialogPane().getButtonTypes().add(decline);

		ButtonType accept = new ButtonType("Accept", ButtonData.OK_DONE);
		eulaDialog.getDialogPane().getButtonTypes().add(accept);

		eulaDialog.showAndWait().filter(response -> {
			return Objects.equals(response, accept);
		}).ifPresent(response -> {
			acceptEula();
		});

	}

	private void acceptEula() {
		File eula = new File(model.getServerDirectory().getAbsoluteFile() + "/eula.txt");
		Properties props;
		if (eula.exists()) {
			props = PropertyUtils.loadProperties(eula);
		} else {
			props = new Properties();
		}
		props.setProperty("eula", "true");
		PropertyUtils.storeProperties(props, eula, EULA_COMMENT);
	}

	public void start() throws IOException {
		File serverDirectory = model.getServerDirectory();
		if (!serverDirectory.exists()) {
			serverDirectory.mkdirs();
		}

		List<String> command = buildCommand(model);
		printToConsole(IN, String.join(" ", command));

		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		builder.directory(serverDirectory);
		process = builder.start();

		InputStream inputStream = process.getInputStream();
		setupInputStreamThread(OUT, inputStream);
		InputStream errorStream = process.getErrorStream();
		setupInputStreamThread(ERR, errorStream);

		process.onExit().thenRun(() -> {
			updateUI();
		});

		updateUI();
	}

	private List<String> buildCommand(ServerConfigurationModel model) {
		File minecraftServerJar = model.getMinecraftServerJar();
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
		strings.add("\"" + minecraftServerJar.getAbsolutePath() + "\"");
		strings.add("--nogui");
		return strings;
	}

	private void setupHotkeys() {
		input.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (history.isEmpty()) {
					return;
				}
				runFX(() -> {
					switch (event.getCode()) {
					case UP:
						historyIndex--;
						break;
					case DOWN:
						historyIndex++;
						break;
					default:
						// Return early and don't do anything else
						return;
					}

					historyIndex = Math.min(Math.max(0, historyIndex), history.size());

					if (historyIndex >= history.size()) {
						input.setText("");
					} else {
						String command = history.get(historyIndex);
						input.setText(command);
					}
				});
			}
		});
	}

	private void setupInputStreamThread(String type, InputStream inputStream) {
		startThread(() -> {
			try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				while ((line = reader.readLine()) != null) {
					printToConsole(type, line);
				}
			} catch (IOException e) {
				printToConsole(ERR, getStackTrace(e));
			}

		});
	}

	public void clear() {
		runFX(() -> console.clear());
	}

	public void stop() {
		writeToProcess("stop");
		final Process process = this.process;
		startThread(() -> {
			try {
				process.waitFor();
				updateUI();
			} catch (InterruptedException e) {
				printToConsole(ERR, getStackTrace(e));
			}
		});
	}

	private boolean isProcessRunning() {
		return process != null && process.isAlive();
	}

	private void updateUI() {
		boolean running = isProcessRunning();
		runFX(() -> {
			startButton.setDisable(running);
			stopButton.setDisable(!running);
			input.setDisable(!running);
		});
	}

	public void submit() {
		String input = this.input.getText();
		if (Objects.equals("stop", input)) {
			stop();
		} else {
			writeToProcess(input);
		}
		this.input.clear();

	}

	private void writeToProcess(final String command) {
		try {
			history.add(command);
			historyIndex = history.size();
			OutputStream os = process.getOutputStream();
			os.write((command + NEW_LINE).getBytes());
			printToConsole(IN, command);
			os.flush();
		} catch (Exception e) {
			printToConsole(ERR, getStackTrace(e));
		}
	}

	private String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	private void printToConsole(String sourcePrefix, String string) {
		runFX(() -> {
			String line = sourcePrefix + string + NEW_LINE;
			if (ERR.equals(sourcePrefix)) {
				System.err.print(line);
			} else {
				System.out.print(line);
			}
			console.appendText(line);
			console.setScrollLeft(Double.MIN_VALUE);
			console.setScrollTop(Double.MAX_VALUE);
		});
	}

	private void runFX(Runnable runnable) {
		Platform.runLater(runnable);
	}

	private void startThread(Runnable runnable) {
		new Thread(runnable).start();
	}
}
