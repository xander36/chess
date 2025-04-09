package websocket.messages;

public class NotificationMessage extends ServerMessage{
    String message;

    public NotificationMessage(String inMsg){
        super(ServerMessageType.NOTIFICATION);
        message = inMsg;
    }
}
