package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String errorMessage;

    public ErrorMessage(String inMsg){
        super(ServerMessageType.ERROR);
        errorMessage = inMsg;
    }
}
