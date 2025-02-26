package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.GameData;
import dataaccess.UserDAO;

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

    public ListResult listGames(ListRequest listRequest){
        String authToken = listRequest.authToken();;
        return new ListResult(gameAccess.listGames(authToken));
    }
}

record ListRequest(String authToken){

}

record ListResult(ArrayList<String> games){

}

record MakeGameRequest(String authToken){

}

record MakeGameResult(int gameID){

}
