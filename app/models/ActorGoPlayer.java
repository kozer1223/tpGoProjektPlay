package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import models.game.GoGroupType;
import models.game.msg.*;
import models.msg.*;
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
	private int[][] labeledBoardData;
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
					System.out.println("!!!");
					String type = event.get("type").asText();
					System.out.println(type);
					if (type.equals("move")){
						int x = event.get("x").asInt();
						int y = event.get("y").asInt();
						if (game != null){
							game.tell(new Move(x,y), getSelf());
						}
					} else if (type.equals("pass")){
						if (game != null){
							game.tell(new Pass(), getSelf());
						}
					} else if (type.equals("apply")){
						if (game != null){
							Map<Integer, GoGroupType> changes = new HashMap<Integer, GoGroupType>();
							Iterator<Entry<String, JsonNode>> it = event.get("changes_map").fields();
							while(it.hasNext()){
								Entry<String, JsonNode> entry = it.next();
								String value = entry.getValue().asText();
								System.out.println(value);
								GoGroupType g = (value.equals("A") ? GoGroupType.ALIVE : GoGroupType.DEAD);
								changes.put(Integer.parseInt(entry.getKey()), g);
							}
							System.out.println("aaa");
							System.out.println(event.get("changes_map").asText());
							game.tell(new LabelsMap(changes), getSelf());
						}
					} else if (type.equals("rematch")){
						game.tell(new RematchRequest(), getSelf());
					} else if (type.equals("deny_rematch")){
						game.tell(new RematchDenial(), getSelf());
					}

					
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
	
	public String stringifyBoard(int[][] board){
		StringBuilder string = new StringBuilder();
		for(int i=0; i<board.length; i++){
			for(int j=0; j<board[i].length; j++){
				string.append(board[i][j] + " ");
			}
		}
		return string.toString();
	}
	
	public String stringifyList(List<?> list){
		StringBuilder string = new StringBuilder();
		for(Object e : list){
			string.append(e.toString() + " ");
		}
		return string.toString();
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
            event.put("message", "Joined game " + color); 
            event.put("phase", 0);
            
            out.write(event);
		} else if (message instanceof Begin) {
			gamePhase = 0;

			boardData = new int[boardSize][boardSize];
			
            ObjectNode event = Json.newObject();
            event.put("message", "Game start");
            event.put("phase", 0);
            
            out.write(event);
		} else if (message instanceof Board) {
			Board board = (Board) message;
			boardData = board.getBoard();
			
            ObjectNode event = Json.newObject();
            event.put("board", stringifyBoard(boardData));
            
            out.write(event);
		} else if (message instanceof LabeledBoard) {
			LabeledBoard labeledBoard = (LabeledBoard) message;
			labeledBoardData = labeledBoard.getLabeledBoard();
			
            ObjectNode event = Json.newObject();
            event.put("labeled_board", stringifyBoard(labeledBoardData));
            
            out.write(event);
		} else if (message instanceof LockedGroups) {
			LockedGroups lockedGroups = (LockedGroups) message;
			List<Integer> groupsList = lockedGroups.getLockedGroups();
			
            ObjectNode event = Json.newObject();
            event.put("locked_groups", stringifyList(groupsList));
            
            out.write(event);
		} else if (message instanceof LabelsMap) {
			LabelsMap labelsMap = (LabelsMap) message;
			Map<Integer, GoGroupType> map = labelsMap.getLabelsMap();
			

            //event.put("lables_map", new JSONObject(labelsMap));
            ObjectNode event = Json.newObject();
            
            ObjectNode innerNode = Json.newObject();
            for (Integer key : map.keySet()){
            	String value = (map.get(key).equals(GoGroupType.ALIVE)) ? "A" : "D";
            	innerNode.put(key.toString(), value);
            }
            event.put("labels_map", innerNode);
            
            System.out.println(event);
            
            out.write(event);
		} else if (message instanceof Turn) {
			Turn turn = (Turn) message;

            ObjectNode event = Json.newObject();
            event.put("message", "Your turn"); 
            
            out.write(event);
		} else if (message instanceof Accepted) {
            ObjectNode event = Json.newObject();
            event.put("message", "Move accepted"); 
            
            out.write(event);
		} else if (message instanceof Message) {
			Message errMessage = (Message) message;
			
            ObjectNode event = Json.newObject();
            event.put("message", errMessage.getMessage()); 
            
            out.write(event);
		} else if (message instanceof GamePhase) {
			GamePhase phaseChange = (GamePhase) message;
			this.gamePhase = phaseChange.getPhase();
			
            ObjectNode event = Json.newObject();
            event.put("message", "Phase " + gamePhase); 
            event.put("phase", gamePhase);
            
            out.write(event);
		} else if (message instanceof Score) {
			Score score = (Score) message;
			this.gamePhase = 2;
			//game.tell(new RematchRequest(), getSelf());
            ObjectNode event = Json.newObject();
            event.put("message", "Black: " + score.getScore1() + " White: " + score.getScore2()); 
            event.put("score", "Black: " + score.getScore1() + " White: " + score.getScore2()); 
            
            out.write(event);
			
		} else if (message instanceof CapturedStones){
			CapturedStones capturedStones = (CapturedStones) message;
            ObjectNode event = Json.newObject();
            event.put("message", "Black: " + capturedStones.getCapturedStones1() + " White: " + capturedStones.getCapturedStones2()); 
            
            out.write(event);
		} else if (message instanceof RematchDenial){
            ObjectNode event = Json.newObject();
            event.put("message", "Rematch denied"); 
            
            out.write(event);
		} else {
			unhandled(message);
		}
	}

}
