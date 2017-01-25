/**
 * 
 */
package models.goserver.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.goserver.game.msg.*;
import models.goserver.game.rules.GoRuleset;
import models.goserver.util.IntPair;

/**
 * Default implementation of a Go game logic.
 * 
 * @author Kacper
 *
 */
public class ActorGoGame extends UntypedActor {

	GoPlayerRef[] players;
	ActorRef[] playerRefs;
	GoGame game;

	/**
	 * Create a Go game with two given players, board size and a ruleset.
	 * @param player1 Black player.
	 * @param player2 White player.
	 * @param boardSize Size of the board.
	 * @param ruleset The ruleset of the game.
	 */
	public ActorGoGame(ActorRef player1, ActorRef player2, int boardSize, GoRuleset ruleset) {
		players = new GoPlayerRef[2];
		playerRefs = new ActorRef[2];
		
		playerRefs[0] = player1;
		players[0] = new GoPlayerRef(player1, this);
		
		playerRefs[1] = player2;
		players[1] = new GoPlayerRef(player2, this);	
		
		game = new DefaultGoGame(players[0], players[1], boardSize, ruleset);
	}
	
	public GoGame getGame() {
		return game;
	}

	public GoPlayerRef getPlayerRefFromActorRef(ActorRef actor){
		if (playerRefs[0].equals(actor)){
			return players[0];
		} else if (playerRefs[1].equals(actor)){
			return players[1];
		} else {
			return null;
		}
	}

	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		ActorRef sender = getSender();
		GoPlayerRef player = getPlayerRefFromActorRef(sender);
		System.out.println("Got message " + message.getClass().toString() + " from " + sender.toString());
		if (player == null){
			
		} else {
			// message from player
			if (message instanceof Move){
				Move move = (Move) message;
				System.out.println("got move");
				try {
					game.makeMove(player, move.getX(), move.getY());
					sender.tell(new Accepted(), getSelf());
				} catch (InvalidMoveException e) {
					sender.tell(new Message(e.getMessage()), getSelf());
				} catch (IllegalArgumentException e){
					//ignore
				}
			} else if (message instanceof Pass){
				try {
					game.passTurn(player);
					sender.tell(new Accepted(), getSelf());
				} catch (IllegalArgumentException e){
					//ignore
				}
			} else if (message instanceof LabelsMap){
				LabelsMap groupTypeChanges = (LabelsMap) message;
				game.applyGroupTypeChanges(player, groupTypeChanges.getLabelsMap());
			} else if (message instanceof RematchRequest){
				game.requestRematch(player);
			} else {
				unhandled(message);
			}
		}
	}

}
