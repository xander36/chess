package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import service.ClearService;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.Action;
import websocket.messages.Notification;

import java.io.IOException;
import java.util.Timer;



@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;


    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand msg = new Gson().fromJson(message, UserGameCommand.class);
        switch (msg.getCommandType()) {
            case RESIGN -> resign(msg.getAuthToken(), msg.getGameID());
            case LEAVE -> leave(msg.getAuthToken(), msg.getGameID());
            //case MOVE -> move(action.username(), action.move());


        }
    }

    private void resign(String authToken, int gameID) throws IOException {
        var message = "";
        var notification = new Notification(Notification.Type.RESIGN, message);
        connections.broadcast(null, notification);
    }

    private void leave(String username, int gameID) throws IOException {
        var message = "";
        var notification = new Notification(Notification.Type.LEAVE, message);
        connections.broadcast(null, notification);
    }

    private void move(String username, ChessMove move) throws IOException {
        var message = String.format("%s %s", username, move);
        var notification = new Notification(Notification.Type.RESIGN, message);
        connections.broadcast(username, notification);
    }



    /*
    private void enter(String visitorName, Session session) throws IOException {
        connections.add(visitorName, session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new Notification(Notification.Type.ARRIVAL, message);
        connections.broadcast(visitorName, notification);
    }



    private void exit(String visitorName) throws IOException {
        connections.remove(visitorName);
        var message = String.format("%s left the shop", visitorName);
        var notification = new Notification(Notification.Type.DEPARTURE, message);
        connections.broadcast(visitorName, notification);
    }

    public void makeNoise(String petName, String sound) throws WebSocketException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new Notification(Notification.Type.NOISE, message);
            connections.broadcast("", notification);
        } catch (Exception ex) {
            throw new WebSocketException(500, ex.getMessage());
        }
    }
    */

}