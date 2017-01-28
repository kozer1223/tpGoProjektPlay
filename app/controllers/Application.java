package controllers;

import play.Logger;
import play.libs.Akka;
import play.mvc.*;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import views.html.*;
import models.*;

public class Application extends Controller {
  
    /**
     * Display the home page.
     */
    public static Result index() {
        return ok(index.render());
    }
  
    /**
     * Display the chat room.
     */
    public static Result chatRoom(String username, Integer boardSize, String opponent) {
    	flash("error", "Please choose a valid life choice.");
        if(username == null || username.trim().equals("")) {
            flash("error", "Please choose a valid username.");
            return redirect(routes.Application.index());
        }
        if(boardSize < 2 || boardSize > 30) {
            flash("error", "Please choose a valid board size.");
            return redirect(routes.Application.index());
        }
        if(!opponent.equals("player") && !opponent.equals("bot")) {
            flash("error", "Please choose a valid opponent.");
            return redirect(routes.Application.index());
        }
        return ok(chatRoom.render(username, boardSize, opponent));
    }

    public static Result chatRoomJs(String username, int boardSize, String opponent) {
        return ok(views.js.chatRoom.render(username, boardSize, opponent));
    }
    
    /**
     * Handle the chat websocket.
     */
    public static WebSocket<JsonNode> chat(final String username, final int boardSize, final String opponent) {
        return new WebSocket<JsonNode>() {
            
            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
                
            	/*Table.notifyAll(new Info("I've got a WebSocket", username));
            	
                // Join the chat room.
                try {                	
                	Table.join(username, in, out);
                	
                } catch (Exception ex) {
                    ex.printStackTrace();
                }*/
            	if (opponent.equals("player")){
            		WaitingLobby.joinDefaultLobby(username, boardSize, in, out);
            	} else if (opponent.equals("bot")){
            		WaitingLobby.playWithBot(username, boardSize, in, out);
        			
            	}
            	
            }
        };
    }
  
}
