package models.game.rules;

import models.game.GoBoard;
import models.game.GoGame;
import models.game.InvalidMoveException;
import models.util.MatrixUtil;

/**
 * Ko Rule implementation.
 * 
 * @author Kacper
 *
 */
public class KoRule implements GoRule {

	private static KoRule instance;
	/**
	 * Message shown for move invalid due to the Ko Rule.
	 */
	public static final String invalidMoveMessage = "Ko rule.";

	private KoRule() {
	};

	/**
	 * @return Instance of KoRule.
	 */
	public synchronized static KoRule getInstance() {
		if (instance == null) {
			instance = new KoRule();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.rules.GoRule#onGameStart(goserver.game.GoGame)
	 */
	@Override
	public void onGameStart(GoGame game) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.rules.GoRule#validateMove(goserver.game.GoBoard, int,
	 * int, int)
	 */
	@Override
	public boolean validateMove(GoBoard board, int color, int x, int y) throws InvalidMoveException {
		if (board.getPreviousBoardData()[x][y] == color) {
			// mozliwe ko
			MockGoBoard mockBoard = new MockGoBoard(board.getSize());
			mockBoard.setBoard(MatrixUtil.copyMatrix(board.getBoardData()));

			mockBoard.placeStone(color, x, y);
			if (MatrixUtil.compareMatrix(board.getPreviousBoardData(), mockBoard.getBoardData())) {
				// powtorzenie planszy
				throw new InvalidMoveException(invalidMoveMessage);
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.rules.GoRule#onGameEnd(goserver.game.GoGame)
	 */
	@Override
	public void onGameEnd(GoGame game) {
	}

}
