package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataclasses.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import dataaccess.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.*;

import java.io.IOException;


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
        var msg = gson.fromJson(message, UserGameCommand.class);
        if (msg.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
            msg = gson.fromJson(message, MakeMoveCommand.class);
        }
        String authToken = msg.getAuthToken();
        int gameID = msg.getGameID();
        String username;
        try {
            username = authDAO.getAuth(authToken).username();
        } catch (DataAccessException e){
            var errMsg = new ErrorMessage("You found the creator's dark vision");

            session.getRemote().sendString(gson.toJson(errMsg));
            return;
        }
        switch (msg.getCommandType()) {
            case CONNECT:
                connect(username, authToken, gameID, session);
                break;
            case RESIGN:
                resign(username, gameID);
                break;
            case LEAVE:
                leave(username, gameID);
                break;
            case MAKE_MOVE:
                if (msg instanceof MakeMoveCommand moveMsg){
                    move(username, gameID, moveMsg.move);
                }
                break;
            default: connections.sendMessage(username, new NotificationMessage("Error: bad command"));
        }
    }

    private void connect(String username, String authToken, int gameID, Session sesh) throws IOException{
        connections.add(username, gameID, sesh);

        try {
            //Verify that these two exist
            var game = gameDAO.getGame(gameID);
            var auth = authDAO.getAuth(authToken);

            var message = "";
            ServerMessage notification = new LoadGameMessage(game);
            connections.sendMessage(username, notification);
            notification = new NotificationMessage(username + " joined the game!");
            connections.broadcastMessageToGame(username, gameID, notification);

        } catch (DataAccessException e) {
            if (e.toString().contains("No game with that ID")) {
                ServerMessage notification = new ErrorMessage("Bad GameID");
                connections.sendMessage(username, notification);
            } else if (e.toString().equals("jdhjdhksm")) {

            } else{
                System.out.println("smth messes up");
            }
        }
    }

    private void move(String username, int gameID, ChessMove move) throws IOException{
        try {
            var game = gameDAO.getGame(gameID);
            var board = game.game().getBoard();
            var piece = board.getPiece(move.getStartPosition());


            if (!(game.game().validMoves(move.getStartPosition()).contains(move))){
                connections.sendMessage(username, new ErrorMessage("Not a valid move"));
                return;
            }

            String whitePlayer = game.whiteUsername();
            String blackPlayer = game.blackUsername();

            if (username.equals(whitePlayer)){
                if (piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                    connections.sendMessage(username, new ErrorMessage("That is a move for the other team"));
                    return;
                }

                if (game.game().getTeamTurn() == ChessGame.TeamColor.BLACK){
                    connections.sendMessage(username, new ErrorMessage("It's currently " + blackPlayer + "'s turn"));
                    return;
                }
            } else if (username.equals(blackPlayer)){
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    connections.sendMessage(username, new ErrorMessage("That is a move for the other team"));
                    return;
                }

                if (game.game().getTeamTurn() == ChessGame.TeamColor.WHITE){
                    connections.sendMessage(username, new ErrorMessage("It's currently " + whitePlayer + "'s turn"));
                    return;
                }
            } else {
                connections.sendMessage(username, new ErrorMessage("You're not playing, silly"));
                return;
            }

            System.out.println(game.game().over);

            if(game.game().over || game.game().isInCheckmate(ChessGame.TeamColor.WHITE) || game.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
                connections.sendMessage(username, new ErrorMessage("The game is over. No further moves can be made"));
                return;
            }





            board.addPiece(move.getStartPosition(), null);

            board.addPiece(move.getEndPosition(), piece);


            game.game().setBoard(board);
            if (game.game().getTeamTurn() == ChessGame.TeamColor.WHITE){
                game.game().setTeamTurn(ChessGame.TeamColor.BLACK);
            } else if (game.game().getTeamTurn() == ChessGame.TeamColor.BLACK){
                game.game().setTeamTurn(ChessGame.TeamColor.WHITE);
            }

            gameDAO.updateGame(gameID, game);

            connections.broadcastMessageToGame(null, gameID, new LoadGameMessage(game));

            connections.broadcastMessageToGame(username, gameID, new NotificationMessage(username + " has moved: " + move));


            if (game.game().isInStalemate(ChessGame.TeamColor.WHITE) || game.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
                connections.broadcastToAll(null, new NotificationMessage(whitePlayer + " and " + blackPlayer + "have ended their game in a draw!"));
            } else if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK)){
                connections.broadcastToAll(null, new NotificationMessage(whitePlayer + " has checkmated " + blackPlayer + "!"));
            } else if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)){
                connections.broadcastToAll(null, new NotificationMessage(blackPlayer + " has checkmated " + whitePlayer + "!"));
            } else if (game.game().isInCheck(ChessGame.TeamColor.BLACK)){
                connections.broadcastToAll(blackPlayer, new NotificationMessage(whitePlayer + " has checked " + blackPlayer + "!"));
            } else if (game.game().isInCheck(ChessGame.TeamColor.WHITE)){
                connections.broadcastToAll(whitePlayer, new NotificationMessage(blackPlayer + " has checked " + whitePlayer + "!"));
            }


        } catch (DataAccessException e){
            throw new RuntimeException(String.format("Lol some of the database blocks cracked and %s oozed out", e.toString()));
        }
    }

    private void resign(String username,int gameID) throws IOException{
        try {
            var game = gameDAO.getGame(gameID);

            if (game.game().over){
                connections.sendMessage(username, new ErrorMessage("This game is already over, you can't resign"));
                return;
            }

            String whitePlayer = game.whiteUsername();
            String blackPlayer = game.blackUsername();

            if (!username.equals(whitePlayer) && !username.equals(blackPlayer)){
                connections.sendMessage(username, new ErrorMessage("You're not playing, silly, you can't resign"));
                return;
            }

            game.game().over = true;
            gameDAO.updateGame(gameID, game);

            if (username.equals(whitePlayer)) {
                connections.broadcastMessageToGame(null, gameID, new NotificationMessage(username +
                        " has resigned their game against " + blackPlayer));
            } else if (username.equals(blackPlayer)) {
                connections.broadcastMessageToGame(null, gameID, new NotificationMessage(username +
                        " has resigned their game against " + whitePlayer));
            }


        } catch (DataAccessException e) {
            throw new RuntimeException(String.format("Lol some of the database blocks cracked and %s oozed out", e.toString()));
        }
    }

    private void leave(String username,int gameID) throws IOException{
        System.out.println(username + " leaving");
        try {
            var game = gameDAO.getGame(gameID);

            String whitePlayer = game.whiteUsername();
            String blackPlayer = game.blackUsername();



            if (username.equals(whitePlayer)) {
                game = new GameData(gameID, null, blackPlayer, game.gameName(), game.game());
            } else if (username.equals(blackPlayer)) {
                game = new GameData(gameID, whitePlayer,null, game.gameName(), game.game());
            }

            connections.broadcastMessageToGame(username, gameID, new NotificationMessage(username +
                    " has left the game"));

            connections.remove(username);

            gameDAO.updateGame(gameID, game);


        } catch (DataAccessException e) {
            throw new RuntimeException(String.format("Lol some of the database blocks cracked and %s oozed out", e.toString()));
        }
    }

}