package application;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import application.create.CreateDialogController;
import application.preferences.PreferencesDialogController;
import application.server.ServerPageController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.ServerConfig;

public class MainSceneController implements Initializable {
	private static final Path PREFERENCES_PROPERTIES = Paths.get("preferences.properties");

	private static final String EULA_COMMENT = "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).";

	private Properties preferences;

	private Set<ServerConfig> serverConfigs;

	private Map<ServerConfig, Tab> tabMap;

	@FXML
	private TreeView<Object> treeView;

	@FXML
	private TabPane tabPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializePreferences();

		tabMap = new HashMap<>();
		serverConfigs = new HashSet<>(Utils.loadServerConfigs());
		initializeTreeView();
		refreshTreeView();
		if (serverConfigs.isEmpty()) {
			createServer();
		}
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

	private void refreshTreeView() {
		TreeItem<Object> root = treeView.getRoot();
		root.getChildren().clear();

		for (ServerConfig serverConfig : serverConfigs) {
			TreeItem<Object> serverItem = buildTreeItem(serverConfig.getServerDirectory());
			serverItem.setValue(serverConfig);
			root.getChildren().add(serverItem);
		}
	}

	private TreeItem<Object> buildTreeItem(Path path) {
		TreeItem<Object> item = new TreeItem<>();
		item.setValue(path);

		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
				stream.forEach(childPath -> {
					TreeItem<Object> child = buildTreeItem(childPath);
					item.getChildren().add(child);
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		item.getChildren().sort(TREE_ITEM_COMPARATOR);

		return item;
	}

	private void selectServerTab(ServerConfig serverConfig) {
		Tab tab;
		if (tabMap.containsKey(serverConfig)) {
			tab = tabMap.get(serverConfig);
		} else {
			tab = createServerTab(serverConfig);
			tabMap.put(serverConfig, tab);
		}

		if (!tabPane.getTabs().contains(tab)) {
			tabPane.getTabs().add(tab);
		}
		tabPane.getSelectionModel().select(tab);
	}

	private Tab createServerTab(ServerConfig serverConfig) {
		Tab tab = new Tab();
		try {
			FXMLLoader loader = Utils.fxmlLoader(ServerPageController.class);
			Parent parent = loader.load();
			ServerPageController controller = loader.getController();
			controller.setServerConfig(serverConfig);
			tab.setContent(parent);
			tab.setText(serverConfig.getServerName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tab;
	}

	public void createServer() {
		try {
			FXMLLoader loader = Utils.fxmlLoader(CreateDialogController.class);
			Dialog<ButtonType> dialog = loader.load();
			CreateDialogController controller = loader.getController();
			controller.setServerConfigs(serverConfigs);
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
				serverConfigs.add(serverConfig);

				// Save data
				saveAllServers();
				acceptEula(serverConfig);

				// Refresh UI
				refreshTreeView();
				selectServerTab(serverConfig);
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
		Utils.saveServerConfigs(serverConfigs);
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

	private void initializeTreeView() {
		TreeItem<Object> root = new TreeItem<>("Servers");
		treeView.setRoot(root);

		treeView.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
			@Override
			public TreeCell<Object> call(TreeView<Object> param) {
				TextFieldTreeCell<Object> treeCell = new TextFieldTreeCell<Object>(new StringConverter<Object>() {
					@Override
					public String toString(Object object) {
						if (object instanceof ServerConfig) {
							return ((ServerConfig) object).getServerName();
						} else if (object instanceof Path) {
							return ((Path) object).getFileName().toString();
						} else {
							return object.toString();
						}
					}

					@Override
					public Object fromString(String string) {
						return string;
					}
				});
				treeCell.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
					boolean doubleClick = e.getClickCount() % 2 == 0;
					if (doubleClick && Objects.equals(e.getButton(), MouseButton.PRIMARY))
						e.consume();
				});
				return treeCell;
			}
		});

		treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				boolean doubleClick = e.getClickCount() % 2 == 0;
				TreeItem<Object> selectedItem = treeView.getSelectionModel().getSelectedItem();
				if (doubleClick && selectedItem != null) {
					if (selectedItem.getValue() instanceof ServerConfig) {
						selectServerTab((ServerConfig) selectedItem.getValue());
					} else if (selectedItem.getValue() instanceof Path) {
						String absolutePath = ((Path) selectedItem.getValue()).toAbsolutePath().toString();
						MinecraftServerManager.INSTANCE.getHostServices().showDocument(absolutePath);
					}
				}
			}
		});

		treeView.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (Objects.equals(KeyCode.F5, event.getCode())) {
					refreshTreeView();
				}
			}
		});
	}

	private Comparator<TreeItem<Object>> TREE_ITEM_COMPARATOR = new Comparator<TreeItem<Object>>() {
		@Override
		public int compare(TreeItem<Object> t1, TreeItem<Object> t2) {
			Object o1 = t1.getValue();
			Object o2 = t2.getValue();
			if (o1 instanceof Path && o2 instanceof Path) {
				Path p1 = (Path) o1;
				Path p2 = (Path) o2;
				int a = 0;
				if (Files.isDirectory(p1)) {
					a--;
				} else if (Files.isDirectory(p2)) {
					a++;
				}
				if (a != 0) {
					return a;
				}
			}
			return StringUtils.compare(o1.toString(), o2.toString());
		}
	};

}
