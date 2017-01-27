package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.game.msg.Accepted;
import models.game.msg.Begin;
import models.game.msg.Board;
import models.game.msg.GamePhase;
import models.game.msg.JoinGame;
import models.game.msg.Message;
import models.game.msg.RematchRequest;
import models.game.msg.Score;
import models.game.msg.Turn;
import models.msg.Quit;
import play.Logger;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;

public class ActorGoPlayer extends UntypedActor {

	private String name;
	private ActorRef game;
	private ActorRef lobby;
	private int boardSize;
	private int color;
	private int[][] boardData;
	private int gamePhase;

	protected WebSocket.In<JsonNode> in;
	protected WebSocket.Out<JsonNode> out;

	public ActorGoPlayer(String name, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out, ActorRef lobbyRef) {
		this.name = name;
		this.in = in;
		this.out = out;
		this.lobby = lobbyRef;
		
		this.in.onMessage(new Callback<JsonNode>() {
			@Override
			public void invoke(JsonNode event) {
				try {
					//int nr = event.get("nr").asInt();
					//getSelf().tell(new Move(nr, name), getSelf());
				} catch (Exception e) {
					Logger.error("invokeError");
				}

			}
		});

		this.in.onClose(new Callback0() {
			@Override
			public void invoke() {
				lobby.tell(new Quit(), getSelf());
			}
		});
	}

	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		if (message instanceof JoinGame) {
			JoinGame join = (JoinGame) message;
			game = join.getGame();
			boardSize = join.getBoardSize();
			color = join.getColor();
			gamePhase = 0;

			boardData = new int[boardSize][boardSize];
			
            ObjectNode event = Json.newObject();
            event.put("message", "Joined game"); 
            
            out.write(event);
		} else if (message instanceof Begin) {
			gamePhase = 0;

			boardData = new int[boardSize][boardSize];
			
            ObjectNode event = Json.newObject();
            event.put("message", "Game start"); 
            
            out.write(event);
		} else if (message instanceof Board) {
			Board board = (Board) message;
			boardData = board.getBoard();
		} else if (message instanceof Turn) {
			Turn turn = (Turn) message;

            ObjectNode event = Json.newObject();
            event.put("message", "Your turn"); 
            
            out.write(event);
		} else if (message instanceof Accepted) {
			// cool
		} else if (message instanceof Message) {

		} else if (message instanceof GamePhase) {
			GamePhase phaseChange = (GamePhase) message;
			this.gamePhase = phaseChange.getPhase();
		} else if (message instanceof Score) {
			// cool
			this.gamePhase = 2;
			game.tell(new RematchRequest(), getSelf());
		} else {
			unhandled(message);
		}
	}

}
