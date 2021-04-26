package org.alver415.minecraft.server.wrapper;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.NotImplementedException;

import org.alver415.minecraft.server.wrapper.create.CreateDialogController;
import org.alver415.minecraft.server.wrapper.input.ConversionException;
import org.alver415.minecraft.server.wrapper.input.Property;
import org.alver415.minecraft.server.wrapper.model.ServerConfig;
import org.alver415.minecraft.server.wrapper.preferences.PreferencesDialogController;
import org.alver415.minecraft.server.wrapper.properties.PropertiesDialogController;
import org.alver415.minecraft.server.wrapper.server.ServerPageController;
import org.alver415.minecraft.server.wrapper.watcher.PathWatcher;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class MainSceneController implements Initializable {
	
	private static final Path PREFERENCES_PROPERTIES = Paths.get("preferences.properties");

	private static final String EULA_COMMENT = "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).";

	private static final String CSS_FX_BASE = "-fx-base: rgba(%1$d, %1$d, %1$d, 255);";

	private PathWatcher pathWatcher;
	private Properties preferences;

	private final BidiMap<ServerConfig, TitledPane> paneMap = new DualHashBidiMap<>();
	private final BidiMap<ServerConfig, Tab> tabMap = new DualHashBidiMap<>();

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
		try {
			pathWatcher = new PathWatcher();
		} catch (IOException e) {
			e.printStackTrace();
		}

		darkModeToggle.setOnAction(event -> {
			scene.getRoot().setStyle(getCss());
		});
		darkModeToggle.fire();
		
		tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			ServerConfig serverConfig = tabMap.getKey(newValue);
			setSelectedServer(serverConfig);
		});

		initializePreferences();
		Utils.loadServerConfigs().forEach(serverConfig -> createServerPane(serverConfig));

		getServerConfigs().stream().findFirst()
			.ifPresentOrElse(serverConfig -> setSelectedServer(serverConfig), () -> createServer());
	}

	public String getCss() {
		boolean darkMode = darkModeToggle.isSelected();
		String css = String.format(CSS_FX_BASE, darkMode ? 21 : 236);
		return css;
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
			try {
				preferences = Utils.loadProperties(PREFERENCES_PROPERTIES);
			} catch (IOException e) {
				e.printStackTrace();
				new Alert(AlertType.ERROR, "Failed to load 'preferences.properties'.").showAndWait();
			}
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
		Path serverDirectory = serverConfig.getServerDirectory();
		TreeItem<Path> root = buildTreeItem(serverDirectory);
		TreeView<Path> treeView = new TreeView<>(root);

		MenuItem open = new MenuItem("Open");
		open.setOnAction(e -> Utils.openFile(treeView.getSelectionModel().getSelectedItem().getValue()));
		MenuItem rename = new MenuItem("Rename");
		rename.setOnAction(e -> {
			TreeItem<Path> selectedItem = treeView.getSelectionModel().getSelectedItem();
			if (selectedItem == treeView.getRoot()) {
				new Alert(AlertType.WARNING, "You can't rename the base directory").showAndWait();
			} else {
				Path path = selectedItem.getValue();
				try {
					Utils.renameFile(path);
				} catch (IOException e1) {
					e1.printStackTrace();
					new Alert(AlertType.ERROR, "Error renaming file.").showAndWait();
				}
			}
		});
		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(event -> {
			TreeItem<Path> selectedItem = treeView.getSelectionModel().getSelectedItem();
			if (selectedItem == treeView.getRoot()) {
				new Alert(AlertType.WARNING, "You can't delete the base directory").showAndWait();
			} else {
				Path path = selectedItem.getValue();
				try {
					Utils.deleteFile(path);
				} catch (IOException e) {
					e.printStackTrace();
					new Alert(AlertType.ERROR, "Error deleting file.").showAndWait();
				}
			}
		});

		treeView.setContextMenu(new ContextMenu(open, rename, delete));

		treeView.setCellFactory(new Callback<TreeView<Path>, TreeCell<Path>>() {
			@Override
			public TreeCell<Path> call(TreeView<Path> treeView) {
				return new TextFieldTreeCell<Path>(new StringConverter<Path>() {
					@Override
					public String toString(Path object) {
						return object.getFileName().toString();
					}

					@Override
					public Path fromString(String string) {
						throw new NotImplementedException();
					}
				});
			}
		});
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
				paneMap.get(serverConfig).setExpanded(false);
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

	private TreeItem<Path> buildTreeItem(Path path) {
		TreeItem<Path> item = new TreeItem<>();
		item.setValue(path);

		if (Files.isDirectory(path)) {
			try {
				pathWatcher.register(path, ENTRY_CREATE, (event) -> {
					Path createdPath = event.context();
					Utils.runFX(() -> {
						item.getChildren().add(buildTreeItem(path.resolve(createdPath)));
						item.getChildren().sort(TREE_ITEM_COMPARATOR);
					});
					System.out.println("CREATE: " + createdPath);
				});
				pathWatcher.register(path, ENTRY_DELETE, (event) -> {
					Path deletedPath = event.context();
					System.out.println("DELETE: " + deletedPath);
					Utils.runFX(() -> {
						item.getChildren().stream().filter(child -> child.getValue().equals(path.resolve(deletedPath)))
								.findFirst().ifPresent(child -> item.getChildren().remove(child));
					});
				});
			} catch (IOException e) {
			}

			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
				stream.forEach(childPath -> {
					item.getChildren().add(buildTreeItem(childPath));
				});
				item.getChildren().sort(TREE_ITEM_COMPARATOR);
				item.setExpanded(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return item;

	}

	private void initializeServer(ServerConfig serverConfig) {
		// Save data
		Utils.runFX(() -> {
			setSelectedServer(serverConfig);
			saveAllServers();
			acceptEula(serverConfig);
		});
	}

	public void createServer() {
		try {
			FXMLLoader loader = Utils.fxmlLoader(CreateDialogController.class);
			Dialog<ButtonType> dialog = loader.load();
			CreateDialogController controller = loader.getController();
			controller.setServerConfigs(getServerConfigs());
			controller.setDefaultServerDirectory(Paths.get(preferences.getProperty("default.server.directory")));
			dialog.showAndWait().filter(response -> response.getButtonData().isDefaultButton()).ifPresent(response -> {
				ServerConfig serverConfig = new ServerConfig();
				serverConfig.setServerName(controller.getServerName());
				serverConfig.setServerDirectory(controller.getServerDirectory());
				serverConfig.setMinecraftServerJar(Paths.get(preferences.getProperty("default.minecraft.jar")));
				serverConfig.setMaximumMemory(preferences.getProperty("default.maximum.memory"));
				serverConfig.setInitialMemory(preferences.getProperty("default.initial.memory"));

				initializeServer(serverConfig);
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void importServer() {
		try {
			FXMLLoader loader = Utils.fxmlLoader(CreateDialogController.class);
			Dialog<ButtonType> dialog = loader.load();
			CreateDialogController controller = loader.getController();
			dialog.setTitle("Import Server");
			controller.setServerConfigs(getServerConfigs());
			controller.setDefaultServerDirectory(Paths.get(preferences.getProperty("default.server.directory")));
			dialog.showAndWait().filter(response -> Objects.equals(response.getButtonData(), ButtonData.OK_DONE))
					.ifPresent(response -> {
						// Create new ServerConfig
						ServerConfig serverConfig = new ServerConfig();
						serverConfig.setServerName(controller.getServerName());
						serverConfig.setServerDirectory(controller.getServerDirectory());
						serverConfig.setMinecraftServerJar(Paths.get(preferences.getProperty("default.minecraft.jar")));
						serverConfig.setMaximumMemory(preferences.getProperty("default.maximum.memory"));
						serverConfig.setInitialMemory(preferences.getProperty("default.initial.memory"));

						initializeServer(serverConfig);
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteServer() {
		Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
		ServerConfig selectedConfig = tabMap.getKey(selectedTab);
		TitledPane pane = paneMap.get(selectedConfig);

		tabPane.getTabs().remove(selectedTab);
		tabMap.remove(selectedConfig);

		accordion.getPanes().remove(pane);
		paneMap.remove(selectedConfig);

		Utils.startThread(() -> saveAllServers());
	}

	private void acceptEula(ServerConfig serverConfig) {
		Path eulaPath = Paths.get(serverConfig.getServerDirectory() + "/eula.txt");
		Properties props = new Properties();
		props.setProperty("eula", "true");
		Utils.storeProperties(props, eulaPath, EULA_COMMENT);
	}

	public void saveAllServers() {
		Utils.saveServerConfigs(getServerConfigs());
	}

	public void openPropertiesDialog() {
		try {
			FXMLLoader loader = Utils.fxmlLoader(PropertiesDialogController.class);
			Dialog<ButtonType> dialog = loader.load();
			dialog.getDialogPane().setStyle(getCss());
			PropertiesDialogController controller = loader.getController();

			ServerConfig selectedServer = getSelectedServer();
			Path serverDirectory = selectedServer.getServerDirectory();
			Path propertiesPath = serverDirectory.resolve("server.properties");
			Properties serverProperties = Utils.loadProperties(propertiesPath);
			Collection<Property<?>> properties = Utils.loadServerProperties();
			for (Property<?> property : properties) {
				try {
					String propertyValue = serverProperties.getProperty(property.getKey());
					property.setValueString(propertyValue);
				} catch (ConversionException e) {
					e.printStackTrace();
				}
			}
			controller.setProperties(properties);

			dialog.showAndWait().filter(response -> {
				return Objects.equals(response.getButtonData(), ButtonData.OK_DONE);
			}).ifPresent(response -> {
				selectedServer.setServerProperties(controller.asProperties());
				Utils.storeProperties(controller.asProperties(), propertiesPath);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private ServerConfig getSelectedServer() {
		Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
		ServerConfig selectedServer = tabMap.getKey(selectedTab);
		return selectedServer;
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
