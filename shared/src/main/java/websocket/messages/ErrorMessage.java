package websocket.messages;

public class ErrorMessage extends ServerMessage{
    public String errorMessage;

    public ErrorMessage(String inMsg){
        super(ServerMessageType.ERROR);
        errorMessage = inMsg;
    }
}
