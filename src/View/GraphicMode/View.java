package View.GraphicMode;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Optional;

import Controller.GameController;
import Model.Grid.PlayerGrid.Direction;
import SaveGame.FileUtils;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class View extends Application {

	private static Stage primaryStage;

	private static ScreenController screenController;

	private static GameGrid gameGrid;

	private static TextFlow console;

	private static ScrollPane scrollPane;

	private static Button soundButton;

	private static WebView webViewRules;

	private static WebView webViewKeys;

	private static Button loadGameBtn;

	private static Text victoryText;

	/**
	 * Initiates graphic interface and loads all scenes from fxml files
	 */
	@Override
	public void start(Stage primaryStage) {
		try {

			View.primaryStage = primaryStage;
			View.primaryStage.getIcons().add(new Image(WINDOW_ICON_PATH));

			// MenuScene
			FXMLLoader loaderMenu = new FXMLLoader(getClass().getResource("rsrc/fxFiles/Menu.fxml"));
			GridPane menuRoot = loaderMenu.load();
			loadGameBtn = (Button) loaderMenu.getNamespace().get("loadGameBtn");
			Scene menuScene = new Scene(menuRoot, SCREEN_WIDTH, SCREEN_HEIGHT);
			menuScene.getStylesheets().add(CSS_FILE_PATH);

			// RulesScene
			FXMLLoader loaderRules = new FXMLLoader(getClass().getResource("rsrc/fxFiles/Rules.fxml"));
			TabPane rulesRoot = loaderRules.load();
			webViewRules = (WebView) loaderRules.getNamespace().get("webViewRules");
			webViewKeys = (WebView) loaderRules.getNamespace().get("webViewKeys");
			Scene rulesScene = new Scene(rulesRoot, SCREEN_WIDTH, SCREEN_HEIGHT);
			rulesScene.getStylesheets().add(CSS_FILE_PATH);

			// VictoryScene
			FXMLLoader loaderVictory = new FXMLLoader(getClass().getResource("rsrc/fxFiles/Victory.fxml"));
			AnchorPane victoryRoot = loaderVictory.load();
			Scene victoryScene = new Scene(victoryRoot, SCREEN_WIDTH, SCREEN_HEIGHT);
			victoryScene.getStylesheets().add(CSS_FILE_PATH);

			// DefeatScene
			FXMLLoader loaderDefeat = new FXMLLoader(getClass().getResource("rsrc/fxFiles/Defeat.fxml"));
			AnchorPane defeatRoot = loaderDefeat.load();
			Scene defeatScene = new Scene(defeatRoot, SCREEN_WIDTH, SCREEN_HEIGHT);
			defeatScene.getStylesheets().add(CSS_FILE_PATH);

			// ScreenController
			screenController = new ScreenController();
			screenController.addScreen(MENU_SCREEN_NAME, menuScene);
			screenController.addScreen(RULES_SCREEN_NAME, rulesScene);
			screenController.addScreen(VICTORY_SCREEN_NAME, victoryScene);
			screenController.addScreen(DEFEAT_SCREEN_NAME, defeatScene);
			primaryStage.setTitle("Bataille Navale");
			primaryStage.setResizable(false);
			primaryStage.setScene(menuScene);
			primaryStage.show();

			updateLoadGameBtnStatus();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * LoadGameBtn is disabled if no save file exists
	 */
	public void updateLoadGameBtnStatus() {
		if (FileUtils.fileExists(GameController.getGameControllerInstance().getDefaultSavePath())) {
			loadGameBtn.setDisable(false);
		} else {
			loadGameBtn.setDisable(true);
		}
	}

	private void loadGameScene() {
		try {
			// GameScene
			FXMLLoader loader = new FXMLLoader(getClass().getResource("rsrc/fxFiles/Game.fxml"));
			BorderPane gameRoot = loader.load();
			console = (TextFlow) loader.getNamespace().get("console");
			scrollPane = (ScrollPane) loader.getNamespace().get("consolePane");
			soundButton = (Button) loader.getNamespace().get("soundButton");
			victoryText = (Text) loader.getNamespace().get("victoryText");
			BorderPane gameBoard = new BorderPane();
			GameGrid gameGrid = startGameGrid(GameController.getGameControllerInstance().getGridSize());
			gameGrid.initializeAudioClip(getClass().getResource("rsrc/audio/missile_launch.mp3").toURI().toString(),
					getClass().getResource("rsrc/audio/missile_impact.mp3").toURI().toString(),
					getClass().getResource("rsrc/audio/missile_miss.mp3").toURI().toString());
			gameGrid.updateGridViews();
			gameBoard.setCenter(gameGrid);
			gameBoard.setId("gameBoard");
			gameRoot.setCenter(gameBoard);

			Scene gameScene = new Scene(gameRoot, SCREEN_WIDTH, SCREEN_HEIGHT);
			gameScene.getStylesheets().add(CSS_FILE_PATH);

			screenController.addScreen(GAME_SCREEN_NAME, gameScene);
		} catch (Exception e) {
			throw new RuntimeException("Exception thrown while charging GameScene", e);
		}
	}

	public static void playerWin() {
		screenController.activateScreen(VICTORY_SCREEN_NAME);
	}

	public static void cpuWin() {
		victoryText.setText("You lost, better luck next time!");
		victoryText.setVisible(true);
	}

	@FXML
	void handleFireAction(ActionEvent event) {
		if (GameController.getGameControllerInstance().isPlayerTurn()) {
			GameController.getGameControllerInstance().playerAttack();
		}
	}

	@FXML
	void handleMouvement(ActionEvent event) {
		if (GameController.getGameControllerInstance().isPlayerTurn()) {
			String btnPressed = ((Button) event.getSource()).getText();
			Direction mouvementDirection = null;
			switch (btnPressed) {
			case "UP":
				mouvementDirection = Direction.UP;
				break;
			case "DOWN":
				mouvementDirection = Direction.DOWN;
				break;
			case "LEFT":
				mouvementDirection = Direction.LEFT;
				break;
			case "RIGHT":
				mouvementDirection = Direction.RIGHT;
				break;
			}

			GameController.getGameControllerInstance().moveShip(mouvementDirection);
		}
	}

	@FXML
	void returnMenu(ActionEvent event) {
		if (screenController.getActiveScreenName().equals(GAME_SCREEN_NAME)) {
			if (!generateConfirmationWindow("Quitter", "Etes vous sur de vouloir quitter la partie?",
					"Vous perdrez tout progres non sauvegard�!")) {
				return;
			}
		}
		screenController.activateScreen("menu");
	}

	@FXML
	void loadGame(ActionEvent event) {
		if (GameController.getGameControllerInstance().loadGame()) {
			loadGameScene();
			screenController.activateScreen(GAME_SCREEN_NAME);
		}
	}

	@FXML
	void saveGame(ActionEvent event) {
		if (GameController.getGameControllerInstance().isPlayerTurn()) {
			GameController.getGameControllerInstance().saveGame();
			updateLoadGameBtnStatus();
		} else {
			Log.write("Wait for your turn to save the game please!", 3);
		}
	}

	@FXML
	void showRules(ActionEvent event) throws URISyntaxException {
		webViewRules.getEngine().load(View.class.getResource("rsrc/html/rules.html").toURI().toString());
		webViewKeys.getEngine().load(View.class.getResource("rsrc/html/keys.html").toURI().toString());
		screenController.activateScreen("rules");
	}

	@FXML
	void exit(ActionEvent event) {
		primaryStage.close();
	}

	@FXML
	void launchNewGame(ActionEvent event) throws IOException {
		GameController.getGameControllerInstance().generateNewGame();
		loadGameScene();
		console.getChildren().clear();
		screenController.activateScreen(GAME_SCREEN_NAME);
	}

	@FXML
	void changeSoundStatus(ActionEvent event) {
		if (GameController.getGameControllerInstance().isPlayerTurn()) {
			if (soundButton.getStyleClass().contains("soundButtonOff")) {
				soundButton.getStyleClass().remove("soundButtonOff");
				GameController.getGameControllerInstance().setPlaySounds(true);
			} else {
				soundButton.getStyleClass().add("soundButtonOff");
				GameController.getGameControllerInstance().setPlaySounds(false);
			}
		}
	}

	@FXML
	void showSourangLinkedIn(ActionEvent event) throws IOException, URISyntaxException {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			Desktop.getDesktop().browse(new URI(SOURANG_LINKEDIN));
		}
	}

	@FXML
	void showMaiaLinkedIn(ActionEvent event) throws IOException, URISyntaxException {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			Desktop.getDesktop().browse(new URI(MAIA_LINKEDIN));
		}
	}

	public static GameGrid startGameGrid(int gridSize) {
		gameGrid = new GameGrid(gridSize);
		return gameGrid;
	}

	public static GameGrid getGameGrid() {
		return gameGrid;
	}

	/**
	 * Generates a graphical confirmation window
	 * 
	 * @param Title    to window
	 * @param Window's header
	 * @param text     to be displayed
	 * @return true if user clicked on confirm, false otherwise
	 */
	public static boolean generateConfirmationWindow(String titleWindow, String header, String text) {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle(titleWindow);
		alert.setHeaderText(header);
		alert.setContentText(text);

		ButtonType confirmBtn = new ButtonType("Confirmer");
		ButtonType cancelBtn = new ButtonType("Annuler");

		alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == confirmBtn) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Generates graphical option window
	 * 
	 * @param titleWindow
	 * @param header
	 * @param text        to be displayed
	 * @param options     for user (unlimited)
	 * @return string from chosen option
	 */
	public static String generateOptionWindow(String titleWindow, String header, String text, String... options) {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(titleWindow);
		alert.setHeaderText(header);
		alert.setContentText(text);

		ButtonType cancelBtn = new ButtonType("Annuler");
		alert.getButtonTypes().setAll(cancelBtn);
		for (int i = 0; i < options.length; i++) {
			alert.getButtonTypes().add(alert.getButtonTypes().size() - 1, new ButtonType(options[i]));
		}

		Optional<ButtonType> result = alert.showAndWait();
		return result.toString();
	}

	/**
	 * Generates a graphical informational window
	 * 
	 * @param titleWindow
	 * @param header
	 * @param text        to be displayed
	 */
	public static void generateInformationWindow(String titleWindow, String header, String text) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(primaryStage);
		alert.setTitle(titleWindow);
		alert.setHeaderText(header);
		alert.setContentText(text);
		alert.showAndWait();
	}

	public void launch() {
		launch("");
	}

	/**
	 * Class allowing to change graphical Scenes
	 * 
	 * @author Maia
	 *
	 */
	private class ScreenController {

		private String activeScreen = null;
		private HashMap<String, Scene> screens = new HashMap<String, Scene>();

		public void addScreen(String name, Scene screen) {
			if (screens.get(name) != null) {
				screens.remove(name);
			}
			screens.put(name, screen);
		}

		public Scene getScreen(String name) {
			return screens.get(name);
		}

		public String getActiveScreenName() {
			return activeScreen;
		}

		public void activateScreen(String name) {
			if (!screens.containsKey(name)) {
				throw new IllegalArgumentException("Screen is not referenced!");
			}
			GameController.getGameControllerInstance().sleep(TRANSITION_TIME);
			View.primaryStage.setScene(screens.get(name));
			activeScreen = name;
		}

	}

	/**
	 * Logger that allows to print messages on graphical game's console
	 * 
	 * @author alexa
	 *
	 */
	public static class Log {

		private static boolean DEBUG = false;

		/**
		 * Allows to write a message on Game's console
		 * 
		 * @param message      you wish to print out
		 * @param level        of message, 1 -> Normal, 2 -> Important, 3 -> Error
		 * @param debugMessage indicates if a message shows on normal or debug logs
		 */
		public static void write(String message, int level, boolean debugMessage) {
			if (!debugMessage || DEBUG) {
				Text res = debugMessage ? setStyle("\n  -> DEBUG: " + message, level)
						: setStyle("\n  -> " + message, level);
				console.getChildren().add(res);
				scrollPane.vvalueProperty().bind(console.heightProperty());
			}
		}

		/**
		 * Allows to write a message on Game's console
		 * 
		 * @param message you wish to print out
		 * @param level   of message, 1 -> Normal, 2 -> Important, 3 -> Error
		 */
		public static void write(String message, int level) {
			write(message, level, false);
		}

		private static Text setStyle(String text, int level) {
			Text res = new Text(text);

			switch (level) {
			case 3:
				res.getStyleClass().add("level3");
			case 2:
				res.getStyleClass().add("level2");
				return res;
			}
			res.setStyle("-fx-fill: white");
			return res;
		}
	}

	private final static int SCREEN_WIDTH = 1000;
	private final static int SCREEN_HEIGHT = 800;

	private final static long TRANSITION_TIME = 100;
	private final static String WINDOW_ICON_PATH = "View/GraphicMode/rsrc/icons/windowIcon.png";
	private final static String CSS_FILE_PATH = "View/GraphicMode/rsrc/fxFiles/application.css";

	private final static String MENU_SCREEN_NAME = "menu";
	private final static String GAME_SCREEN_NAME = "game";
	private final static String RULES_SCREEN_NAME = "rules";
	private final static String WEB_SCREEN_NAME = "webView";
	private final static String VICTORY_SCREEN_NAME = "victory";
	private final static String DEFEAT_SCREEN_NAME = "defeat";

	private final static String SOURANG_LINKEDIN = "https://www.linkedin.com/in/massourang-sourang-751208185/";
	private final static String MAIA_LINKEDIN = "https://www.linkedin.com/in/alexandre-maia-7b00b9175/";

}
