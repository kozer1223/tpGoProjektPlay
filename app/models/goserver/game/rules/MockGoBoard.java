package models.goserver.game.rules;

import models.goserver.game.DefaultGoBoard;
import models.goserver.game.GoBoard;

/**
 * DefaultGoBoard with the capability to completely change the board.
 * Used in Ko Rule implementation.
 * 
 * @author Kacper
 *
 */
public class MockGoBoard extends DefaultGoBoard implements GoBoard {

	public MockGoBoard(int size) {
		super(size);
	}

	/**
	 * Change the board data to the given one.
	 * 
	 * @param board
	 *            New board data.
	 */
	public void setBoard(int[][] board) {
		this.board = board;
	}

}
