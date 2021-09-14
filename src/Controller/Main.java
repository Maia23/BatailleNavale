package Controller;

import java.util.Scanner;

import View.ConsoleMode.ConsoleView;
import View.GraphicMode.View;
import javafx.application.Application;

public class Main {

	public static int pickMenu() {
		Scanner sc = new Scanner(System.in);
		while (true) {
			ConsoleView.showModeMenu();
			String response = sc.next();
			ConsoleView.skipLines(1);
			if (ConsoleGame.isValidResponse(response, 1, 2)) {
				return Integer.parseInt(response);
			}
		}
	}

	public static void main(String[] args) {

		ConsoleView.showTitle();
		ConsoleView.skipLines(1);
		int choice = pickMenu();
		switch (choice) {
		case 1:
			ConsoleGame game = new ConsoleGame();
			game.play();
			break;
		case 2:
			Application.launch(View.class, "");
			break;
		}
	}

}
