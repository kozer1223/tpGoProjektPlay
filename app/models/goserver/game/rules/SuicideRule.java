/**
 * 
 */
package models.goserver.game.rules;

import java.util.ArrayList;
import java.util.List;

import models.goserver.game.GoBoard;
import models.goserver.game.GoGame;
import models.goserver.game.InvalidMoveException;
import models.goserver.util.IntPair;

/**
 * Suicide rule implementation.
 * 
 * @author Kacper
 *
 */
public class SuicideRule implements GoRule {

	private static SuicideRule instance;
	/**
	 * Message thrown when violating the suicide rule.
	 */
	public static final String invalidMoveMessage = "Suicide rule.";

	private SuicideRule() {
	};

	/**
	 * @return Instance of SuicideRule.
	 */
	public synchronized static SuicideRule getInstance() {
		if (instance == null) {
			instance = new SuicideRule();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see goserver.game.rules.GoRule#onGameStart(goserver.game.GoGame)
	 */
	@Override
	public void onGameStart(GoGame game) {
		game.getBoard().setSuicideCheckEnabled(true);
	}

	/* (non-Javadoc)
	 * @see goserver.game.rules.GoRule#validateMove(goserver.game.GoBoard, int, int, int)
	 */
	@Override
	public boolean validateMove(GoBoard board, int color, int x, int y) throws InvalidMoveException {
		return true;
	}

	/* (non-Javadoc)
	 * @see goserver.game.rules.GoRule#onGameEnd(goserver.game.GoGame)
	 */
	@Override
	public void onGameEnd(GoGame game) {
	}

}
