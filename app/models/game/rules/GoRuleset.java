/**
 * 
 */
package models.game.rules;

import java.util.ArrayList;
import java.util.List;

import models.game.GoBoard;
import models.game.GoGame;
import models.game.InvalidMoveException;

/**
 * Ruleset containing a collection of rules.
 * 
 * @author Kacper
 *
 */
public class GoRuleset implements GoRule {

	/**
	 * List of rules.
	 */
	List<GoRule> rules;

	/**
	 * Create an empty ruleset object.
	 */
	public GoRuleset() {
		rules = new ArrayList<GoRule>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.rules.GoRule#onGameStart(goserver.game.GoGame)
	 */
	public void onGameStart(GoGame game) {
		for (GoRule rule : rules) {
			rule.onGameStart(game);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.rules.GoRule#onGameEnd(goserver.game.GoGame)
	 */
	public void onGameEnd(GoGame game) {
		for (GoRule rule : rules) {
			rule.onGameEnd(game);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see goserver.game.rules.GoRule#validateMove(goserver.game.GoBoard, int,
	 * int, int)
	 */
	public boolean validateMove(GoBoard board, int color, int x, int y) throws InvalidMoveException {
		boolean isValid = true;
		for (GoRule rule : rules) {
			isValid = isValid && rule.validateMove(board, color, x, y);
		}
		return isValid;
	}

	/**
	 * Add a new rule to the ruleset.
	 * 
	 * @param rule
	 *            Rule object.
	 */
	public void addRule(GoRule rule) {
		rules.add(rule);
	}

	/**
	 * Add a new rule to the ruleset and return the ruleset object.
	 * 
	 * @param rule
	 *            Rule object.
	 * @return Ruleset object with the added rule.
	 */
	public GoRuleset with(GoRule rule) {
		addRule(rule);
		return this;
	}

	// TODO
	// onGameStart()
	// onGameEnd() / onScoreCount()

}
