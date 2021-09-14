package Controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import Model.CoordinatesInMatrix;
import Model.Ship;
import Model.Grid.ComputerGrid;
import Model.Grid.PlayerGrid;
import Model.Grid.PlayerGrid.Direction;
import Model.Ships.Battleship;
import Model.Ships.Cruiser;
import Model.Ships.Destroyer;
import Model.Ships.Submarine;
import SaveGame.FileUtils;
import View.GraphicMode.View;
import View.GraphicMode.View.Log;

/**
 * Class defining the Game's spinal cord
 * 
 * @author Maia
 *
 */
public class GameController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * ############################## Constants ###################################
	 */
	private static GameController GAME_CONTROLLER = new GameController();

	private static int GRID_SIZE = 15;

	public static boolean playSounds = true;

	private static String defaultGameSavePath = "src/SaveGame/saveFiles/save.txt";

	private final ArrayList<Ship> SHIPS_TO_PLACE = new ArrayList<>(
			Arrays.asList(new Battleship(), new Cruiser(), new Cruiser(), new Destroyer(), new Destroyer(),
					new Destroyer(), new Submarine(), new Submarine(), new Submarine(), new Submarine()));

	/* ############################## Model ################################### */
	private static PlayerGrid playerGrid;
	private static ComputerGrid cpuGrid;

	private static boolean isPlayerTurn = true;

	/* ############################## View ################################### */

	/**
	 * Allows to retrieve the only existing instance of the GameController
	 * (singleton)
	 * 
	 * @return GameController instance
	 */
	public static GameController getGameControllerInstance() {
		return GAME_CONTROLLER;
	}

	public void switchPlayers() {
		isPlayerTurn = !isPlayerTurn;
		if (!isPlayerTurn) {
			botTurn();
		}
	}

	private GameController() {

	}

	/**
	 * Method allows to create a new Game, with new Grids
	 */
	public void generateNewGame() {
		View.startGameGrid(GRID_SIZE);
		playerGrid = new PlayerGrid(GRID_SIZE);
		cpuGrid = new ComputerGrid(GRID_SIZE);
		placeShips();
	}

	/**
	 * Place ships randomly on a Grid
	 */
	public void placeShips() {

		for (Ship ship : SHIPS_TO_PLACE) {

			Ship ship1 = ship.clone();
			Ship ship2 = ship.clone();

			ship1.setCoordinates(playerGrid.getRandomEmptySpace(ship.getShipSize()));
			playerGrid.placeShip(ship1);

			ship2.setCoordinates(cpuGrid.getRandomEmptySpace(ship.getShipSize()));
			cpuGrid.placeShip(ship2);
		}

		View.getGameGrid().updateGridViews();
	}

	/**
	 * Moves ship in grid
	 * 
	 * @param direction of mouvement
	 * @return true if ship was moved, false otherwise
	 */
	public boolean moveShip(Direction direction) {

		if (playerGrid.getSelectedShip() != null) {
			playerGrid.moveShip(playerGrid.getSelectedShip(), direction);
			View.getGameGrid().updateGridViews();
			return true;
		}

		Log.write("Button cannot be used right now, do not forget to select a ship first :)", 3);
		return false;

	}

	/**
	 * Shoot on enemy grid.
	 * 
	 * According to user's turn, it detects if a ship is selected and if a cell is
	 * targeted on enemy's grid.
	 * 
	 */
	public void fire() {

		PlayerGrid attacker = isPlayerTurn ? playerGrid : cpuGrid;

		PlayerGrid target = isPlayerTurn ? cpuGrid : playerGrid;

		// Flare
		if (attacker.getSelectedShip().hasFlare() && !attacker.getSelectedShip().usedFlare()) {
			View.getGameGrid().useFlare();
			((Destroyer) attacker.getSelectedShip()).useFlare();
			Log.write("Fusee eclairante utilisee!", 1);
		}

		if (attacker.getSelectedShip() != null && target.getTargetedCell() != null) {
			if (playSounds) {
				View.getGameGrid().playLaunchSound();
			}

			// Attack
			if (target.attack(target.getTargetedCell(), attacker.getSelectedShip())) {
				if (playSounds) {
					View.getGameGrid().playImpactSound();
				}
				Log.write("Shot at coordinates : " + target.getTargetedCell().toString(), 1, true);
				target.diselectTargetedCell();
			} else if (playSounds) {
				View.getGameGrid().playMissSound();
			}
		}
		checkIfGameEnded();
	}

	public void botTurn() {
		cpuGrid.attack();
		playerGrid.setTargetedCell(cpuGrid.getTargetedCell());
		fire();
		sleep(3000);
		View.getGameGrid().updateGridViews();
		switchPlayers();
	}

	/**
	 * Allows to select a ship on a grid
	 * 
	 * @param clicked coordinates
	 */
	public void selectShip(CoordinatesInMatrix c) {
		if (isPlayerTurn()) {
			playerGrid.selectShip(new CoordinatesInMatrix(c.getLine(), c.getColumn()));
		} else {
			cpuGrid.selectShip(new CoordinatesInMatrix(c.getLine(), c.getColumn()));
		}
	}

	/**
	 * Allows to target a cell on enemy's grid
	 * 
	 * @param coordinates to be targeted
	 */
	public void targetCell(CoordinatesInMatrix c) {
		if (isPlayerTurn()) {
			if (playerGrid.getSelectedShip() != null) {
				cpuGrid.targetCell(c);
			} else {
				Log.write("No ship is selected for player!", 3, true);
			}
		} else {
			if (cpuGrid.getSelectedShip() != null) {
				playerGrid.targetCell(c);
			} else {
				Log.write("No ship is selected for CPU!", 3, true);
			}
		}
	}

	public void saveGame() {
		System.out.println("save");
		if (!FileUtils.fileExists(defaultGameSavePath)
				|| (FileUtils.fileExists(defaultGameSavePath) && View.generateConfirmationWindow("Confirmation", "",
						"Vous avez deja une partie sauvegardee, voulez vous l'ecraser?"))) {
			SaveManager.saveGameControllerAttributes();
			View.generateInformationWindow("Information", "", "Your game has been saved!");
		}
	}

	public boolean loadGame() {
		if (SaveManager.loadSavedGameControllerAttributes()) {
			return true;
		}
		return false;
	}

	public void checkIfGameEnded() {
		if (checkIfPlayerWon()) {
			View.playerWin();
		} else if (checkIfCpuWon()) {
			View.cpuWin();
		}

	}

	private boolean checkIfPlayerWon() {
		if (cpuGrid.getAvailableShips().size() == 0) {
			return true;
		}

		return false;
	}

	private boolean checkIfCpuWon() {
		if (playerGrid.getAvailableShips().size() == 0) {
			return true;
		}

		return false;
	}

	/**
	 * Makes the thread sleep for the given time
	 * 
	 * @param milis to be paused
	 */
	public void sleep(long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			System.err.println("Error during thread sleep!");
			throw new RuntimeException(e);
		}
	}

	// GETTERS
	public boolean isPlayerTurn() {
		return isPlayerTurn;
	}

	public PlayerGrid getPlayerGrid() {
		return playerGrid;
	}

	public ComputerGrid getCpuGrid() {
		return cpuGrid;
	}

	public void setPlaySounds(boolean playSounds) {
		this.playSounds = playSounds;
	}

	public int getGridSize() {
		return GRID_SIZE;
	}

	public String getDefaultSavePath() {
		return defaultGameSavePath;
	}

	/**
	 * Class managing saves and loading of games
	 * 
	 * @author Maia
	 *
	 */
	private abstract static class SaveManager {

		/**
		 * Method writing GameController parameteres on a text file
		 */
		public static void saveGameControllerAttributes() {
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(defaultGameSavePath));
				out.writeObject(GRID_SIZE);
				out.writeObject(playerGrid);
				out.writeObject(cpuGrid);
				out.writeObject(isPlayerTurn);
				out.flush();
				out.close();
			} catch (Exception e) {
				System.err.println("Exception thrown, could not save file!");
				throw new RuntimeException(e);
			}
		}

		/**
		 * Method responsible from retrieving saved attributes from text file
		 * 
		 * @return
		 */
		public static boolean loadSavedGameControllerAttributes() {
			try {
				if (FileUtils.fileExists(defaultGameSavePath)) {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(defaultGameSavePath));

					GRID_SIZE = (int) in.readObject();
					playerGrid = (PlayerGrid) in.readObject();
					cpuGrid = (ComputerGrid) in.readObject();
					isPlayerTurn = (boolean) in.readObject();

					in.close();
					return true;
				}

				return false;
			} catch (Exception e) {
				System.err.println("Exception thrown, could not load file!");
				throw new RuntimeException(e);
			}
		}
	}

	public void playerAttack() {
		fire();
		switchPlayers();
	}

}
