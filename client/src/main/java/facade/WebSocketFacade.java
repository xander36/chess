package facade;


import chess.ChessMove;
import com.google.gson.Gson;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.out.println("Code 500: Can't make websocket cuz of " + ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void leaveGame(String username) throws WebSocketException {
        var action = new Action(Action.Type.LEAVE, username, null);
        sendAction(action);
    }

    public void resign(String username) throws WebSocketException {
        var action = new Action(Action.Type.RESIGN, username, null);
        sendAction(action);
    }

    public void move(String username, ChessMove move) throws WebSocketException{
        var action = new Action(Action.Type.MOVE, username, move);
        sendAction(action);
    }

    private void sendAction (Action action) throws WebSocketException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new WebSocketException(500, ex.getMessage());
        }
    }

}
