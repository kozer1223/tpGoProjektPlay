package models.game.msg;

public class Board {
	
	final int[][] board;

	public Board(int[][] board) {
		this.board = board;
	}

	public int[][] getBoard() {
		return board;
	}

}
