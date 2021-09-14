package Model.Ships;

import java.util.ArrayList;


import Model.CoordinatesInMatrix;
import Model.Ship;
import Model.Grid.GameCases;

public class Cruiser extends Ship {

	public Cruiser() {
		super(5, 4);
	}
	
	@Override
	public String toString() {
		return "Cruiser";
	}

	@Override
	public GameCases getGameCase() {
		return GameCases.CRUISER_CASE;
	}

}
