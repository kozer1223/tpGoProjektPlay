package models.goserver.game;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.goserver.game.msg.*;
import models.goserver.util.IntPair;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

public class GoBotActor extends UntypedActor {
	
	private ActorRef game;
	private int boardSize;
	private int color;
	private int[][] boardData;
	private int gamePhase;

	private List<IntPair> validMoves;
	private int lastIndex;
	private GoMoveType lastMove;
	Random rng;
	final double passChance = 0.1;
	

	public GoBotActor() {
		rng = new Random();
		boardData = null;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if (message instanceof JoinGame){
			JoinGame join = (JoinGame) message;
			game = join.getGame();
			boardSize = join.getBoardSize();
			color = join.getColor();
			gamePhase = 0;
			lastIndex = 0;
			lastMove = null;
			
			boardData = new int[boardSize][boardSize];
		} else if (message instanceof Begin){
			gamePhase = 0;
			lastIndex = 0;
			lastMove = null;
			
			boardData = new int[boardSize][boardSize];
		} else if (message instanceof Board){
			Board board = (Board) message;
			boardData = board.getBoard();
		} else if (message instanceof Turn){
			Turn turn = (Turn) message;
			lastMove = turn.getLastMove();
			getValidMoves();
			playTurn(lastMove);
		} else if (message instanceof Accepted){
			// cool
		} else if (message instanceof Message){
			validMoves.remove(lastIndex);
			playTurn(lastMove);
		} else if (message instanceof GamePhase){
			GamePhase phaseChange = (GamePhase) message;
			this.gamePhase = phaseChange.getPhase();
		} else if (message instanceof Score){
			// cool
			this.gamePhase = 2;
			game.tell(new RematchRequest(), getSelf());
		}  else {
			unhandled(message);
		}

	}
	
	public void getValidMoves() {
		validMoves = new ArrayList<IntPair>();

		for (int i = 0; i < boardData.length; i++) {
			for (int j = 0; j < boardData[i].length; j++) {
				if (boardData[i][j] == 0) {
					validMoves.add(new IntPair(i, j));
				}
			}
		}
	}

	public void playTurn(GoMoveType lastMove) {
		if (gamePhase == 0){
			if (lastMove == GoMoveType.PASS) {
				double passRnd = rng.nextDouble();
				if (passRnd >= passChance) {
					passTurn();
					return;
				}
			}
			
			if (!validMoves.isEmpty()){
				lastIndex = rng.nextInt(validMoves.size());
				makeMove(validMoves.get(lastIndex).x, validMoves.get(lastIndex).y);
			} else {
				passTurn();
			}
		} else {
			game.tell(new LabelsMap(new HashMap<Integer, GoGroupType>()), getSelf());
		}
	}
	
	private void makeMove(int x, int y) {
		game.tell(new Move(x, y), getSelf());
	}

	private void passTurn() {
		game.tell(new Pass(), getSelf());
	}

}
