package Model.Ships;

import Model.Ship;
import Model.Grid.GameCases;

public class Destroyer extends Ship {
	
	private boolean usedFlare = false;


	public Destroyer() {
		super(3, 1);
	}
	
	@Override
	public String toString() {
		return "Destroyer";
	}

	@Override
	public GameCases getGameCase() {
		return GameCases.DESTROYER_CASE;
	}
	
	@Override
	public boolean hasFlare() {
		return true;
	}
	
	@Override
	public boolean usedFlare() {
		return usedFlare;
	}
	
	public void useFlare() {
		usedFlare = true;
	}
	
}
