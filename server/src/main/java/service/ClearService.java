package service;

import dataaccess.*;

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

