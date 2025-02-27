package service;

import dataaccess.*;
import server.ChessMatchException;
import server.CredentialsException;

import java.util.ArrayList;

public class GameService {

    private UserDAO userAccess;
    private AuthDAO authAccess;
    private GameDAO gameAccess;

    public GameService(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public ListResult listGames(ListRequest listRequest) throws CredentialsException {
        String authToken = listRequest.authToken();

        if (authAccess.getAuth(authToken) == null) {
            throw new CredentialsException("Not an authorized user");
        }

        return new ListResult(gameAccess.listGames(authToken));
    }

    public MakeGameResult makeGame(MakeGameRequest request) throws CredentialsException, DataAccessException{
        String authToken = request.authToken();

        if (authAccess.getAuth(authToken) == null) {
            throw new CredentialsException("Not an authorized user");
        }

        int newGameId = gameAccess.makeGame(request.gameName());

        return new MakeGameResult(newGameId);
    }

    public void joinGame(JoinGameRequest request) throws CredentialsException, ChessMatchException {
        String authToken = request.authToken();

        if (authAccess.getAuth(authToken) == null) {
            throw new CredentialsException("Not an authorized user");
        }

        AuthData auth = authAccess.getAuth(authToken);
        String username = auth.username();

        int gameID = request.gameID();


        try {
            GameData game = gameAccess.getGame(gameID);

            GameData newGame;
            if (request.playerColor() == null) {
                throw new ChessMatchException("Null team");
            }

            if (request.playerColor().equals("WHITE")) {
                if (game.whiteUsername() == null) {
                    newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
                } else {
                    throw new ChessMatchException("white user spot taken");
                }
            } else if (request.playerColor().equals("BLACK")) {
                if (game.blackUsername() == null) {
                    newGame = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
                } else {
                    throw new ChessMatchException("black user spot taken");
                }
            } else {
                throw new ChessMatchException("invalid team color");
            }

            gameAccess.updateGame(gameID, newGame);
        } catch(DataAccessException e){
            throw new ChessMatchException("Game with that ID doesnt exist");
        }
    }
}


