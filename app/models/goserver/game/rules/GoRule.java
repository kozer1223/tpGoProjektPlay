/**
 * 
 */
package models.goserver.game.rules;

import models.goserver.game.GoBoard;
import models.goserver.game.GoGame;
import models.goserver.game.InvalidMoveException;

/**
 * Go game rule interface.
 * 
 * @author Kacper
 *
 */
public interface GoRule {

	/**
	 * Method called at the beginning of the game.
	 * 
	 * @param game
	 *            Game.
	 */
	public void onGameStart(GoGame game);

	/**
	 * Method called at the end of the game.
	 * 
	 * @param game
	 *            Game.
	 */
	public void onGameEnd(GoGame game);

	/**
	 * Method called before placing a stone on a board. Throws an exception
	 * whenever the move is invalid according to the rule. Returns true if the
	 * move is valid.
	 * 
	 * @param board
	 *            Board object.
	 * @param color
	 *            Player's color.
	 * @param x
	 *            X position on the board.
	 * @param y
	 *            Y position on the board.
	 * @return true if move is valid.
	 * @throws InvalidMoveException
	 */
	public boolean validateMove(GoBoard board, int color, int x, int y) throws InvalidMoveException;

}
