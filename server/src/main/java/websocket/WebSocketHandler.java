package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.*;

import java.io.IOException;
import java.util.Timer;



@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    private Gson gson = new Gson();


    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("Got message from ");
        var msg = gson.fromJson(message, UserGameCommand.class);
        if (msg.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
            msg = gson.fromJson(message, MakeMoveCommand.class);
        }
        System.out.println(msg.getAuthToken());
        String authToken = msg.getAuthToken();
        int gameID = msg.getGameID();
        String username;
        try {
            System.out.println("we get a name for this guy");
            username = authDAO.getAuth(authToken).username();
            System.out.println("and do it respectfully");
        } catch (DataAccessException e){
            System.out.println("he's a ghost");
            var errMsg = new ErrorMessage("You found the creator's dark vision");

            session.getRemote().sendString(gson.toJson(errMsg));
            return;
        }
        switch (msg.getCommandType()) {
            case CONNECT:
                connect(username, authToken, gameID, session);
                break;
            //case RESIGN -> resign(msg.getAuthToken(), msg.getGameID());
            //case LEAVE -> leave(msg.getAuthToken(), msg.getGameID());
            case MAKE_MOVE:
                System.out.println("we move------------------------------------------");
                System.out.println(msg);
                if (msg instanceof MakeMoveCommand moveMsg){
                    System.out.println("cast works");
                    move(username, gameID, moveMsg.move);
                }
                break;
            default: connections.sendMessage(username, new NotificationMessage("Error: bad command"));
        }
    }

    private void connect(String username, String authToken, int gameID, Session sesh) throws IOException{
        System.out.println("connection");
        connections.add(username, sesh);

        try {
            //Verify that these two exist
            var game = gameDAO.getGame(gameID);
            System.out.println("lets check their auth");
            var auth = authDAO.getAuth(authToken);
            System.out.println(auth);

            var message = "";
            ServerMessage notification = new LoadGameMessage(gameID);
            connections.sendMessage(username, notification);
            notification = new NotificationMessage(username + " joined the game!");
            connections.broadcastMessageToGame(username, gameID, notification);

        } catch (DataAccessException e) {
            System.out.println("checkers in the connector tripped:");
            System.out.println(e.toString());
            if (e.toString().contains("No game with that ID")) {
                ServerMessage notification = new ErrorMessage("Bad GameID");
                connections.sendMessage(username, notification);
            } else if (e.toString().equals("jdhjdhksm")) {

            } else{
                System.out.println("smth messes up");
            }
        }
    }

    private void move(String username, int gameID, ChessMove move){
        try {
            var game = gameDAO.getGame(gameID);
            var board = game.game().getBoard();
            var piece = board.getPiece(move.getStartPosition());


            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), piece);

            game.game().setBoard(board);
            gameDAO.updateGame(gameID, game);

        } catch (DataAccessException e){
            throw new RuntimeException(String.format("Lol some of the database blocks cracked and %s oozed out", e.toString()));
        }
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