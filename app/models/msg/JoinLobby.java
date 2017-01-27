package models.msg;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.WebSocket;

public class JoinLobby {
	private final String name;
	private final int boardSize;
	private final WebSocket.In<JsonNode> in;
	private final WebSocket.Out<JsonNode> out;

	public JoinLobby(String name, int boardSize, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
		this.name = name;
		this.boardSize = boardSize;
		this.in = in;
		this.out = out;
	}

	public String getName() {
		return name;
	}

	public int getBoardSize() {
		return boardSize;
	}

	public WebSocket.In<JsonNode> getIn() {
		return in;
	}

	public WebSocket.Out<JsonNode> getOut() {
		return out;
	}

}
