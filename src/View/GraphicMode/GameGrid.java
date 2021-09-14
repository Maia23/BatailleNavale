package View.GraphicMode;

import Controller.GameController;
import Model.CoordinatesInMatrix;
import Model.Grid.GameCases;
import Model.Grid.PlayerGrid;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;

public class GameGrid extends BorderPane {

	private boolean debugMode = false;

	private VBox playerGridView = new VBox();
	private VBox cpuGridView = new VBox();

	public GameGrid(int gridSize) {

		for (int i = 0; i < gridSize; i++) {
			HBox playerRow = new HBox();
			HBox cpuRow = new HBox();
			for (int j = 0; j < gridSize; j++) {
				Case playerCell = new Case(new CoordinatesInMatrix(i, j), GameCases.EMPTY_CASE);
				playerCell.setOnMouseClicked(event -> handlePlayerCaseClick(event));
				playerRow.getChildren().add(playerCell);

				Case cpuCell = new Case(new CoordinatesInMatrix(i, j), GameCases.EMPTY_CASE);
				cpuCell.setOnMouseClicked(event -> handleCpuCaseClick(event));
				cpuRow.getChildren().add(cpuCell);
			}
			playerGridView.getChildren().add(playerRow);
			cpuGridView.getChildren().add(cpuRow);
		}
		setLeft(playerGridView);
		setRight(cpuGridView);
	}

	public static String repeat(int count, String with) {
		return new String(new char[count]).replace("\0", with);
	}

	public static String repeat(int count) {
		return repeat(count, " ");
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();

		int numberOfRows = GameController.getGameControllerInstance().getPlayerGrid().getGrid().length;

		int numberOfColumns = GameController.getGameControllerInstance().getPlayerGrid().getGrid()[0].length;

		String separate = "      ";
		String player = "PLAYER GRID";
		int playerNumberOfSpace = (numberOfColumns * 4 - player.length()) / 2;
		String playerSpace = repeat((playerNumberOfSpace % 2) == 0 ? playerNumberOfSpace + 2 : playerNumberOfSpace);
		String cpu = "CPU GRID";
		int cpuNumberOfSpace = (numberOfColumns * 4 - cpu.length()) / 2;
		String cpuSpace = repeat((cpuNumberOfSpace % 2 == 0) ? cpuNumberOfSpace + 2 : cpuNumberOfSpace);

		// Premiere ligne pour l'appartenance des grilles;
		res.append(playerSpace + player + playerSpace + separate + cpuSpace + cpu + cpuSpace + "\n");

		for (int gridNumber = 0; gridNumber < 2; gridNumber++) {
			res.append(" | ");
			for (int i = 0; i < numberOfColumns; i++) {
				if (i < 10) {
					res.append(i + " | ");
				} else {
					res.append(i + "| ");
				}

			}
			res.append(separate);
		}
		res.append("\n");
		char c = 'a';
		for (int row = 0; row < numberOfRows; row++) {
			for (int gridNumber = 0; gridNumber < 2; gridNumber++) {
				res.append(c + "| ");
				for (int col = 0; col < numberOfColumns; col++) {
					PlayerGrid gameGrid = gridNumber == 0 ? GameController.getGameControllerInstance().getPlayerGrid() : GameController.getGameControllerInstance().getCpuGrid();
					res.append(gameGrid.getGrid()[row][col].toString() + " | ");
				}
				res.append(separate);
			}
			res.append("\n");
			c++;
		}

		return res.toString();
	}

	/**
	 * Updates grid interfaces
	 */
	public void updateGridViews() {
		updatePlayerGridView();
		updateCpuGridView();
	}
	
	public void useFlare() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				CoordinatesInMatrix coords = new CoordinatesInMatrix(i, j);
				Case c = getViewCase(coords, cpuGridView);
				GameCases value = GameController.getGameControllerInstance().getCpuGrid().getValue(coords);
				c.getStyleClass().clear();
				if (!value.toString().equals("*") && !value.toString().equals("S")) {
					c.getStyleClass().add("flared");
				}
			}
		}
	}

	
	private void updatePlayerGridView() {
		for (Node node : playerGridView.getChildren()) {
			HBox child = (HBox) node;	//Children are always HBox representing every line
			for (Node cell : child.getChildren()) {
				Case gridCase = (Case) cell;	//Children are always Case
				GameCases value = GameController.getGameControllerInstance().getPlayerGrid().getValue(gridCase.getCaseCoords());
				gridCase.setCaseType(value);
				gridCase.getStyleClass().clear();
				setCSSclass(gridCase, value);
				updateIfSelected(gridCase);
			}
		}
	}

	private void updateCpuGridView() {
		for (Node node : cpuGridView.getChildren()) {
			HBox child = (HBox) node;	//Children are always HBox representing every line
			for (Node cell : child.getChildren()) {
				Case gridCase = (Case) cell;	//Children are always Case
				GameCases value = GameController.getGameControllerInstance().getCpuGrid().getValue(gridCase.getCaseCoords());
				gridCase.setCaseType(value);
				gridCase.getStyleClass().clear();
				updateIfTargeted(gridCase);
				if (value == GameCases.WRECKAGE_CASE || debugMode) {
					setCSSclass(gridCase, value);
				} else {
					gridCase.getStyleClass().add("empty");
				}
			}
		}
	}
	
	/**
	 * Adds style css classes to cases
	 * @param gridCase
	 * @param value
	 */
	private void setCSSclass(Case gridCase, GameCases value) {
		switch (value.toString()) {
		case "*":
			gridCase.getStyleClass().add("empty");
			break;
		default:
			gridCase.getStyleClass().add(value.toString());
			break;
		}
	}

	/**
	 * Updates case interface if case is selected
	 * @param gridCase
	 */
	private void updateIfSelected(Case gridCase) {
		if (GameController.getGameControllerInstance().getPlayerGrid().getSelectedShip() != null) {
			for (CoordinatesInMatrix selectedCoords : GameController.getGameControllerInstance().getPlayerGrid().getSelectedShip().getCoordinates()) {
				if (selectedCoords.equals(gridCase.getCaseCoords())) {
					gridCase.getStyleClass().add(SELECTED_CSS_CLASS);
				}
			}
		} else {
			gridCase.getStyleClass().remove(SELECTED_CSS_CLASS);
		}
	}

	/**
	 * Updates case interface if case is targeted
	 * @param gridCase
	 */
	private void updateIfTargeted(Case gridCase) {
		if (GameController.getGameControllerInstance().getPlayerGrid().getSelectedShip() != null && GameController.getGameControllerInstance().getCpuGrid().getTargetedCell() != null && GameController.getGameControllerInstance().getPlayerGrid().getPossibleHits(GameController.getGameControllerInstance().getCpuGrid().getTargetedCell(), GameController.getGameControllerInstance().getPlayerGrid().getSelectedShip()).contains(gridCase.getCaseCoords())) {
			getViewCase(gridCase.getCaseCoords(), cpuGridView).getStyleClass().add(TARGETED_CSS_CLASS);
		} else {
			gridCase.getStyleClass().remove(TARGETED_CSS_CLASS);
		}
	}

	public Case getViewCase(CoordinatesInMatrix coords, VBox gridView) {
		return (Case) (((HBox) gridView.getChildren().get(coords.getLine())).getChildren().get(coords.getColumn()));
	}

	public void handlePlayerCaseClick(MouseEvent event) {
		Case c = (Case) event.getTarget();
		GameController.getGameControllerInstance().selectShip(c.getCaseCoords());
		updateGridViews();
	}
	
	public void handleCpuCaseClick(MouseEvent event) {
		Case c = (Case) event.getTarget();
		GameController.getGameControllerInstance().targetCell(c.getCaseCoords());
		updateGridViews();
	}

	public void playLaunchSound() {
		//LAUNCH_AUDIO.play();
		//waitSoundEnd(LAUNCH_AUDIO);
	}

	public void playImpactSound() {
		IMPACT_AUDIO.play();
		GameController.getGameControllerInstance().sleep(100);
		//waitSoundEnd(IMPACT_AUDIO);
	}

	public void playMissSound() {
		MISS_AUDIO.play();
		GameController.getGameControllerInstance().sleep(100);
		//waitSoundEnd(MISS_AUDIO);
	}

	private void waitSoundEnd(AudioClip audio) {
		try {
			while (audio.isPlaying()) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}


	public void initializeAudioClip(String sourceLaunchSound, String impactSound, String missSound) {
		LAUNCH_AUDIO = new AudioClip(sourceLaunchSound);
		IMPACT_AUDIO = new AudioClip(impactSound);
		MISS_AUDIO = new AudioClip(missSound);
	}

	private final static String SELECTED_CSS_CLASS = "selected";
	private final static String TARGETED_CSS_CLASS = "target";

	private static AudioClip LAUNCH_AUDIO = null;
	private static AudioClip IMPACT_AUDIO = null;
	private static AudioClip MISS_AUDIO = null;

}
