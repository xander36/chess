package websocket;


import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

import com.google.gson.Gson;

public class ConnectionManager {

    Gson gson = new Gson();

    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String username, int gameID, Session session) {
        var connection = new Connection(username, gameID, session);
        connections.put(username, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void sendMessage(String sendUsername, ServerMessage msg) throws IOException{
        System.out.println("sending message");
        System.out.println(sendUsername);
        System.out.println(msg);
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.username.equals(sendUsername)) {
                    System.out.println("how do you spell esgeti");
                    c.send(gson.toJson(msg));
                }
            }
        }
    }

    public void broadcastMessageToGame(String exceptUsername, int gameID, ServerMessage msg) throws IOException{
        System.out.println("broadcast to " + gameID);
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(exceptUsername)) {
                    if (c.gameID == gameID){
                        System.out.println("I sent it to " + c.username);
                            c.send(gson.toJson(msg));
                    }
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }

    public void broadcastToAll(String exceptUsername, ServerMessage msg) throws IOException{
        for (var c : connections.values()) {
            if (!c.username.equals(exceptUsername)) {
                c.send(gson.toJson(msg));
            }
        }
    }

}
