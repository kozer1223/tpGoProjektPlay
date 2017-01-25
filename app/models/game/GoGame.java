package models.game;

import java.util.List;
import java.util.Map;

import models.game.rules.GoRuleset;

/**
 * Go game logic interface.
 * 
 * @author Kacper
 *
 */
public interface GoGame {

	/**
	 * @return Black player.
	 */
	public GoPlayer getPlayer1();

	/**
	 * @param player
	 *            Black player.
	 */
	public void setPlayer1(GoPlayer player);

	/**
	 * @return White player.
	 */
	public GoPlayer getPlayer2();

	/**
	 * @param player
	 *            White player.
	 */
	public void setPlayer2(GoPlayer player);

	/**
	 * @return Ruleset used by the game.
	 */
	public GoRuleset getRuleset();

	/**
	 * @param ruleset
	 *            Ruleset to be used by the game.
	 */
	public void setRuleset(GoRuleset ruleset);

	/**
	 * @return GoBoard object representing the board
	 */
	public GoBoard getBoard();

	/**
	 * Returns a GoPlayer object representing the player opposing the given one.
	 * 
	 * @param player
	 *            The player.
	 * @return Given player's opposing player.
	 */
	public GoPlayer getOpposingPlayer(GoPlayer player);

	/**
	 * Attempts to have the given player place a stone on (x,y) if it is their
	 * turn.
	 * 
	 * @param player
	 *            Playing player.
	 * @param x
	 *            X position on the board.
	 * @param y
	 *            Y position on the board.
	 * @throws InvalidMoveException
	 */
	public void makeMove(GoPlayer player, int x, int y) throws InvalidMoveException;

	/**
	 * If it is the given player's turn, passes the turn.
	 * 
	 * @param player
	 *            Playing player.
	 */
	public void passTurn(GoPlayer player);

	/**
	 * @param player
	 *            Player.
	 * @return Number of stones captured by the player.
	 */
	public int getPlayersCapturedStones(GoPlayer player);

	/**
	 * @param player
	 *            Player.
	 * @return true if it's the player's turn.
	 */
	public boolean isPlayersTurn(GoPlayer player);

	/**
	 * Returns the current game phase.
	 * 
	 * @return 0 - stone placing phase, 1 - group marking phase, 2 - game end
	 */
	public int getGamePhase();

	/**
	 * @return true if phase = 0.
	 */
	public boolean isStonePlacingPhase();

	/**
	 * @return true if phase = 1.
	 */
	public boolean isGroupMarkingPhase();

	/**
	 * @return true if game has ended.
	 */
	public boolean isGameEnd();

	/**
	 * If the game has ended, returns the given player's score.
	 * 
	 * @param player
	 *            Player.
	 * @return Player's score.
	 */
	public double getPlayersScore(GoPlayer player);

	/**
	 * Set given player's score.
	 * 
	 * @param player
	 *            Player.
	 * @param score
	 *            Player's score.
	 */
	public void setPlayersScore(GoPlayer player, double score);

	/**
	 * Returns a map of all groups and their group types (dead, alive).
	 * 
	 * @return A map of groups and their types.
	 */
	public Map<Integer, GoGroupType> getLabelsMap();

	/**
	 * If it is the player's turn, attempts to apply changes to group types.
	 * 
	 * @param player
	 *            Playing player.
	 * @param groupTypeChanges
	 *            Map representing the group changes.
	 */
	public void applyGroupTypeChanges(GoPlayer player, Map<Integer, GoGroupType> groupTypeChanges);

	/**
	 * @return List of all locked groups.
	 */
	public List<Integer> getAllLockedGroups();

	/**
	 * Makes the player leave the game, awarding victory to the second player.
	 * 
	 * @param player
	 *            Player
	 */
	public void leaveGame(GoPlayer player);

	/**
	 * Makes the player request a rematch at the end of the game.
	 * 
	 * @param player
	 *            Player.
	 */
	public void requestRematch(GoPlayer player);

	/**
	 * Makes the player deny a rematch.
	 * 
	 * @param player
	 *            Player
	 */
	public void denyRematch(GoPlayer player);

}
