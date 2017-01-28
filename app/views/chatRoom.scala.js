@(username: String, boardSize: Integer, opponent: String)

$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
	var chatSocket = new WS("@routes.Application.chat(username, boardSize, opponent).webSocketURL(request)")


    var sendMessage = function() {
        chatSocket.send(JSON.stringify( {nr: $("#nr").val()} ))
        $("#nr").val('')

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
				var className = "empty";
				if (res[i] == 1){
					className = "black"
				} else if (res[i] == 2){
					className = "white"
				}
				$("#"+rowCount+"_"+colCount).attr('class', className);
				colCount++;
				if(colCount > @boardSize){
					rowCount++;
					colCount = 1;
				}
			}
		} else if (data.message != undefined){
			var el = $('<div class="message"><p style="font-size:16px"></p></div>')
			$("p", el).text(data.message)
			$(el).addClass('me')
			$('#messages').append(el) 
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
		var ids = event.target.id.match(/([0-9]+)_([0-9]+)/);
		chatSocket.send(JSON.stringify( {type: "move", x: ids[1]-1, y: ids[2]-1} ));
	}
	
	for(var i=1; i<=@boardSize; i++){
		for(var j=1; j<=@boardSize; j++){
			$("#"+i+"_"+j).click(pressStone)
			//$("#"+i+"_"+j).click()
		}
	}
	//$("#board").click(pressStone)


    chatSocket.onmessage = receiveEvent

})
