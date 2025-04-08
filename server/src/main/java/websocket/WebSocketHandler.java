package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import websocket.messages.Action;
import websocket.messages.Notification;
import websocket.messages.Action;

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

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Action action = new Gson().fromJson(message, Action.class);
        switch (action.type()) {
            case RESIGN -> resign(action.username());
            case LEAVE -> leave(action.username());
            case MOVE -> move(action.username(), action.move());
        }
    }

    private void resign(String username) throws IOException {
        var message = String.format("%s resigned", username);
        var notification = new Notification(Notification.Type.RESIGN, message);
        connections.broadcast(username, notification);
    }

    private void leave(String username) throws IOException {
        var message = String.format("%s left", username);
        var notification = new Notification(Notification.Type.RESIGN, message);
        connections.broadcast(username, notification);
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