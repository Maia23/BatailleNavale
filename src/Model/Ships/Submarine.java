package Model.Ships;

import java.util.ArrayList;

import Model.CoordinatesInMatrix;
import Model.Ship;
import Model.Grid.GameCases;
import Model.Grid.PlayerGrid.Direction;

public class Submarine extends Ship {
	

	public Submarine() {
		super(1, 1, true);
	}
	
	@Override
	public String toString() {
		return "Submarine";
	}

	@Override
	public GameCases getGameCase() {
		return GameCases.SUBMARINE_CASE;
	}

}
