package models.game.msg;

import akka.actor.ActorRef;

public class JoinGame {
	
	private final ActorRef game;
	private final int boardSize;
	private final int color;

	public JoinGame(ActorRef game, int boardSize, int color) {
		this.game = game;
		this.boardSize = boardSize;
		this.color = color;
	}

	public ActorRef getGame() {
		return game;
	}

	public int getBoardSize() {
		return boardSize;
	}

	public int getColor() {
		return color;
	}

}
