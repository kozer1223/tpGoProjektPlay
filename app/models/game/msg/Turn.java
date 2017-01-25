package models.game.msg;

import models.game.GoMoveType;

public class Turn {
	
	final GoMoveType lastMove;

	public Turn(GoMoveType lastMove) {
		this.lastMove = lastMove;
	}

	public GoMoveType getLastMove() {
		return lastMove;
	}

}
