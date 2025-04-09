package facade;


import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;

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
                    System.out.println("avst, it be arriving on cliently shores");
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

    public void leaveGame(String authToken, int gameID) throws WebSocketException {
        var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        sendObject(action);
    }

    public void resign(String authToken, int gameID) throws WebSocketException {
        var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        sendObject(action);
    }

    public void move(String authToken, int gameID, ChessMove move) throws WebSocketException{
        var action = new MakeMoveCommand(authToken, gameID, move);
        sendObject(action);
    }

    public void connect(String authToken, int gameID)throws WebSocketException{
        var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        sendObject(command);
    }

    private void sendObject (Object obj) throws WebSocketException {
        System.out.println("send");
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(obj));
        } catch (IOException ex) {
            throw new WebSocketException(500, ex.getMessage());
        }
    }


}
