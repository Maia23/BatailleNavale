package View.GraphicMode;

import Controller.GameController;
import Model.CoordinatesInMatrix;
import Model.Grid.GameCases;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Graphical case in Grid
 * @author Maia
 *
 */
public class Case extends Rectangle {
	
	private GameCases caseType = null;
	private CoordinatesInMatrix caseCoords = null;
	
	
	public Case(CoordinatesInMatrix coords, GameCases caseType) {
		super(25, 25);
		this.caseType = caseType;
		this.caseCoords = coords;
		setStroke(Color.BLACK);
		setFill(Color.AQUAMARINE);
	}
	
	@Override
	public String toString() {
		return "Case at coordinates (" + caseCoords.getLine()  + ", " + caseCoords.getColumn() + ") with type = " + caseType.toString();
	}
	
	public boolean isHitInPlayerArea() {
		return !GameController.getGameControllerInstance().getPlayerGrid().isEmpty(caseCoords);
	}
	
	public boolean isHitInCpuArea() {
		return !GameController.getGameControllerInstance().getCpuGrid().isEmpty(caseCoords);
	}
	
	public boolean hit() {
		return false;
	}

	public GameCases getCaseType() {
		return caseType;
	}

	public void setCaseType(GameCases caseType) {
		this.caseType = caseType;
	}

	public CoordinatesInMatrix getCaseCoords() {
		return caseCoords;
	}

	public void setCaseCoords(CoordinatesInMatrix caseCoords) {
		this.caseCoords = caseCoords;
	}
	
	
}
