package models;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import models.game.ActorGoGame;
import models.game.DefaultGoRuleset;
import models.game.GoBotActor;
import models.game.GoPlayerRef;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ActorSystem system = ActorSystem.create("game");
		final ActorRef player1 = system.actorOf(new Props(GoBotActor.class), "player1");
		final ActorRef player2 = system.actorOf(new Props(GoBotActor.class), "player2");
		final int size = 9;
		
		ActorRef game = system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new ActorGoGame(player1, player2, size, DefaultGoRuleset.getDefaultRuleset());
			}
		}), "game");

	}

}
