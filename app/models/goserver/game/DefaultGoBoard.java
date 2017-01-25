/**
 * 
 */
package models.goserver.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.goserver.game.rules.SuicideRule;
import models.goserver.util.IntPair;

/**
 * Default implementation of a Go board.
 * 
 * @author Kacper
 *
 */
public class DefaultGoBoard implements GoBoard {

	// stone colors
	public static final int EMPTY = 0;
	public static final int BLACK = 1;
	public static final int WHITE = 2;

	protected static final int BLACK_TERRITORY = 3;
	protected static final int WHITE_TERRITORY = 4;
	protected static final int DISPUTED_TERRITORY = 5;

	private final int size;
	protected int board[][];
	protected int previousBoard[][]; // ko rule
	private int labeledBoard[][]; // labeled stone groups
	private Map<Integer, GoGroupType> groups; // group states (dead/alive)
	private Map<Integer, Integer> groupLockCount; // 0 - default suggestion
													// 1 - changed by previous
													// player
													// 2 - locked

	private boolean suicideCheckEnabled = false; // should Suicide rule be
													// employed

	/**
	 * Create a size x size board.
	 * 
	 * @param size
	 *            Size of the board.
	 */
	public DefaultGoBoard(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException();
		}
		this.size = size;
		board = new int[size][size];
		previousBoard = new int[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				board[i][j] = EMPTY;
				previousBoard[i][j] = EMPTY;
			}
		}

		labeledBoard = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#placeStone(int, int, int)
	 */
	public IntPair placeStone(int color, int x, int y) throws InvalidMoveException {
		if (x < 0 || x >= size || y < 0 || y >= size) {
			throw new InvalidMoveException("Invalid move.");
		}
		if (board[x][y] != EMPTY) {
			throw new InvalidMoveException("Invalid move.");
		}
		if (color != WHITE && color != BLACK) {
			throw new IllegalArgumentException();
		}

		// move validity according to optional rules is enforced by
		// GoGame.makeMove()

		// board -> previousBoard
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				previousBoard[i][j] = board[i][j];
			}
		}

		board[x][y] = color;

		// capture
		int captured = 0;
		if (x + 1 < size && board[x + 1][y] == getOpposingColor(color))
			captured += captureStones(x + 1, y);
		if (y + 1 < size && board[x][y + 1] == getOpposingColor(color))
			captured += captureStones(x, y + 1);
		if (x - 1 >= 0 && board[x - 1][y] == getOpposingColor(color))
			captured += captureStones(x - 1, y);
		if (y - 1 >= 0 && board[x][y - 1] == getOpposingColor(color))
			captured += captureStones(x, y - 1);

		if (suicideCheckEnabled && captured <= 0) {
			// is suicidal?
			if (mockCaptureStones(x, y) != 0) {
				board[x][y] = EMPTY;
				throw new InvalidMoveException(SuicideRule.invalidMoveMessage);
			}
		}
		labeledBoard = null;

		return new IntPair(captured, captureStones(x, y));
	}

	/**
	 * Captures the group, to which the given stone belongs, if it has no
	 * liberties left. Returns the number of captured stones.
	 * 
	 * @param x
	 *            X position on the board.
	 * @param y
	 *            Y position on the board.
	 * @return Number of captured stones.
	 */
	private int captureStones(int x, int y) {
		// get the group
		List<IntPair> stoneGroup = getConnectedStones(x, y);

		int liberties = 0;

		for (int i = 0; i < stoneGroup.size(); i++) {
			liberties += getLiberties(stoneGroup.get(i).x, stoneGroup.get(i).y);
		}
		if (liberties == 0) { // captured
			int captured = stoneGroup.size();
			for (int i = 0; i < stoneGroup.size(); i++) {
				board[stoneGroup.get(i).x][stoneGroup.get(i).y] = EMPTY;
			}
			return captured;
		} else {
			return 0;
		}
	}

	/**
	 * Check if the group, to which the given stone belongs, would be captured.
	 * This function does not remove any stones from the board.
	 * 
	 * @param x
	 *            X position on the board.
	 * @param y
	 *            Y position on the board.
	 * @return Number of stones that would be captured.
	 */
	private int mockCaptureStones(int x, int y) {
		List<IntPair> stoneGroup = getConnectedStones(x, y);

		int liberties = 0;

		for (int i = 0; i < stoneGroup.size(); i++) {
			liberties += getLiberties(stoneGroup.get(i).x, stoneGroup.get(i).y);
		}
		if (liberties == 0) { // uduszone
			return stoneGroup.size();
		} else {
			return 0;
		}
	}

	/**
	 * Returns the number of empty spaces surrounding given stone. Returns -1 if
	 * (x,y) is empty.
	 * 
	 * @param x
	 *            X position on the board.
	 * @param y
	 *            Y position on the board.
	 * @return Number of empty spaces surrounding the stone, -1 if (x,y) is
	 *         empty.
	 * @throws IllegalArgumentException
	 */
	public int getLiberties(int x, int y) throws IllegalArgumentException {
		if (x < 0 || x >= size || y < 0 || y >= size) {
			throw new IllegalArgumentException();
		}
		if (board[x][y] == EMPTY) {
			return -1;
		}
		int liberties = 0;
		if (x + 1 < size && board[x + 1][y] == EMPTY)
			liberties++;
		if (y + 1 < size && board[x][y + 1] == EMPTY)
			liberties++;
		if (x - 1 >= 0 && board[x - 1][y] == EMPTY)
			liberties++;
		if (y - 1 >= 0 && board[x][y - 1] == EMPTY)
			liberties++;
		return liberties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getOpposingColor(int)
	 */
	public int getOpposingColor(int color) {
		switch (color) {
		case BLACK:
			return WHITE;
		case WHITE:
			return BLACK;
		default:
			return EMPTY;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getConnectedStones(int, int)
	 */
	public List<IntPair> getConnectedStones(int x, int y) {
		// floodfill algorithm
		final int width = getSize();
		final int height = getSize();

		Set<IntPair> result = new HashSet<IntPair>();
		List<IntPair> queue = new ArrayList<IntPair>();

		final int color = board[x][y];

		queue.add(new IntPair(x, y));

		int i, cur_x, cur_y;

		while (!queue.isEmpty()) {
			cur_x = queue.get(0).x;
			cur_y = queue.get(0).y;

			// current tile
			result.add(new IntPair(cur_x, cur_y));
			if (cur_y - 1 >= 0 && board[cur_x][cur_y - 1] == color) {
				if (result.add(new IntPair(cur_x, cur_y - 1)))
					queue.add(new IntPair(cur_x, cur_y - 1));
			}
			if (cur_y + 1 < height && board[cur_x][cur_y + 1] == color) {
				if (result.add(new IntPair(cur_x, cur_y + 1)))
					queue.add(new IntPair(cur_x, cur_y + 1));
			}
			// left
			i = 1;
			while (cur_x - i >= 0) {
				if (board[cur_x - i][cur_y] == color) {
					result.add(new IntPair(cur_x - i, cur_y));
					if (cur_y - 1 >= 0 && board[cur_x - i][cur_y - 1] == color) {
						if (result.add(new IntPair(cur_x - i, cur_y - 1)))
							queue.add(new IntPair(cur_x - i, cur_y - 1));
					}
					if (cur_y + 1 < height && board[cur_x - i][cur_y + 1] == color) {
						if (result.add(new IntPair(cur_x - i, cur_y + 1)))
							queue.add(new IntPair(cur_x - i, cur_y + 1));
					}
					i++;
				} else {
					break;
				}
			}
			// right
			i = 1;
			while (cur_x + i < width) {
				if (board[cur_x + i][cur_y] == color) {
					result.add(new IntPair(cur_x + i, cur_y));
					if (cur_y - 1 >= 0 && board[cur_x + i][cur_y - 1] == color) {
						if (result.add(new IntPair(cur_x + i, cur_y - 1)))
							queue.add(new IntPair(cur_x + i, cur_y - 1));
					}
					if (cur_y + 1 < height && board[cur_x + i][cur_y + 1] == color) {
						if (result.add(new IntPair(cur_x + i, cur_y + 1)))
							queue.add(new IntPair(cur_x + i, cur_y + 1));
					}
					i++;
				} else {
					break;
				}
			}
			queue.remove(0);
		}

		return new ArrayList<IntPair>(result);
	}

	/**
	 * Generate separate labels for each stone group using two-pass algorithm.
	 */
	private void createLabels() {
		// two-pass algorithm
		labeledBoard = new int[size][size];
		Map<Integer, Set<Integer>> labels = new HashMap<Integer, Set<Integer>>();
		int lowestLabel = 0;

		int[] neighbors = new int[2];

		// 1st pass
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j] != EMPTY) {
					neighbors[0] = 0;
					neighbors[1] = 0;
					// 0th neighbor
					if (i > 0 && board[i - 1][j] == board[i][j]) {
						neighbors[0] = labeledBoard[i - 1][j];
					}
					// 1st neighbor
					if (j > 0 && board[i][j - 1] == board[i][j]) {
						neighbors[1] = labeledBoard[i][j - 1];
					}

					if (neighbors[0] > 0 && neighbors[1] > 0) {
						// two neighbors
						if (neighbors[0] != neighbors[1]) {
							// different labels
							labeledBoard[i][j] = Math.min(neighbors[0], neighbors[1]);
							// store label equivalence
							labels.get(neighbors[0]).addAll(labels.get(neighbors[1]));
							labels.get(neighbors[1]).addAll(labels.get(neighbors[0]));
						} else {
							// same labels
							labeledBoard[i][j] = neighbors[0];
						}
					} else if (neighbors[0] > 0 && neighbors[1] == 0) {
						// one neighbor
						labeledBoard[i][j] = neighbors[0];
					} else if (neighbors[0] == 0 && neighbors[1] > 0) {
						// one neighbor
						labeledBoard[i][j] = neighbors[1];
					} else {
						// no neighbor -> new label
						lowestLabel++;
						labeledBoard[i][j] = lowestLabel;
						labels.put(lowestLabel, new HashSet<Integer>());
						labels.get(lowestLabel).add(lowestLabel);
					}

				} else {
					labeledBoard[i][j] = 0;
				}
			}
		}

		// 2nd pass
		for (int i = 0; i < labeledBoard.length; i++) {
			for (int j = 0; j < labeledBoard[i].length; j++) {
				if (labeledBoard[i][j] != 0) {
					// find lowest equivalent label
					labeledBoard[i][j] = Collections.min(labels.get(labeledBoard[i][j]));
				}
			}
		}

		// 3rd pass (relabel the labels to be consecutive integers)
		Map<Integer, Integer> newLabels = new HashMap<Integer, Integer>();
		groups = new HashMap<Integer, GoGroupType>();
		groupLockCount = new HashMap<Integer, Integer>();
		lowestLabel = 0;

		for (int i = 0; i < labeledBoard.length; i++) {
			for (int j = 0; j < labeledBoard[i].length; j++) {
				if (labeledBoard[i][j] != 0) {
					if (newLabels.containsKey(labeledBoard[i][j])) {
						labeledBoard[i][j] = newLabels.get(labeledBoard[i][j]);
					} else {
						lowestLabel++;
						newLabels.put(labeledBoard[i][j], lowestLabel);
						labeledBoard[i][j] = lowestLabel;
						groups.put(lowestLabel, GoGroupType.ALIVE);
						groupLockCount.put(lowestLabel, 0);
					}
				}
			}
		}

		// TODO Smarter group state detecting algorithm.
		// (Optional)

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getBoardWithLabeledGroups()
	 */
	@Override
	public int[][] getBoardWithLabeledGroups() {
		if (labeledBoard == null) {
			createLabels();
		}
		return labeledBoard;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getAllGroupLabels()
	 */
	@Override
	public int[] getAllGroupLabels() {
		if (labeledBoard == null) {
			createLabels();
		}

		int labels[] = new int[groups.keySet().size()];
		int i = 0;
		for (int label : groups.keySet()) {
			labels[i] = label;
			i++;
		}

		return labels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getGroupType(int)
	 */
	@Override
	public GoGroupType getGroupType(int label) {
		if (labeledBoard == null) {
			createLabels();
		}

		return groups.get(label);
	}

	/**
	 * Set the group with a given label's type to the given type.
	 * 
	 * @param label
	 *            Label of the group.
	 * @param type
	 *            New group type.
	 */
	protected void setGroupType(int label, GoGroupType type) {
		if (labeledBoard == null) {
			createLabels();
		}

		groups.put(label, type);
		groupLockCount.put(label, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#checkIfGroupIsLocked(int)
	 */
	@Override
	public boolean checkIfGroupIsLocked(int label) {
		if (labeledBoard == null) {
			createLabels();
		}

		return groupLockCount.get(label) >= 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#applyGroupTypeChanges(java.util.Map)
	 */
	@Override
	public boolean applyGroupTypeChanges(Map<Integer, GoGroupType> groupTypeChanges) {
		if (labeledBoard == null) {
			createLabels();
		}

		boolean changed = false;
		for (int label : groups.keySet()) {
			if (groupTypeChanges.containsKey(label) && !checkIfGroupIsLocked(label)
					&& getGroupType(label) != groupTypeChanges.get(label)) {
				setGroupType(label, groupTypeChanges.get(label));
				changed = true;
			} else {
				groupLockCount.put(label, Math.min(2, groupLockCount.get(label) + 1));
			}
		}
		return changed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#resetGroupLabels()
	 */
	public void resetGroupLabels() {
		labeledBoard = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getSize()
	 */
	public int getSize() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getBoardData()
	 */
	public int[][] getBoardData() {
		return board;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getPreviousBoardData()
	 */
	public int[][] getPreviousBoardData() {
		return previousBoard;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getBlackColor()
	 */
	@Override
	public int getBlackColor() {
		return BLACK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getWhiteColor()
	 */
	@Override
	public int getWhiteColor() {
		return WHITE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#getEmptyColor()
	 */
	@Override
	public int getEmptyColor() {
		return EMPTY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#setSuicideCheckEnabled(boolean)
	 */
	@Override
	public void setSuicideCheckEnabled(boolean checkEnabled) {
		suicideCheckEnabled = checkEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.GoBoard#calculateTerritoryScore()
	 */
	@Override
	public IntPair calculateTerritoryScore() {
		int territoryBoard[][] = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				territoryBoard[i][j] = board[i][j];
			}
		}

		int blackScore = 0;
		int whiteScore = 0;

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (territoryBoard[i][j] == EMPTY) {
					// unmarked territory
					Set<IntPair> territory = new HashSet<IntPair>();
					List<IntPair> queue = new ArrayList<IntPair>();

					int territoryColor = EMPTY;

					queue.add(new IntPair(i, j));

					int k, cur_x, cur_y;

					while (!queue.isEmpty()) {
						cur_x = queue.get(0).x;
						cur_y = queue.get(0).y;

						// current tile
						territory.add(new IntPair(cur_x, cur_y));
						if (cur_y - 1 >= 0) {
							if (board[cur_x][cur_y - 1] == EMPTY) {
								if (territory.add(new IntPair(cur_x, cur_y - 1)))
									queue.add(new IntPair(cur_x, cur_y - 1));
							} else {
								territoryColor = adjustTerritoryColor(territoryColor, board[cur_x][cur_y - 1]);
							}
						}
						if (cur_y + 1 < size) {
							if (board[cur_x][cur_y + 1] == EMPTY) {
								if (territory.add(new IntPair(cur_x, cur_y + 1)))
									queue.add(new IntPair(cur_x, cur_y + 1));
							} else {
								territoryColor = adjustTerritoryColor(territoryColor, board[cur_x][cur_y + 1]);
							}
						}
						// left
						k = 1;
						while (cur_x - k >= 0) {
							if (board[cur_x - k][cur_y] == EMPTY) {
								territory.add(new IntPair(cur_x - k, cur_y));
								if (cur_y - 1 >= 0) {
									if (board[cur_x - k][cur_y - 1] == EMPTY) {
										if (territory.add(new IntPair(cur_x - k, cur_y - 1)))
											queue.add(new IntPair(cur_x - k, cur_y - 1));
									} else {
										territoryColor = adjustTerritoryColor(territoryColor,
												board[cur_x - k][cur_y - 1]);
									}
								}
								if (cur_y + 1 < size) {
									if (board[cur_x - k][cur_y + 1] == EMPTY) {
										if (territory.add(new IntPair(cur_x - k, cur_y + 1)))
											queue.add(new IntPair(cur_x - k, cur_y + 1));
									} else {
										territoryColor = adjustTerritoryColor(territoryColor,
												board[cur_x - k][cur_y + 1]);
									}
								}
								k++;
							} else {
								break;
							}
						}
						// right
						k = 1;
						while (cur_x + k < size) {
							if (board[cur_x + k][cur_y] == EMPTY) {
								territory.add(new IntPair(cur_x + k, cur_y));
								if (cur_y - 1 >= 0) {
									if (board[cur_x + k][cur_y - 1] == EMPTY) {
										if (territory.add(new IntPair(cur_x + k, cur_y - 1)))
											queue.add(new IntPair(cur_x + k, cur_y - 1));
									} else {
										territoryColor = adjustTerritoryColor(territoryColor,
												board[cur_x + k][cur_y - 1]);
									}
								}
								if (cur_y + 1 < size) {
									if (board[cur_x + k][cur_y + 1] == EMPTY) {
										if (territory.add(new IntPair(cur_x + k, cur_y + 1)))
											queue.add(new IntPair(cur_x + k, cur_y + 1));
									} else {
										territoryColor = adjustTerritoryColor(territoryColor,
												board[cur_x + k][cur_y + 1]);
									}
								}
								k++;
							} else {
								break;
							}
						}
						queue.remove(0);
					}

					// mark territory
					int territorySize = 0;
					for (IntPair tile : territory) {
						territoryBoard[tile.x][tile.y] = territoryColor;
						territorySize++;
					}

					if (territoryColor == BLACK_TERRITORY) {
						blackScore += territorySize;
					} else if (territoryColor == WHITE_TERRITORY) {
						whiteScore += territorySize;
					}

				}
			}
		}

		return new IntPair(blackScore, whiteScore);
	}

	/**
	 * Returns adjusted territory color depending on the given stone color.
	 * (Used to determine to whom does a territory belong)
	 * 
	 * @param territoryColor
	 *            Previously determined territory color.
	 * @param stoneColor
	 *            Color of a stone touching the territory.
	 * @return Adjusted territory color.
	 */
	private int adjustTerritoryColor(int territoryColor, int stoneColor) {
		// unmarked territory, so it belongs to the player with the same color
		// as the stone
		if (territoryColor == EMPTY) {
			return stoneColorToTerritoryColor(stoneColor);
		}
		// territory belongs to both players
		if (territoryColor == DISPUTED_TERRITORY) {
			return DISPUTED_TERRITORY;
		}
		// territory belongs to both players
		if (territoryColor != stoneColorToTerritoryColor(stoneColor)) {
			return DISPUTED_TERRITORY;
		}
		return territoryColor;
	}

	/**
	 * Returns territory color matching the given stone color.
	 * @param stoneColor Stone color (BLACK or WHITE).
	 * @return BLACK_TERRITORY or WHITE_TERRITORY.
	 */
	private int stoneColorToTerritoryColor(int stoneColor) {
		if (stoneColor == EMPTY) {
			return EMPTY;
		}
		return (stoneColor == BLACK) ? BLACK_TERRITORY : WHITE_TERRITORY;
	}

	/* (non-Javadoc)
	 * @see goserver.game.GoBoard#removeDeadGroups()
	 */
	@Override
	public void removeDeadGroups() {
		if (labeledBoard == null) {
			return;
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (labeledBoard[i][j] != 0) {
					if (groups.get(labeledBoard[i][j]) == GoGroupType.DEAD) {
						board[i][j] = EMPTY;
					}
				}
			}
		}
	}

}
