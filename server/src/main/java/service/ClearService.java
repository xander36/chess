package service;

import dataaccess.*;
import request.ClearRequest;
import result.ClearResult;

public class ClearService {

    private UserDAO userAccess;
    private AuthDAO authAccess;
    private GameDAO gameAccess;


    public ClearService(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        //A service class needs access classes to interact with all the data
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public ClearResult clear(ClearRequest registerRequest){
        //This service lass simpley clears out all the data
        userAccess.clear();
        authAccess.clear();
        gameAccess.clear();
        return new ClearResult();
    }
}

