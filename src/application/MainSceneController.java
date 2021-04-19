package application;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.NotImplementedException;

import application.create.CreateDialogController;
import application.preferences.PreferencesDialogController;
import application.server.ServerPageController;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.ServerConfig;

public class MainSceneController implements Initializable {
	private static final Path PREFERENCES_PROPERTIES = Paths.get("preferences.properties");

	private static final String DARK_CSS = "-fx-base: rgba(60, 60, 60, 255);";
	private static final String LIGHT_CSS = "";

	private static final String EULA_COMMENT = "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).";

	private Properties preferences;

	private BidiMap<ServerConfig, TitledPane> paneMap = new DualHashBidiMap<>();
	private BidiMap<ServerConfig, Tab> tabMap = new DualHashBidiMap<>();

	@FXML
	private Scene scene;

	@FXML
	private Accordion accordion;

	@FXML
	private TabPane tabPane;

	@FXML
	private ToggleButton darkModeToggle;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		darkModeToggle
				.setOnAction(event -> scene.getRoot().setStyle(darkModeToggle.isSelected() ? DARK_CSS : LIGHT_CSS));
		tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			ServerConfig serverConfig = tabMap.getKey(newValue);
			setSelectedServer(serverConfig);
		});

		initializePreferences();
		Utils.loadServerConfigs().forEach(serverConfig -> createServerPane(serverConfig));

		getServerConfigs().stream().findFirst().ifPresentOrElse(serverConfig -> setSelectedServer(serverConfig),
				() -> createServer());
	}

	private Set<ServerConfig> getServerConfigs() {
		return paneMap.keySet();
	}

	private void initializePreferences() {
		if (!Files.exists(PREFERENCES_PROPERTIES)) {
			preferences = new Properties();
			preferences.setProperty("default.server.directory", "servers");
			preferences.setProperty("default.minecraft.jar", "server.jar");
			preferences.setProperty("default.maximum.memory", "2g");
			preferences.setProperty("default.initial.memory", "512m");
			Utils.storeProperties(preferences, PREFERENCES_PROPERTIES);
		} else {
			preferences = Utils.loadProperties(PREFERENCES_PROPERTIES);
		}
	}

	private void setSelectedServer(ServerConfig serverConfig) {
		if (serverConfig == null) {
			return;
		}
		Tab tab = getServerTab(serverConfig);
		tabPane.getSelectionModel().select(tab);

		TitledPane pane = getServerPane(serverConfig);
		accordion.expandedPaneProperty().set(pane);
	}

	private TitledPane getServerPane(ServerConfig serverConfig) {
		if (!paneMap.containsKey(serverConfig)) {
			createServerPane(serverConfig);
		}
		return paneMap.get(serverConfig);
	}

	private TitledPane createServerPane(ServerConfig serverConfig) {
		TreeView<Path> treeView = new TreeView<>(buildTreeItem(serverConfig.getServerDirectory()));
		initializeTreeView(treeView);
		TitledPane pane = new TitledPane(serverConfig.getServerName(), treeView);
		pane.expandedProperty()
				.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
					if (newValue.booleanValue()) {
						Utils.runFX(() -> {
							setSelectedServer(serverConfig);
						});
					}
				});
		paneMap.put(serverConfig, pane);
		if (!accordion.getPanes().contains(pane)) {
			accordion.getPanes().add(pane);
			accordion.getPanes().sort(new Comparator<TitledPane>() {
				@Override
				public int compare(TitledPane o1, TitledPane o2) {
					String t1 = o1.getText();
					String t2 = o2.getText();
					return t1.compareTo(t2);
				}
			});
		}
		return pane;
	}

	private Tab getServerTab(ServerConfig serverConfig) {
		if (!tabMap.containsKey(serverConfig)) {
			createServerTab(serverConfig);
		}
		return tabMap.get(serverConfig);
	}

	private Tab createServerTab(ServerConfig serverConfig) {
		Tab tab = new Tab();
		tab.setText(serverConfig.getServerName());
		try {
			FXMLLoader loader = Utils.fxmlLoader(ServerPageController.class);
			Parent parent = loader.load();
			ServerPageController controller = loader.getController();
			controller.setServerConfig(serverConfig);
			tab.setContent(parent);
			tab.setOnCloseRequest((Event event) -> {
				if (controller.isProcessRunning()) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Confirmation Dialog");
					alert.setHeaderText("The Server is Still Running");
					alert.setContentText("Closing this window will stop the process abruptly.");
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() != ButtonType.OK) {
						event.consume();
					} else {
						controller.stop();
					}
				}
			});
			tab.setOnClosed((Event event) -> {
				tabMap.remove(serverConfig);
			});
		} catch (

		IOException e) {
			tab.setText("ERROR");
			e.printStackTrace();
		}
		tabMap.put(serverConfig, tab);
		if (!tabPane.getTabs().contains(tab)) {
			tabPane.getTabs().add(tab);
		}
		return tab;
	}

	private void setupTreeItemListener(TreeItem<Path> item) {
		Path path = (item.getValue());
		Utils.startThread(() -> {
			try {
				WatchService watchService = Utils.getWatchService();
				path.register(watchService, ENTRY_CREATE, ENTRY_DELETE);

				boolean poll = true;
				while (poll) {
					WatchKey key = watchService.take();
					for (WatchEvent<?> event : key.pollEvents()) {
						if (event.kind() == ENTRY_CREATE) {
							Path createdPath = path.resolve((Path) event.context());
							TreeItem<Path> newChild = new TreeItem<>(createdPath);
							item.getChildren().add(newChild);
							item.getChildren().sort(TREE_ITEM_COMPARATOR);
						} else if (event.kind() == ENTRY_DELETE) {
							item.getChildren().stream()
									.filter(i -> i.getValue().equals(path.resolve((Path) event.context()))).findFirst()
									.ifPresent(oldChild -> item.getChildren().remove(oldChild));
							;
						}
					}
					poll = key.reset();
				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});

	}

	private TreeItem<Path> buildTreeItem(Path path) {
		TreeItem<Path> item = new TreeItem<>();
		item.setValue(path);

		if (Files.isDirectory(path)) {
			setupTreeItemListener(item);
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
				stream.forEach(childPath -> {
					item.getChildren().add(buildTreeItem(childPath));
				});
				item.getChildren().sort(TREE_ITEM_COMPARATOR);
				item.setExpanded(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return item;

	}

	public void createServer() {
		try {
			FXMLLoader loader = Utils.fxmlLoader(CreateDialogController.class);
			Dialog<ButtonType> dialog = loader.load();
			CreateDialogController controller = loader.getController();
			controller.setServerConfigs(getServerConfigs());
			controller.setDefaultServerDirectory(Paths.get(preferences.getProperty("default.server.directory")));
			dialog.showAndWait().filter(response -> {
				return Objects.equals(response.getButtonData(), ButtonData.OK_DONE);
			}).ifPresent(response -> {
				// Create new ServerConfig
				ServerConfig serverConfig = new ServerConfig();
				serverConfig.setServerName(controller.getServerName());
				serverConfig.setServerDirectory(controller.getServerDirectory());
				serverConfig.setMinecraftServerJar(Paths.get(preferences.getProperty("default.minecraft.jar")));
				serverConfig.setMaximumMemory(preferences.getProperty("default.maximum.memory"));
				serverConfig.setInitialMemory(preferences.getProperty("default.initial.memory"));

				// Refresh UI
				setSelectedServer(serverConfig);

				// Save data
				saveAllServers();
				acceptEula(serverConfig);
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void acceptEula(ServerConfig serverConfig) {
		Path eula = Paths.get(serverConfig.getServerDirectory() + "/eula.txt");
		Properties props;
		if (Files.isReadable(eula)) {
			props = Utils.loadProperties(eula);
		} else {
			props = new Properties();
		}
		props.setProperty("eula", "true");
		Utils.storeProperties(props, eula, EULA_COMMENT);
	}

	public void saveAllServers() {
		Utils.saveServerConfigs(getServerConfigs());
	}

	public void openPreferencesDialog() {
		try {
			FXMLLoader loader = Utils.fxmlLoader(PreferencesDialogController.class);
			Dialog<ButtonType> dialog = loader.load();
			PreferencesDialogController controller = loader.getController();

			preferences = Utils.loadProperties(PREFERENCES_PROPERTIES);
			Path defaultServerDirectory = Paths.get(preferences.getProperty("default.server.directory", ""));
			Path defaultMinecraftJar = Paths.get(preferences.getProperty("default.minecraft.jar", ""));
			String maximumMemory = preferences.getProperty("default.maximum.memory");
			String initialMemory = preferences.getProperty("default.initial.memory");
			controller.setDefaultServerDirectory(defaultServerDirectory);
			controller.setDefaultMinecraftJar(defaultMinecraftJar);
			controller.setDefaultMaximumMemory(maximumMemory);
			controller.setDefaultInitialMemory(initialMemory);

			dialog.showAndWait().filter(response -> {
				return Objects.equals(response.getButtonData(), ButtonData.OK_DONE);
			}).ifPresent(response -> {
				preferences.setProperty("default.server.directory", controller.getDefaultServerDirectory().toString());
				preferences.setProperty("default.minecraft.jar", controller.getDefaultMinecraftJar().toString());
				preferences.setProperty("default.maximum.memory", controller.getDefaultMaximumMemory());
				preferences.setProperty("default.initial.memory", controller.getDefaultInitialMemory());

				Utils.storeProperties(preferences, PREFERENCES_PROPERTIES);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeTreeView(TreeView<Path> treeView) {
		treeView.setCellFactory(new Callback<TreeView<Path>, TreeCell<Path>>() {
			@Override
			public TreeCell<Path> call(TreeView<Path> param) {
				TreeCell<Path> treeCell = new TextFieldTreeCell<Path>(new StringConverter<Path>() {
					@Override
					public String toString(Path object) {
						return object.getFileName().toString();
					}

					@Override
					public Path fromString(String string) {
						throw new NotImplementedException();
					}
				});
				return treeCell;
			}
		});
		treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				boolean doubleClick = e.getClickCount() % 2 == 0;
				TreeItem<Path> selectedItem = treeView.getSelectionModel().getSelectedItem();
				if (doubleClick && selectedItem != null) {
					String absolutePath = selectedItem.getValue().toAbsolutePath().toString();
					MinecraftServerManager.INSTANCE.getHostServices().showDocument(absolutePath);
				}
			}
		});
	}

	private static final Comparator<TreeItem<Path>> TREE_ITEM_COMPARATOR = new Comparator<TreeItem<Path>>() {
		@Override
		public int compare(TreeItem<Path> o1, TreeItem<Path> o2) {
			Path p1 = o1.getValue();
			Path p2 = o2.getValue();
			int i = 0;
			if (Files.isDirectory(p1)) {
				i--;
			}
			if (Files.isDirectory(p2)) {
				i++;
			}
			if (i != 0) {
				return i;
			}
			return p1.compareTo(p2);
		}
	};

}
