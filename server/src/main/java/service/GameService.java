package service;

import dataaccess.*;
import dataclasses.*;
import request.JoinGameRequest;
import request.ListRequest;
import request.MakeGameRequest;
import result.ListResult;
import result.MakeGameResult;
import server.ChessMatchException;

public class GameService {

    private UserDAO userAccess;
    private AuthDAO authAccess;
    private GameDAO gameAccess;

    public GameService(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public ListResult listGames(ListRequest listRequest) throws DataAccessException {
        System.out.println("game handler wants list");
        String authToken = listRequest.authToken();

        authAccess.getAuth(authToken);

        return new ListResult(gameAccess.listGames(authToken));
    }

    public MakeGameResult makeGame(MakeGameRequest makeGameRequest) throws DataAccessException{
        String authToken = makeGameRequest.authToken();
        authAccess.getAuth(authToken);

        int newGameId = gameAccess.makeGame(makeGameRequest.gameName());

        return new MakeGameResult(newGameId);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException, ChessMatchException{
        String authToken = joinGameRequest.authToken();

        AuthData auth = authAccess.getAuth(authToken);
        String username = auth.username();

        int gameID = joinGameRequest.gameID();

        try {
            GameData game = gameAccess.getGame(gameID);

            GameData newGame;
            if (joinGameRequest.playerColor() == null) {
                throw new ChessMatchException("Null team");
            }

            if (joinGameRequest.playerColor().equals("WHITE")) {
                if (game.whiteUsername() == null) {
                    newGame = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
                } else {
                    throw new ChessMatchException("white user spot taken");
                }
            } else if (joinGameRequest.playerColor().equals("BLACK")) {
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


