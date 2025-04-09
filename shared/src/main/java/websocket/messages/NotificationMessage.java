package websocket.messages;

public class NotificationMessage extends ServerMessage{
    public String message;

    public NotificationMessage(String inMsg){
        super(ServerMessageType.NOTIFICATION);
        message = inMsg;
    }
}
