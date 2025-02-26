package server;

import dataaccess.*;

import java.util.UUID;

public class ClearService {

    private UserDAO userAccess;
    private AuthDAO authAccess;
    private GameDAO gameAccess;


    public ClearService(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public ClearResult clear(ClearRequest registerRequest) throws DataAccessException{
        userAccess.clear();
        authAccess.clear();
        gameAccess.clear();
        return new ClearResult();
    }
}

record ClearRequest(){

}

record ClearResult(){

}
