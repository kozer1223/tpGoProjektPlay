package models.game.rules;

import models.game.GoBoard;
import models.game.GoGame;
import models.game.InvalidMoveException;
import models.util.MatrixUtil;

/**
 * Komi rule implementation.
 * 
 * @author Kacper
 *
 */
public class KomiRule implements GoRule {

	private static KomiRule instance;
	/**
	 * Points awarded to White player.
	 */
	public static final double komiPoints = 6.5;

	private KomiRule() {
	};

	/**
	 * @return Instance of KomiRule.
	 */
	public synchronized static KomiRule getInstance() {
		if (instance == null) {
			instance = new KomiRule();
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
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.rules.GoRule#onGameEnd(goserver.game.GoGame)
	 */
	@Override
	public void onGameEnd(GoGame game) {
		double player2Score = game.getPlayersScore(game.getPlayer2());
		player2Score += komiPoints;
		game.setPlayersScore(game.getPlayer2(), player2Score);
	}

}
