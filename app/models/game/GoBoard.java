package models.game;

import java.util.List;
import java.util.Map;

import models.util.IntPair;

/**
 * Go board interface.
 * 
 * @author Kacper
 *
 */
public interface GoBoard {

	/**
	 * Places a stone on (x,y) and attempts to capture the opponent's stones.
	 * Returns the number of captured stones by both players (in case of
	 * suicidal move).
	 * 
	 * @param color
	 *            Player's color.
	 * @param x
	 *            X position on the board.
	 * @param y
	 *            Y position on the board.
	 * @return IntPair of (stones captured by the playing player, stones
	 *         captured by the opponent in case of a suicidal move)
	 * @throws IllegalArgumentException
	 * @throws InvalidMoveException
	 */
	public IntPair placeStone(int color, int x, int y) throws InvalidMoveException;

	/**
	 * Get a list of all the stones belonging to the same group as the given
	 * stone.
	 * 
	 * @param x
	 *            X position on the board.
	 * @param y
	 *            Y position on the board.
	 * @return List of al the stones belonging to the same group as the given
	 *         stone.
	 */
	public List<IntPair> getConnectedStones(int x, int y);

	/**
	 * Get the size of the board.
	 * 
	 * @return Size of the board.
	 */
	public int getSize();

	/**
	 * Get the data representation of the board.
	 * 
	 * @return Two-dimensional integer array representing the board with each
	 *         value representing an empty, black or white space.
	 */
	public int[][] getBoardData();

	/**
	 * Get the data representation of the board from one move ago.
	 * 
	 * @return Two-dimensional integer array representing the board with each
	 *         value representing an empty, black or white space.
	 */
	public int[][] getPreviousBoardData();

	/**
	 * @return Integer representing the black color.
	 */
	public int getBlackColor();

	/**
	 * @return Integer representing the white color.
	 */
	public int getWhiteColor();

	/**
	 * @return Integer representing an empty space.
	 */
	public int getEmptyColor();

	/**
	 * Function that returns the color of the opposite player.
	 * 
	 * @param color
	 *            Color.
	 * @return Color opposite to given color. Returns EMPTY if given EMPTY.
	 */
	public int getOpposingColor(int color);

	/**
	 * Turn suicide rule checking on or off.
	 * 
	 * @param check
	 *            true if you want to turn on suicide rule, off to turn it off.
	 */
	public void setSuicideCheckEnabled(boolean check);

	/**
	 * Get the representation of the board with each group being labeled with a
	 * unique integer.
	 * 
	 * @return Two-dimensional integer array representing the board with each
	 *         value being equal to the label of the group to which the stone
	 *         belongs to.
	 */
	public int[][] getBoardWithLabeledGroups();

	/**
	 * Get all labels of stone groups.
	 * 
	 * @return Integer array of all group labels.
	 */
	public int[] getAllGroupLabels();

	/**
	 * Get the group type (dead, alive) of a group with given label.
	 * 
	 * @param label
	 *            Label of the group.
	 * @return Group type of the group.
	 */
	public GoGroupType getGroupType(int label);

	/**
	 * Check if the group is locked.
	 * 
	 * @param label
	 *            Label of the group.
	 * @return true if the group is locked, false otherwise.
	 */
	public boolean checkIfGroupIsLocked(int label);

	/**
	 * Apply group changes to the board. Returns true if there were any changes,
	 * false if there were no changes.
	 * 
	 * @param groupTypeChanges
	 *            Map (Integer (label) -> GoGroupType) representing group type
	 *            changes.
	 * @return true if there were any changes, false otherwise.
	 */
	public boolean applyGroupTypeChanges(Map<Integer, GoGroupType> groupTypeChanges);

	/**
	 * Resets all group labels.
	 */
	public void resetGroupLabels();

	/**
	 * Calculates both players' score based on the territories. The territories
	 * are determined by removing dead stone groups from the board.
	 * 
	 * @return IntPair of (Black player's score, White player's score)
	 */
	public IntPair calculateTerritoryScore();

	/**
	 * Remove all dead stone groups.
	 */
	public void removeDeadGroups();

}
