package Model.Grid;

import java.util.ArrayList;

import Model.CoordinatesInMatrix;
import Model.Ship;
import View.GraphicMode.View.Log;

public class ComputerGrid extends PlayerGrid {

	private ArrayList<CoordinatesInMatrix> shots = new ArrayList<CoordinatesInMatrix>();
	private ArrayList<CoordinatesInMatrix> hitsOnOpponent = new ArrayList<CoordinatesInMatrix>();
	private ArrayList<CoordinatesInMatrix> possibleNextHits = new ArrayList<CoordinatesInMatrix>();

	public ComputerGrid(int gridSize) {
		super(gridSize);
	}

	/*
	 * public void fillPossibleNextHits() { for (CoordinatesInMatrix hit :
	 * hitsOnOpponent) { for (CoordinatesInMatrix surroundings :
	 * hit.getSurroundingCoordinates(this.gridSize)) { if (!haveCoords(this.shots,
	 * surroundings)) { if (!haveCoords(possibleNextHits, surroundings)) {
	 * possibleNextHits.add(surroundings); } } } } }
	 */

	public ArrayList<CoordinatesInMatrix> possibleHitsInShots() {
		ArrayList<CoordinatesInMatrix> tempArray = new ArrayList<CoordinatesInMatrix>();
		for (CoordinatesInMatrix possibleNextHit : tempArray) {
			for (CoordinatesInMatrix shot : this.shots) {
				if (possibleNextHit.equals(shot)) {
					tempArray.add(possibleNextHit);
				}
			}
		}
		return tempArray;
	}

	public void filterPossibleNextHits() {
		ArrayList<CoordinatesInMatrix> listOfCoords = possibleHitsInShots();
		for (CoordinatesInMatrix coords : listOfCoords) {
			this.possibleNextHits.remove(coords);
		}
	}

	public boolean shouldAttack() {
		// fillPossibleNextHits();
		filterPossibleNextHits();
		if (this.possibleNextHits.isEmpty()) {
			return false;
		}
		CoordinatesInMatrix target = this.possibleNextHits.get(getRandomInt(0, this.possibleNextHits.size() - 1));
		setTargetedCell(target);
		if (!consoleMode) {
			Log.write("L'IA a décidé de viser la case " + target.toString(), 2);
		}
		return true;
	}

	public void randomAttack() {
		while (true) {
			int x = getRandomInt(0, 14);
			int y = getRandomInt(0, 14);
			CoordinatesInMatrix coords = new CoordinatesInMatrix(x, y);
			if (!haveCoords(this.shots, coords)) {
				setTargetedCell(coords);
				break;
			}
		}
	}

	public void attack() {
		if (!shouldAttack()) {
			randomAttack();
			if (!consoleMode) {
				Log.write("L'IA a décidé de viser une case aléatoirement.", 2);
			}
		}
		pickShip();
		for (CoordinatesInMatrix coords : getPossibleHits(getTargetedCell(), getSelectedShip())) {
			if (!haveCoords(shots, coords)) {
				shots.add(coords);
			}
		}
	}

	public boolean haveCoords(ArrayList<CoordinatesInMatrix> listOfCoords, CoordinatesInMatrix coords) {
		for (CoordinatesInMatrix item : listOfCoords) {
			if (item.equals(coords)) {
				return true;
			}
		}
		return false;
	}

	public int pickAction() {
		attack();
		return 1;
	}

	public void pickShip() {
		for (Ship ship : this.getShips()) {
			if (!ship.isDestroyed()) {
				setShipSelected(ship);
				if (!consoleMode) {
					Log.write("L'IA a choisi le " + ship.toString(), 2);
				}
				break;
			}
		}
	}

	public CoordinatesInMatrix pickTargetedCoordinates() {
		return this.getTargetedCell();
	}

}
