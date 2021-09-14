package Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import Model.CoordinatesInMatrix;
import Model.Ship;
import Model.Grid.ComputerGrid;
import Model.Grid.PlayerGrid;
import Model.Grid.PlayerGrid.Direction;
import Model.Ships.Battleship;
import Model.Ships.Cruiser;
import Model.Ships.Destroyer;
import Model.Ships.Submarine;
import View.ConsoleMode.ConsoleView;

public class ConsoleGame {

	/*
	 * ############################## Constants ###################################
	 */

	private final static int GRID_SIZE = 15;

	public static boolean playSounds = true;

	private final ArrayList<Ship> SHIPS_TO_PLACE = new ArrayList<>(
			Arrays.asList(new Battleship(), new Cruiser(), new Cruiser(), new Destroyer(), new Destroyer(),
					new Destroyer(), new Submarine(), new Submarine(), new Submarine(), new Submarine()));

	/* ############################## Model ################################### */
	private static PlayerGrid playerGrid;
	private static ComputerGrid cpuGrid;
	private boolean isPlayerTurn = true;
	private Scanner sc = new Scanner(System.in);

	public ConsoleGame() {
		generateNewGame();
	}

	// A completer
	public ConsoleGame(String path) {

	}

	public void play() {
		displayGame();
		while (true) {
			if (isPlayerTurn) {
				ConsoleView.showPlayerTurn();
				ConsoleView.skipLines(1);
				sleep(2000);
				if (pickAction() == 1) {
					attackShip();
				} else {
					moveShip();
				}
			} else {
				ConsoleView.showCpuTurn();
				ConsoleView.skipLines(1);
				sleep(2000);
				if (cpuGrid.pickAction() == 1) {
					attackShip();
				} else {
					moveShip();
				}
			}
			if (isOver()) {
				break;
			}
			sleep(100);
			isPlayerTurn = !isPlayerTurn;
			ConsoleView.skipLines(3);
		}

	}

	public int pickAction() {
		while (true) {
			ConsoleView.showActionMenu();

			String response = sc.next();
			ConsoleView.skipLines(1);
			if (isValidResponse(response, 1, 2)) {
				return Integer.parseInt(response);
			}

		}
	}

	public Ship pickShipToAttack() {
		ArrayList<Ship> availableShips = getAttacker().getAvailableShips();
		return pickShipFromList(availableShips);
	}

	public Ship pickShipToMove() {
		ArrayList<Ship> movableShips = getAttacker().getMovableShips();
		return pickShipFromList(movableShips);
	}

	public Ship pickShipFromList(ArrayList<Ship> listOfShips) {
		while (true) {
			for (int i = 0; i < listOfShips.size(); i++) {
				ConsoleView.showShipOption(getAttacker().getAvailableShips().get(i), i + 1);
			}

			String response = sc.next();
			if (isValidResponse(response, 1, listOfShips.size())) {
				Ship shipSelected = listOfShips.get(Integer.parseInt(response) - 1);
				ConsoleView.showShipSelected(shipSelected);
				return shipSelected;
			}

			ConsoleView.skipLines(1);
		}
	}

	public CoordinatesInMatrix pickTargetedCoordinates() {
		ConsoleView.showTargetRequest();
		int x = pickCoordinate("X");
		int y = pickCoordinate("Y");
		CoordinatesInMatrix selectedCoordinates = new CoordinatesInMatrix(x, y);
		ConsoleView.showCoordsSelected(selectedCoordinates);
		return selectedCoordinates;
	}

	public int pickCoordinate(String axis) {
		while (true) {
			ConsoleView.showCoordinateRequest(axis);
			String response = sc.next();
			if (isValidResponse(response, 0, GRID_SIZE - 1)) {
				return Integer.parseInt(response);
			}
			ConsoleView.skipLines(1);
		}
	}

	public Direction pickDirection() {
		while (true) {
			ConsoleView.showDirectionRequest();
			ArrayList<PlayerGrid.Direction> listOfDirection = getAttacker().getPossibleDirections();
			int optionNumber = 1;
			for (Direction direction : listOfDirection) {
				ConsoleView.showMoveOption(optionNumber, direction);
				optionNumber++;
			}

			String response = sc.next();
			if (isValidResponse(response, 1, listOfDirection.size())) {
				int index = Integer.parseInt(response) - 1;
				return Direction.class.getEnumConstants()[index];
			}
			ConsoleView.skipLines(1);
		}
	}

	public void generateNewGame() {
		playerGrid = new PlayerGrid(GRID_SIZE);
		playerGrid.consoleMode();
		cpuGrid = new ComputerGrid(GRID_SIZE);
		cpuGrid.consoleMode();
		placeShips();
	}

	public void placeShips() {
		for (Ship ship : SHIPS_TO_PLACE) {

			Ship ship1 = ship.clone();
			Ship ship2 = ship.clone();

			ship1.setCoordinates(playerGrid.getRandomEmptySpace(ship.getShipSize()));
			playerGrid.placeShip(ship1);

			ship2.setCoordinates(cpuGrid.getRandomEmptySpace(ship.getShipSize()));
			cpuGrid.placeShip(ship2);
		}

	}

	public void attackShip() {
		if (isPlayerTurn()) {
			ConsoleView.showAttackMessage();
			ConsoleView.skipLines(1);
			getAttacker().setShipSelected(pickShipToAttack());
			ConsoleView.skipLines(1);
			displayGame();
			ConsoleView.skipLines(1);
			getTarget().setTargetedCell(pickTargetedCoordinates());
			ConsoleView.skipLines(1);
			fire();
			displayGame();
			getAttacker().diselectShip();
			getAttacker().diselectTargetedCell();
			getTarget().diselectTargetedCell();
		} else {
			ConsoleView.showCpuAttackMessage();
			ConsoleView.skipLines(1);
			sleep(2000);
			ConsoleView.showCpuSelectedShip(getAttacker().getSelectedShip());
			ConsoleView.skipLines(1);
			sleep(2000);
			getTarget().setTargetedCell(cpuGrid.pickTargetedCoordinates());
			ConsoleView.showCpuTargetedCell(cpuGrid.getTargetedCell());
			ConsoleView.skipLines(1);
			sleep(2000);
			fire();
			displayGame();
			getAttacker().diselectShip();
			getAttacker().diselectTargetedCell();
			getTarget().diselectTargetedCell();
		}

	}

	public void moveShip() {
		while (true) {
			ConsoleView.showMoveMessage();
			ConsoleView.skipLines(1);
			getAttacker().setShipSelected(pickShipToMove());
			ConsoleView.skipLines(1);
			displayGame();
			if (getAttacker().moveShip(getAttacker().getSelectedShip(), pickDirection())) {
				ConsoleView.showMoveSuccess(getAttacker().getSelectedShip());
				break;
			} else {
				ConsoleView.showWrongDirection(getAttacker().getSelectedShip());
			}
		}
		displayGame();
		getAttacker().diselectShip();
	}

	public void displayGame() {
		ConsoleView.showGrid(playerGrid, cpuGrid, GRID_SIZE);
	}

	public PlayerGrid getAttacker() {
		if (isPlayerTurn) {
			return playerGrid;
		}
		return cpuGrid;
	}

	public PlayerGrid getTarget() {
		if (isPlayerTurn) {
			return cpuGrid;
		}
		return playerGrid;
	}

	public void fire() {

		PlayerGrid attacker = getAttacker();

		PlayerGrid target = getTarget();

		if (attacker.getSelectedShip() != null && target.getTargetedCell() != null) {
			if (target.willGetHit(target.getTargetedCell(), attacker.getSelectedShip())) {
				ConsoleView.showHitMessage();
				ConsoleView.skipLines(1);
				sleep(1000);
				ArrayList<CoordinatesInMatrix> hitCoordinates = target.getHitCoordinates(attacker.getSelectedShip());
				for (CoordinatesInMatrix coords : hitCoordinates) {
					if (isPlayerTurn) {
						ConsoleView.showHitMessage(coords);
					} else {
						ConsoleView.showCpuHitMessage(coords);
					}
				}
				target.diselectTargetedCell();
			} else {
				ConsoleView.showMissMessage();
			}
		}
		ConsoleView.skipLines(1);
	}

	public boolean isOver() {
		if (playerGrid.hasLost()) {
			System.out.println("Tu as perdu... :(");
			return true;
		} else if (cpuGrid.hasLost()) {
			System.out.println("BRAVOOO ! Vous avez gagné la partie !");
			return true;
		}
		return false;
	}

	/**
	 * Checks if user input is valid
	 * @param response from user
	 * @param min
	 * @param max
	 * @return
	 */
	public static boolean isValidResponse(String response, int min, int max) {
		if (response == null || response.length() == 0) {
			return false;
		}
		try {
			int number = Integer.parseInt(response);
			if (number < min || number > max) {
				System.out.println(
						"Le nombre choisit n'est pas une option valide. Veuillez saisir une option parmi la liste ci-dessous.");
				ConsoleView.skipLines(1);
				return false;
			}
		} catch (NumberFormatException nfe) {
			System.out.println("Vous n'avez pas saisi un nombre. Veuillez réessayer...");
			ConsoleView.skipLines(1);
			return false;
		}
		return true;
	}

	public int getRandomInt(int max) {
		Random rand = new Random();
		return rand.nextInt(max);
	}

	public void sleep(long miliseconds) {
		try {
			Thread.sleep(miliseconds);
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

}
