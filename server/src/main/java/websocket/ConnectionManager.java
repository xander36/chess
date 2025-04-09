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

    public void add(String username, Session session) {
        var connection = new Connection(username, session);
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
                System.out.println(c.username);
                System.out.println(" vs ");
                System.out.println(sendUsername);
                if (c.username.equals(sendUsername)) {
                    System.out.println("how do you spell esgeti");
                    c.send(gson.toJson(msg));
                }
            }
        }
    }

    public void broadcastMessageToGame(String exceptUsername, int gameID, ServerMessage msg) throws IOException{
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.username.equals(exceptUsername)) {
                    System.out.println("Even if he aint in game " + gameID);
                    c.send(gson.toJson(msg));
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

}
