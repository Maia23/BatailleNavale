package Model;

import java.io.Serializable;
import java.util.Arrays;

import Model.Grid.GameCases;
import Model.Grid.PlayerGrid.Direction;
import View.GraphicMode.View.Log;

/**
 * Skeleton of every ship in the game
 * 
 * @author Maia
 *
 */
public abstract class Ship implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CoordinatesInMatrix[] shipCoordinates = null;
	private Integer[] hitCoordinates = null;

	private boolean isHit = false;
	private boolean isDestroyed = false;

	private boolean verticalPosition;
	private boolean isUnderwater = false;
	private int shipSize = -1;
	private int blastRadius = -1;

	public Ship(int shipSize, int blastRadius, boolean isUnderwater) {
		shipCoordinates = new CoordinatesInMatrix[shipSize];
		hitCoordinates = new Integer[shipSize];
		for (int i = 0; i < shipSize; i++) {
			hitCoordinates[i] = 0;
		}
		this.isUnderwater = isUnderwater;
		this.shipSize = shipSize;
		this.blastRadius = blastRadius;
	}

	public Ship(int shipSize, int blastRadius) {
		this(shipSize, blastRadius, false);
	}

	public CoordinatesInMatrix[] getCoordinates() {
		return shipCoordinates;
	}

	public void setCoordinates(CoordinatesInMatrix[] shipCoordinates) {
		Arrays.sort(shipCoordinates);
		this.shipCoordinates = shipCoordinates;
		if (shipCoordinates.length > 1) {
			verticalPosition = shipCoordinates[0].getColumn() == shipCoordinates[1].getColumn();
		}
	}

	/**
	 * Checks if a ship can move in a given direction, ships in horizontal position
	 * cannot move UP and DOWN
	 * 
	 * @param direction
	 * @returntrue if ship can move in the given direction, false otherwise
	 */
	public boolean canMoveInDirection(Direction direction) {
		if (shipCoordinates.length == 1) {
			return true;
		}

		switch (direction) {
		case UP:
		case DOWN:
			return verticalPosition;
		case LEFT:
		case RIGHT:
			return !verticalPosition;
		}

		throw new RuntimeException("Direction not recognized");
	}

	/**
	 * Updates ship coordinates after mouvement
	 * 
	 * @param direction of mouvement
	 * @return array of updated coordinates
	 */
	public CoordinatesInMatrix[] getShipCoordinatesAfterMouvement(Direction direction, boolean consoleMode) {
		if (!canMoveInDirection(direction)) {
			if (!consoleMode) {
				Log.write("Ship cannot go in that direction!", 2);
			}
			return null;
		}

		CoordinatesInMatrix[] res = shipCoordinates.clone();

		switch (direction) {
		case UP:
			res[res.length - 1] = new CoordinatesInMatrix(res[0].getLine() - 1, res[0].getColumn());
			break;
		case DOWN:
			res[0] = new CoordinatesInMatrix(res[res.length - 1].getLine() + 1, res[res.length - 1].getColumn());
			break;
		case LEFT:
			res[res.length - 1] = new CoordinatesInMatrix(res[0].getLine(), res[0].getColumn() - 1);
			break;
		case RIGHT:
			res[0] = new CoordinatesInMatrix(res[res.length - 1].getLine(), res[res.length - 1].getColumn() + 1);
			break;
		}

		Arrays.sort(res);
		return res;
	}

	public int getBlastRadius() {
		return blastRadius;
	}

	public boolean isHit() {
		return isHit;
	}

	public void setHit(CoordinatesInMatrix coordsOfImpact) {
		for (int i = 0; i < shipSize; i++) {
			if (shipCoordinates[i].equals(coordsOfImpact)) {
				hitCoordinates[i] = 1;
				isHit = true;
				checkIfShipIsDead();
				return;
			}
		}
	}

	public void checkIfShipIsDead() {
		for (Integer integer : hitCoordinates) {
			if (integer == 0) {
				return;
			}
		}

		isDestroyed = true;
	}

	public boolean isDestroyed() {
		return isDestroyed;
	}

	public void setDead(boolean isDestroyed) {
		this.isDestroyed = isDestroyed;
	}

	public GameCases getGameCase() {
		return null;
	}

	public int getShipSize() {
		return shipSize;
	}

	public boolean isUnderwater() {
		return isUnderwater;
	}

	public boolean hasFlare() {
		return false;
	}

	public boolean usedFlare() {
		return false;
	}

	public Ship clone() {
		try {
			return (Ship) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("Clone not supported for Ship class");
		}
	}

}
