package View.ConsoleMode;

import Model.CoordinatesInMatrix;
import Model.Ship;
import Model.Grid.PlayerGrid;
import Model.Grid.PlayerGrid.Direction;

public class ConsoleView {

	private static String title = "#####   Bataille Navale Mobile    ######";
	private static String askMode = "Choisissez votre preference: \n" + "1 - Mode Console \n" + "2 - Mode Graphique \n"
			+ "\nVotre choix:";
	private static String startMessage = "Vous allez maintenant démarrer une nouvelle partie";
	private static String isPlayerTurnMessage = "C'est à ton tour de jouer.";
	private static String isCpuTurnMessage = "C'est au tour de l'IA de jouer.";
	private static String launchMessage = "Le missile a été tiré !";
	private static String targetRequest = "Veuillez renseigner les coordonnées de l'emplacement que vous souhaitez attaquer";
	private static String coordinateRequest = "Saisissez la valeur de ";
	private static String hitMessage = "Bien joué !";
	private static String missMessage = "Tu n'as pas touché de bateau.";
	private static String cpuMissMessage = "Tu n'as pas touché de bateau.";
	private static String moveMessage = "Tu as décidé de déplacer un bateau.\n Choisi un bateau à déplacer.";
	private static String directionRequest = "Veuillez choisir une direction dans laquelle déplacer votre bateau";
	private static String cpuAttack = "L'IA a décidé d'attaquer ta grille.";

	public static void showTitle() {
		System.out.println(title);
	}

	public static void showModeMenu() {
		System.out.println(askMode);
	}

	public static void showStartMessage() {
		System.out.println(startMessage);
	}

	public static void showMenu() {
		System.out.println("******Menu******");
		System.out.println("1 - Démarrer une nouvelle partie");
		System.out.println("2 - Charger une partie");
		System.out.println("3 - Règles");
		System.out.println("4 - Quitter la partie");
	}

	public static void showIsPlayerTurnMessage() {
		System.out.println(isPlayerTurnMessage);
	}

	public static void showIsCpuTurnMessage() {
		System.out.println(isCpuTurnMessage);
	}

	public static void showActionMenu() {
		System.out.println("Veuillez choisir une action.");
		System.out.println("1 - Attaquer");
		System.out.println("2 - Déplacer un bateau");
	}

	public static void showAttackMessage() {
		System.out.println("Vous avez décidé d'attaquer.");
		System.out.println("Choisissez le bateau avec lequel vous souhaitez attaquer.");
	}

	public static void showShipOption(Ship ship, int optionNumber) {
		System.out.println(
				optionNumber + " - " + ship.toString() + " à la position " + ship.getCoordinates()[0].toString());
	}

	public static void showShipSelected(Ship ship) {
		System.out.println(
				"Vous avez choisi le " + ship.toString() + " à la position " + ship.getCoordinates()[0].toString());
	}

	public static void showTargetRequest() {
		System.out.println(targetRequest);
	}

	public static void showCoordsSelected(CoordinatesInMatrix coords) {
		System.err.println("Vous avez choisi d'attaquer les coordonnées " + coords.toString() + ".");
	}

	public static void showLauchMessage() {
		System.out.println(launchMessage);
	}

	public static void showHitMessage() {
		System.out.println(hitMessage);
	}

	public static void showHitMessage(CoordinatesInMatrix coords) {
		System.err.println("Vous avez touché un bateau à la position " + coords.toString());
	}

	public static void showCpuHitMessage(CoordinatesInMatrix coords) {
		System.err.println("L'IA a touché un bateau à la position " + coords.toString());
	}

	public static void showMissMessage() {
		System.out.println(missMessage);
	}

	public static void showCpuMissMessage() {
		System.out.println(cpuMissMessage);
	}

	public static void showCoordinateRequest(String coordinate) {
		System.out.println(coordinateRequest + coordinate);
	}

	public static void showMoveMessage() {
		System.out.println(moveMessage);
	}

	public static void showMoveOption(int optionNumber, Direction direction) {
		System.out.println(optionNumber + " - " + direction.toString());
	}

	public static void showDirectionRequest() {
		System.out.println(directionRequest);
	}

	public static void showMoveSuccess(Ship ship) {
		System.out.println("Vous avez déplacé " + ship.toString() + " à la position "
				+ ship.getCoordinates()[0].toString() + " avec succès.");
	}

	public static void showWrongDirection(Ship ship) {
		System.out.println(
				"Le" + ship.toString() + " ne peut pas être déplacé dans cette direction. Veuillez réessayer.");
	}

	public static void showPlayerTurn() {
		System.out.println("C'est à ton tour de jouer.");
	}

	public static void showCpuTurn() {
		System.out.println("C'est au tour de l'IA de jouer");
	}

	public static void showCpuAttackMessage() {
		System.out.println(cpuAttack);
	}

	public static void showCpuSelectedShip(Ship ship) {
		System.out.println("L'IA a choisi le " + ship.toString());
	}

	public static void showCpuTargetedCell(CoordinatesInMatrix coords) {
		System.out.println("L'IA va attaquer les coordonnées " + coords.toString());
	}

	public static void showGrid(PlayerGrid playerGrid, PlayerGrid cpuGrid, int gridSize) {
		StringBuilder res = new StringBuilder();

		int numberOfRows = gridSize;

		int numberOfColumns = gridSize;

		String separate = "      ";
		String player = "PLAYER GRID";
		int playerNumberOfSpace = (numberOfColumns * 4 - player.length()) / 2;
		String playerSpace = repeat((playerNumberOfSpace % 2) == 0 ? playerNumberOfSpace + 2 : playerNumberOfSpace);
		String cpu = "CPU GRID";
		int cpuNumberOfSpace = (numberOfColumns * 4 - cpu.length()) / 2;
		String cpuSpace = repeat((cpuNumberOfSpace % 2 == 0) ? cpuNumberOfSpace + 2 : cpuNumberOfSpace);

		// Premiere ligne pour l'appartenance des grilles;
		res.append(playerSpace + player + playerSpace + separate + cpuSpace + cpu + cpuSpace + "\n");

		for (int gridNumber = 0; gridNumber < 2; gridNumber++) {
			res.append("  | ");
			for (int i = 0; i < numberOfColumns; i++) {
				if (i < 10) {
					res.append(i + " | ");
				} else {
					res.append(i + "| ");
				}

			}
			res.append(separate);
		}
		res.append("\n");
		for (int row = 0; row < numberOfRows; row++) {
			for (int gridNumber = 0; gridNumber < 2; gridNumber++) {
				if (row < 10) {
					res.append(" " + row + "| ");
				} else {
					res.append(row + "| ");
				}
				for (int col = 0; col < numberOfColumns; col++) {
					PlayerGrid gameGrid = gridNumber == 0 ? playerGrid : cpuGrid;
					res.append(gameGrid.getGrid()[row][col].toString() + " | ");
				}
				res.append(separate);
			}
			res.append("\n");
		}

		System.out.println(res.toString());
	}

	public static void skipLines(int number) {
		for (int i = 0; i < number; i++) {
			System.out.println();
		}
	}

	public static String repeat(int count, String with) {
		return new String(new char[count]).replace("\0", with);
	}

	public static String repeat(int count) {
		return repeat(count, " ");
	}

}
