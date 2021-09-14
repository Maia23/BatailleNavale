package Model.Ships;

import java.util.ArrayList;

import Model.CoordinatesInMatrix;
import Model.Ship;
import Model.Grid.GameCases;

public class Battleship extends Ship {


	public Battleship() {
		super(7, 9);
	}
	
	@Override
	public String toString() {
		return "Battleship";
	}

	@Override
	public GameCases getGameCase() {
		return GameCases.BATTLESHIP_CASE;
	}

}
