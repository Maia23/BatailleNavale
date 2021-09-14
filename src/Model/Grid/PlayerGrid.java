package Model.Grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import Model.CoordinatesInMatrix;
import Model.Ship;
import View.GraphicMode.View.Log;

/**
 * Implements grid and CPU player, containing ships, hit coordinates, ship
 * selected and targeted cell
 * 
 * @author Maia
 *
 */
public class PlayerGrid implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GameCases[][] playerGrid = null;
	private ArrayList<Ship> shipsInGrid = null;

	private ArrayList<CoordinatesInMatrix> hitCoordinates = null;

	private Ship shipSelected = null;
	private CoordinatesInMatrix targetedCell = null;
	protected boolean consoleMode = false;

	public PlayerGrid(int gridSize) {

		playerGrid = new GameCases[gridSize][gridSize];
		shipsInGrid = new ArrayList<Ship>();
		hitCoordinates = new ArrayList<CoordinatesInMatrix>();

		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				playerGrid[i][j] = GameCases.EMPTY_CASE;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();

		for (int row = 0; row < playerGrid.length; row++) {
			res.append("|");
			for (int col = 0; col < playerGrid[row].length; col++) {
				res.append(playerGrid[row][col].toString() + "|");
			}
			res.append("\n");
		}

		return res.toString();
	}

	public GameCases[][] getGrid() {
		return this.playerGrid;
	}

	public void setGrid(GameCases[][] gameGrid) {
		this.playerGrid = gameGrid;
	}

	public Ship getSelectedShip() {
		return shipSelected;
	}

	public void consoleMode() {
		consoleMode = true;
	}

	/**
	 * Gets value from one case
	 * 
	 * @param coords
	 * @return String from case
	 */
	public GameCases getValue(CoordinatesInMatrix coords) {
		if (!isInsideArea(coords)) {
			return null;
		}
		return playerGrid[coords.getLine()][coords.getColumn()];
	}

	public ArrayList<CoordinatesInMatrix> getHitCells() {
		return hitCoordinates;
	}

	/**
	 * Set value in one case if it is empty
	 * 
	 * @param coords case where value will be inserted
	 * @param value  to insert
	 * @return true if case was empty and value was inserted, false otherwise
	 */
	public void setValue(CoordinatesInMatrix coords, GameCases value) {
		playerGrid[coords.getLine()][coords.getColumn()] = value;
	}

	/**
	 * Tests if a value can be set at given coordinates
	 * 
	 * @param coords
	 * @param value
	 * @return
	 */
	private boolean testSetShip(CoordinatesInMatrix coords, GameCases value) {
		if (!isInsideArea(coords)) {
			throw new IllegalArgumentException("Coordinates [" + coords.toString() + "] are not inside play area !");
		} else if (!isEmpty(coords)) {
			return false;
		}

		return true;
	}

	/**
	 * Checks if a case is empty
	 * 
	 * @param coords from case to test
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty(CoordinatesInMatrix coords) {
		return playerGrid[coords.getLine()][coords.getColumn()].toString().equals(GameCases.EMPTY_CASE.toString());
	}

	/**
	 * Checks if given coordinates are inside the grid's area
	 * 
	 * @param coords to case
	 * @return true if inside grid's area, false otherwise
	 */
	public boolean isInsideArea(CoordinatesInMatrix coords) {
		return (coords.getLine() >= 0 && coords.getLine() < playerGrid.length)
				&& (coords.getColumn() >= 0 && coords.getColumn() < playerGrid.length);
	}

	/**
	 * Allows to place ship on the grid's instance
	 * 
	 * @param ship to be placed
	 * @return true if ship could be placed, false otherwise
	 */
	public boolean placeShip(Ship ship) {
		for (CoordinatesInMatrix coordinates : ship.getCoordinates()) {
			if (!testSetShip(coordinates, ship.getGameCase())) {
				return false;
			}
		}
		for (CoordinatesInMatrix coordinates : ship.getCoordinates()) {
			setValue(coordinates, ship.getGameCase());
		}
		shipsInGrid.add(ship);
		return true;
	}

	/**
	 * Selects a ship from the grid based on the given coordinates
	 * 
	 * @param coordinatesInMatrix
	 */
	public boolean selectShip(CoordinatesInMatrix coordinatesInMatrix) {

		for (Ship ship : shipsInGrid) {
			for (CoordinatesInMatrix shipCoords : ship.getCoordinates()) {
				if (shipCoords.equals(coordinatesInMatrix)) {
					if (shipSelected == null || !(ship.getGameCase() == shipSelected.getGameCase())) {
						if (!consoleMode) {
							Log.write("Ship selected : " + ship.toString() + " (blastRadius = " + ship.getBlastRadius()
									+ ", size = " + ship.getShipSize() + ")", 1);
						}
					}
					if (!ship.isDestroyed()) {
						shipSelected = ship;
						return true;
					} else {
						Log.write("Tried to select a destroyed ship", 1, true);
						return false;
					}
				}
			}
		}
		shipSelected = null;
		return false;
	}

	/**
	 * Selects a cell as targeted based on th given coordinates
	 * 
	 * @param coordinatesInMatrix
	 * @return true if given coordinates are inside grid's area, false otherwise
	 */
	public boolean targetCell(CoordinatesInMatrix coordinatesInMatrix) {
		if (isInsideArea(coordinatesInMatrix)) {
			targetedCell = coordinatesInMatrix;
			if (!consoleMode) {
				Log.write("A cell was targeted by the player " + coordinatesInMatrix.toString(), 1, true);
			}
			return true;
		}
		if (!consoleMode) {
			Log.write("Tried to target a cell on CPU grid but was not inside Area! " + coordinatesInMatrix.toString(),
					1, true);
		}
		targetedCell = null;
		return false;
	}

	public ArrayList<Ship> getShips() {
		return shipsInGrid;
	}

	public CoordinatesInMatrix getTargetedCell() {
		return targetedCell;
	}

	/**
	 * Allows to diselect the targeted cell
	 */
	public void diselectTargetedCell() {
		targetedCell = null;
	}

	/**
	 * Allows to diselect ships
	 */
	public void diselectShip() {
		shipSelected = null;
	}

	/**
	 * Manage attack on grid instance, setting targeted cells as missed or hit
	 * 
	 * @param impactCoords  coordinates to center of impact
	 * @param attackingShip ship responsible for the attack
	 * @return true if any ship were hit, false otherwise
	 */
	public boolean attack(CoordinatesInMatrix impactCoords, Ship attackingShip) {
		if (!isInsideArea(impactCoords)) {
			throw new IllegalArgumentException("Point of impact is not inside area");
		}

		boolean hit = false;

		ArrayList<CoordinatesInMatrix> impactArea = getPossibleHits(impactCoords, attackingShip);
		if (!consoleMode) {
			Log.write("Impact area : " + impactArea.toString(), 3, true);
		}

		for (CoordinatesInMatrix coords : impactArea) {
			GameCases value = getValue(coords);
			if (value == GameCases.BATTLESHIP_CASE || value == GameCases.CRUISER_CASE
					|| value == GameCases.DESTROYER_CASE || value == GameCases.SUBMARINE_CASE) {

				if (hitShip(coords, attackingShip)) {
					setValue(coords, GameCases.WRECKAGE_CASE);
					if (!consoleMode) {
						Log.write("A ship was hit on coordinates : " + coords.toString(), 2);
					}
					hit = true;
				}
			}
		}
		return hit;
	}

	/**
	 * Marks certain ship coordinates as hit
	 * 
	 * @param coords        coordinates to hit
	 * @param attackingShip ship attacking
	 * @return true if ship has been hit, false otherwise
	 */
	private boolean hitShip(CoordinatesInMatrix coords, Ship attackingShip) {
		for (Ship ship : shipsInGrid) {
			for (CoordinatesInMatrix shipCoords : ship.getCoordinates()) {
				if (shipCoords.equals(coords)) {
					if (ship.isUnderwater() && !attackingShip.isUnderwater()) {
						if (!consoleMode) {
							Log.write("Hit on a submarine but attacking ship is not a submarine!!", 2, true);
						}
						return false;
					}
					ship.setHit(coords);
				}
			}
		}

		return true;
	}

	/**
	 * Allows to move ship on grid
	 * 
	 * @param shipToMove
	 * @param direction  of mouvement
	 */
	public boolean moveShip(Ship shipToMove, Direction direction) {

		CoordinatesInMatrix[] tempArray = shipToMove.getShipCoordinatesAfterMouvement(direction, consoleMode);

		if (tempArray == null) {
			return false;
		}

		for (CoordinatesInMatrix coordinates : tempArray) {
			if (!isInsideArea(coordinates)) {
				if (!consoleMode) {
					Log.write("You're at the edge, choose another direction!", 3);
				}
				return false;
			} else if (!Arrays.asList(shipToMove.getCoordinates()).contains(coordinates)
					&& getValue(coordinates) != GameCases.EMPTY_CASE) {
				if (!consoleMode) {
					Log.write("There is another ship there, do you want to kill your sailors? :o", 3);
				}
				return false;
			}
		}

		removeShipFromGrid(shipToMove);
		shipToMove.setCoordinates(tempArray);
		placeShipInGrid(shipToMove);
		if (!consoleMode) {
			Log.write("Ship moved : \n\t-> New ShipCoords are = " + Arrays.toString(tempArray), 1, true);
		}
		return true;

	}

	private void removeShipFromGrid(Ship ship) {
		for (CoordinatesInMatrix coordinatesInMatrix : ship.getCoordinates()) {
			setValue(coordinatesInMatrix, GameCases.EMPTY_CASE);
		}
	}

	/**
	 * Places ship on grid
	 * 
	 * @param ship
	 */
	private void placeShipInGrid(Ship ship) {
		for (CoordinatesInMatrix coordinatesInMatrix : ship.getCoordinates()) {
			setValue(coordinatesInMatrix, ship.getGameCase());
		}
	}

	/**
	 * Allows to retrieve list of targeted coordinates according to center of impact
	 * and attackingship's blast radius
	 * 
	 * @param impactCoords
	 * @param attackingShip
	 * @return list of targeted coordinates
	 */
	public ArrayList<CoordinatesInMatrix> getPossibleHits(CoordinatesInMatrix impactCoords, Ship attackingShip) {

		ArrayList<CoordinatesInMatrix> res = new ArrayList<CoordinatesInMatrix>();
		int line = impactCoords.getLine();
		int column = impactCoords.getColumn();

		switch (attackingShip.getBlastRadius()) {
		case 9:
			res.add(new CoordinatesInMatrix(line - 1, column - 1));
			res.add(new CoordinatesInMatrix(line - 1, column + 1));
			res.add(new CoordinatesInMatrix(line + 1, column - 1));
			res.add(new CoordinatesInMatrix(line + 1, column + 1));
		case 4:
			res.add(new CoordinatesInMatrix(line - 1, column));
			res.add(new CoordinatesInMatrix(line, column - 1));
			res.add(new CoordinatesInMatrix(line, column + 1));
			res.add(new CoordinatesInMatrix(line + 1, column));
		case 1:
			res.add(impactCoords);
			break;
		}

		return res;
	}

	/**
	 * Search for a random empty space in grid's matrix
	 * 
	 * @param length of space
	 */
	public CoordinatesInMatrix[] getRandomEmptySpace(int length) {

		CoordinatesInMatrix[] res = new CoordinatesInMatrix[length];
		CoordinatesInMatrix origin = null;

		do {

			origin = new CoordinatesInMatrix(getRandomInt(0, playerGrid.length - 1),
					getRandomInt(0, playerGrid.length - 1));
			if (!isInsideArea(origin) || !isEmpty(origin)) {
				continue; // We keep looping
			} else {
				res[0] = origin; // We got a first coordinate
				if (length == 1) {
					return res;
				}
			}

			int upCounter = 1;
			boolean up = true;
			int downCounter = 1;
			boolean down = true;
			int rightCounter = 1;
			boolean right = true;
			int leftCounter = 1;
			boolean left = true;

			CoordinatesInMatrix upCoordinates;
			CoordinatesInMatrix downCoordinates;
			CoordinatesInMatrix rightCoordinates;
			CoordinatesInMatrix leftCoordinates;

			for (int i = 1; i <= length; i++) {
				switch (getRandomInt(1, 4)) {
				case 1:
					// Up
					upCoordinates = new CoordinatesInMatrix(origin.getLine() - i, origin.getColumn());
					if (!isInsideArea(upCoordinates) || length > origin.getLine() || !isEmpty(upCoordinates)) {
						up = false; // If not empty or not enough space no need to keep looping through cases
					}

					if (up) {
						if (upCounter++ == length) { // If enough cases are empty
							for (int j = 0; j < length; j++) {
								res[j] = new CoordinatesInMatrix(origin.getLine() - j, origin.getColumn());
							}
							return res;
						}
					}
					break;

				case 2:
					// Down
					downCoordinates = new CoordinatesInMatrix(origin.getLine() + i, origin.getColumn());
					if (!isInsideArea(downCoordinates) || length >= playerGrid.length - origin.getLine()
							|| !isEmpty(downCoordinates)) {
						down = false; // If not empty or not enough space no need to keep looping through cases
					}

					if (down) {
						if (downCounter++ == length) { // If enough cases are empty
							for (int j = 0; j < length; j++) {
								res[j] = new CoordinatesInMatrix(origin.getLine() + j, origin.getColumn());
							}
							return res;
						}
					}
					break;

				case 3:
					// Left
					leftCoordinates = new CoordinatesInMatrix(origin.getLine(), origin.getColumn() - i);
					if (!isInsideArea(leftCoordinates) || length > origin.getColumn() || !isEmpty(leftCoordinates)) {
						left = false; // If not empty or not enough space no need to keep looping through cases
					}

					if (left) {
						if (leftCounter++ == length) { // If enough cases are empty
							for (int j = 0; j < length; j++) {
								res[j] = new CoordinatesInMatrix(origin.getLine(), origin.getColumn() - j);
							}
							return res;
						}
					}
					break;

				case 4:
					// Right
					rightCoordinates = new CoordinatesInMatrix(origin.getLine(), origin.getColumn() + i);
					if (!isInsideArea(rightCoordinates) || length >= playerGrid.length - origin.getColumn()
							|| !isEmpty(rightCoordinates)) {
						right = false; // If not empty or not enough space no need to keep looping through cases
					}

					if (right) {
						if (rightCounter++ == length) { // If enough cases are empty
							for (int j = 0; j < length; j++) {
								res[j] = new CoordinatesInMatrix(origin.getLine(), origin.getColumn() + j);
							}
							return res;
						}
					}
					break;
				}
			}

		} while (true);
	}

	public ArrayList<Ship> getAvailableShips() {
		ArrayList<Ship> availableShips = new ArrayList<Ship>();
		for (Ship ship : shipsInGrid) {
			if (!ship.isDestroyed()) {
				availableShips.add(ship);
			}
		}
		return availableShips;
	}

	public ArrayList<Ship> getMovableShips() {
		ArrayList<Ship> movableShips = new ArrayList<Ship>();
		for (Ship ship : shipsInGrid) {
			if (!ship.isHit()) {
				movableShips.add(ship);
			}
		}
		return movableShips;
	}

	/**
	 * Generates random integer
	 * 
	 * @param min of range
	 * @param max of range
	 * @return generated int
	 */
	public static int getRandomInt(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public void setTargetedCell(CoordinatesInMatrix coordinates) {
		targetedCell = coordinates;
	}

	public void setShipSelected(Ship ship) {
		shipSelected = ship;
	}

	public static enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	public ArrayList<CoordinatesInMatrix> getHitCoordinates(Ship attackingShip) {
		ArrayList<CoordinatesInMatrix> impactArea = getPossibleHits(targetedCell, attackingShip);
		ArrayList<CoordinatesInMatrix> list = new ArrayList<CoordinatesInMatrix>();
		for (CoordinatesInMatrix coords : impactArea) {
			GameCases value = getValue(coords);
			if (value == GameCases.BATTLESHIP_CASE || value == GameCases.CRUISER_CASE
					|| value == GameCases.DESTROYER_CASE || value == GameCases.SUBMARINE_CASE) {

				if (hitShip(targetedCell, attackingShip)) {
					setValue(coords, GameCases.WRECKAGE_CASE);
					list.add(coords);
					hitCoordinates.add(coords);
				}
			}
		}
		return list;
	}

	public boolean willGetHit(CoordinatesInMatrix impactCoords, Ship attackingShip) {
		ArrayList<CoordinatesInMatrix> impactArea = getPossibleHits(impactCoords, attackingShip);
		for (CoordinatesInMatrix coords : impactArea) {
			GameCases value = getValue(coords);
			if (value == GameCases.BATTLESHIP_CASE || value == GameCases.CRUISER_CASE
					|| value == GameCases.DESTROYER_CASE || value == GameCases.SUBMARINE_CASE) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<PlayerGrid.Direction> getPossibleDirections() {
		ArrayList<PlayerGrid.Direction> possibleDirections = new ArrayList<PlayerGrid.Direction>();
		for (Direction direction : Direction.class.getEnumConstants()) {
			if (this.shipSelected.canMoveInDirection(direction)) {
				possibleDirections.add(direction);
			}
		}
		return possibleDirections;
	}

	public boolean hasLost() {
		return getAvailableShips().isEmpty();
	}

}
