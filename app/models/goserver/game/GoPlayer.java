/**
 * 
 */
package models.goserver.game;

/**
 * Go player interface.
 * 
 * @author Kacper
 *
 */
public interface GoPlayer {

	/**
	 * @param game
	 *            Game the player is playing.
	 */
	void setGame(GoGame game);

	/**
	 * Method called when the game begins.
	 */
	void notifyAboutGameBegin();

	/**
	 * Method called whenever it's the player's turn.
	 * 
	 * @param opponentsMove
	 *            Opponents last move. If the player is moving first, the
	 *            parameter GoMoveType.FIRST is sent.
	 */
	void notifyAboutTurn(GoMoveType opponentsMove);

	/**
	 * Method called whenever the board changes.
	 */
	void updateBoard();

	/**
	 * Method called whenever the phase of the game changes.
	 * @param gamePhase New phase.
	 */
	void notifyAboutGamePhaseChange(int gamePhase);

	/**
	 * Method called whenever the game ends.
	 * @param blackScore Black's score.
	 * @param whiteScore White's score.
	 */
	void notifyAboutGameEnd(double blackScore, double whiteScore);

	/**
	 * Method called whenever a rematch request is accepted by the opponent.
	 */
	void rematchAccepted();

	/**
	 * Method called whenever a rematch request is denied by the opponent.
	 */
	void rematchDenied();

}
