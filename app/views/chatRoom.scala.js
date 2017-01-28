@(username: String, boardSize: Integer, opponent: String)

$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
	var chatSocket = new WS("@routes.Application.chat(username, boardSize, opponent).webSocketURL(request)")


    var sendMessage = function() {
        chatSocket.send(JSON.stringify( {nr: $("#nr").val()} ))
        $("#nr").val('')

    }
	
	var board = []
	var labeledBoard = []
	for (var i = 0; i <= @boardSize+1; i++){
		board[i] = [];
		labeledBoard[i] = [];
	}
	var lockedGroups
	var gamePhase = 0
	var labelsMap = [];
	var playersTurn;
	var color = 0;
	
	var redrawBoard = function() {
		for (var i = 1; i <= @boardSize; i++){
			for (var j = 1; j <= @boardSize; j++){
				if (gamePhase == 0){
					var className = "empty"
					if (board[i][j] == 1){
						className = "black"
					} else if (board[i][j] == 2){
						className = "white"
					}
					$("#"+i+"_"+j).attr('class', className);
				} else {
					if (lockedGroups != undefined && labelsMap != undefined){
						var className = "empty";
						if (board[i][j] == 1){
							className = "black"
						} else if (board[i][j] == 2){
							className = "white"
						}
						if (board[i][j] != 0){
							var label = labeledBoard[i][j]
							if (labelsMap[label] === "A"){
								className = className.concat("_alive")
							} else {
								className = className.concat("_dead")
							}
							if (lockedGroups.indexOf(label) != -1){
								className = className.concat("_locked")
							}
						}
						$("#"+i+"_"+j).attr('class', className);
					}
				}
			}
		}
	}
	
	var displayMessage = function(message){
		$("#messageDisplay").html(message);
	}
	
	var displayCaptured = function(blackScore, whiteScore){
		$("#blackData").html("Black (" + (color == 0 ? "You" : "Opponent") + "):<br/>" + blackScore)
		$("#whiteData").html("White (" + (color == 1 ? "You" : "Opponent") + "):<br/>" + whiteScore)
	}

    var receiveEvent = function(event) {
        var data = JSON.parse(event.data)

        // Handle errors
        if(data.error) {
            chatSocket.close()
            $("#onError span").text(data.error)
            $("#onError").show()
            return
        } 
        else {
            $("#onChat").show()
        }        
        // Create the message element  
		if(data.board != undefined){
			var boardRaw = data.board;
			var res = boardRaw.split(" ");
			var rowCount = 1;
			var colCount = 1;
			for (var i = 0; i < res.length; i++) {
				board[rowCount][colCount] = res[i]
				colCount++;
				if(colCount > @boardSize){
					rowCount++;
					colCount = 1;
				}
			}
			redrawBoard();
		} else if (data.labeled_board != undefined){
			var boardRaw = data.labeled_board;
			var res = boardRaw.split(" ");
			var rowCount = 1;
			var colCount = 1;
			for (var i = 0; i < res.length; i++) {
				labeledBoard[rowCount][colCount] = res[i]
				colCount++;
				if(colCount > @boardSize){
					rowCount++;
					colCount = 1;
				}
			}
			redrawBoard();
		} else if (data.locked_groups != undefined){
			var lockedGroupsRaw = data.locked_groups;
			lockedGroups = lockedGroupsRaw.split(" ");
			redrawBoard();
		} else if (data.labels_map != undefined){
			labelsMap = data.labels_map;
			redrawBoard();
		} else if (data.message != undefined){
			/*var el = $('<div class="message"><p style="font-size:16px"></p></div>')
			$("p", el).text(data.message)
			$(el).addClass('me')
			$('#messages').append(el)*/
		}
		
		if (data.err_message != undefined){
			displayMessage(data.err_message)
		}
		
		if (data.turn != undefined){
			yourTurn();
			displayMessage("&nbsp")
		}
		
		if (data.accepted != undefined){
			oppTurn();
			displayMessage("&nbsp")
		}
		
		if (data.captured != undefined){
			var res = data.captured.split(" ");
			displayCaptured(res[0], res[1])
		}
		
		if (data.join != undefined && data.join){
			$("#waiting").html("&nbsp")
		}
		
		if (data.begin != undefined && data.begin){
			$("#waiting").html("&nbsp")
			displayCaptured(0, 0)
		}
		
		if (data.color != undefined){
			color = data.color
		}
		
		if (data.denied != undefined && data.denied){
			$("#waiting").html("Rematch denied.")
			window.alert("Rematch denied")
			oppTurn();
		}
		
		if (data.score != undefined){
			oppTurn();
			var rematch = confirm(data.score + "\nRematch?")
			if (rematch){
				$("#waiting").html("Waiting for reply...")
				chatSocket.send(JSON.stringify( {type: "rematch"} ))
				for (var i=1; i <= @boardSize; i++){
					for (var j=1; j <= @boardSize; j++){
						board[i][j] = 0
					}
				}
				$("#waiting").html("Waiting for reply...")
				redrawBoard(0);
			} else {
				// TODO
				chatSocket.send(JSON.stringify( {type: "deny_rematch"} ))
			}
		}
		
		if (data.phase != undefined){
			gamePhase = data.phase
		}

    }

    var handleReturnKey = function(e) {
        if(e.charCode == 13 || e.keyCode == 13) {
            e.preventDefault()
            sendMessage()
        }
    }

    $("#nr").keypress(handleReturnKey)
	
	var pressStone = function(event) {
		if (playersTurn){
			var ids = event.target.id.match(/([0-9]+)_([0-9]+)/);
			if (gamePhase == 0){
				chatSocket.send(JSON.stringify( {type: "move", x: ids[1]-1, y: ids[2]-1} ))
			} else {
				if (labelsMap != undefined){
					var label = labeledBoard[ids[1]][ids[2]]
					if (labelsMap[label] != undefined && lockedGroups.indexOf(label) == -1){
						labelsMap[label] = (labelsMap[label] === "A" ? "D" : "A")
						redrawBoard()
					}
				}
			}
		}
	}
	
	for(var i=1; i<=@boardSize; i++){
		for(var j=1; j<=@boardSize; j++){
			$("#"+i+"_"+j).click(pressStone)
		}
	}
	
	var passTurn = function(event) {
		chatSocket.send(JSON.stringify( {type: "pass"} ))
	}
	
	var applyChanges = function(event) {
		chatSocket.send(JSON.stringify( {type: "apply", changes_map: labelsMap } ))
		if (playersTurn && gamePhase == 1){
			oppTurn();
		}
	}
	
	var yourTurn = function() {
		playersTurn = true;
		$("#pass").attr('disabled', false);
		if (gamePhase == 1){
			$("#apply").attr('disabled', false);
		}
	}
	
	var oppTurn = function() {
		playersTurn = false;
		$("#pass").attr('disabled', true);
		$("#apply").attr('disabled', true);
	}
	
	$("#pass").click(passTurn)
	$("#apply").click(applyChanges)
	$("#pass").attr('disabled', true);
	$("#apply").attr('disabled', true);


    chatSocket.onmessage = receiveEvent

})
