package fr.eeid.codingame;

import java.util.Scanner;

import fr.eeid.codingame.io.InputTracer;
import fr.eeid.codingame.model.Board;

/** Main game class. */
class Player {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		InputTracer input = new InputTracer(in);
		// Parse initial conditions
		Board board = new Board(input);
		while (true) {
			// Parse current state of the game
			board.update(input);
			
			System.out.println(board.getAction());
		}
	}

}