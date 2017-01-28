package models;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import models.game.ActorGoGame;
import models.game.DefaultGoRuleset;
import models.game.GoBotActor;
import models.msg.JoinLobby;
import models.msg.Quit;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.WebSocket;

public class WaitingLobby extends UntypedActor {
	
	static ActorRef defaultWaitingLobby = Akka.system().actorOf(Props.create(WaitingLobby.class));
	
	public static void joinDefaultLobby(String name, int boardSize, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
		defaultWaitingLobby.tell(new JoinLobby(name, boardSize, in, out), ActorRef.noSender());
	}
	
	private Map<Integer, ActorRef> waitingPlayers;

	public WaitingLobby() {
		waitingPlayers = new HashMap<Integer, ActorRef>();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof JoinLobby){
			final JoinLobby joinLobby = (JoinLobby) message;
			final ActorRef player = Akka.system().actorOf(new Props(new UntypedActorFactory() {
				public UntypedActor create() {
					return new ActorGoPlayer(joinLobby.getName(), joinLobby.getIn(), joinLobby.getOut(), getSelf());
				}
			}));
            ObjectNode event = Json.newObject();
            
            joinLobby.getOut().write(event);
			if (waitingPlayers.containsKey(joinLobby.getBoardSize())){
				//start game
				ActorRef game = Akka.system()
						.actorOf(Props.create(ActorGoGame.class, waitingPlayers.get(joinLobby.getBoardSize()), player,
								joinLobby.getBoardSize(), DefaultGoRuleset.getDefaultRuleset()));
				waitingPlayers.remove(joinLobby.getBoardSize());
			} else {
				//new waiting player
				waitingPlayers.put(joinLobby.getBoardSize(), player);
			}
		} else if (message instanceof Quit){
			if(waitingPlayers.containsValue(getSender())){
				for(Integer key : waitingPlayers.keySet()){
					if(waitingPlayers.get(key).equals(getSender())){
						waitingPlayers.remove(key);
					}
				}
			}
		}
		
	}

	public static void playWithBot(final String username, final int boardSize, final play.mvc.WebSocket.In<JsonNode> in,
			final play.mvc.WebSocket.Out<JsonNode> out) {
		final ActorRef player = Akka.system().actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new ActorGoPlayer(username, in, out, ActorRef.noSender());
			}
		}));
		final ActorRef bot = Akka.system().actorOf(new Props(GoBotActor.class));
		ActorRef game = Akka.system()
				.actorOf(Props.create(ActorGoGame.class, player, bot, boardSize, DefaultGoRuleset.getDefaultRuleset()));
	}
	

}
