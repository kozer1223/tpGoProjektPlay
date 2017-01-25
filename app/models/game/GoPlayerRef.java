package models.game;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.game.msg.*;
import models.util.MatrixUtil;

public class GoPlayerRef implements GoPlayer {
	
	private ActorGoGame game;
	private final ActorRef actor;

	public GoPlayerRef(ActorRef actor, ActorGoGame game) {
		this.actor = actor;
		this.game = game;
	}
	
	public ActorRef getActorReference() {
		return actor;
	}

	@Override
	public void setGame(GoGame game) {
		actor.tell(new JoinGame(this.game.getSelf(), game.getBoard().getSize(), 0), this.game.getSelf());
	}

	@Override
	public void notifyAboutGameBegin() {
		// TODO Auto-generated method stub
		actor.tell(new Begin(), this.game.getSelf());
	}

	@Override
	public void notifyAboutTurn(GoMoveType opponentsMove) {
		// TODO Auto-generated method stub
		actor.tell(new Turn(opponentsMove), game.getSelf());
		
	}

	@Override
	public void updateBoard() {
		MatrixUtil.printMatrix(game.getGame().getBoard().getBoardData());
		
		if (game != null) {
			if (game.getGame().isStonePlacingPhase()) {
				actor.tell(new Board(MatrixUtil.copyMatrix(game.getGame().getBoard().getBoardData())), game.getSelf());
				actor.tell(new CapturedStones(game.getGame().getPlayersCapturedStones(game.getGame().getPlayer1()),
						game.getGame().getPlayersCapturedStones(game.getGame().getPlayer2())), game.getSelf());
			} else {
				actor.tell(new LabeledBoard(MatrixUtil.copyMatrix(game.getGame().getBoard().getBoardWithLabeledGroups())),
						game.getSelf());
				actor.tell(new LabelsMap(game.getGame().getLabelsMap()), game.getSelf());
				actor.tell(new LockedGroups(game.getGame().getAllLockedGroups()), game.getSelf());
			}
		}
		
	}

	@Override
	public void notifyAboutGamePhaseChange(int gamePhase) {
		actor.tell(new GamePhase(gamePhase), game.getSelf());
	}

	@Override
	public void notifyAboutGameEnd(double blackScore, double whiteScore) {
		actor.tell(new Score(blackScore, whiteScore), game.getSelf());
	}

	@Override
	public void rematchAccepted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rematchDenied() {
		actor.tell(new RematchDenial(), game.getSelf());
	}

}
