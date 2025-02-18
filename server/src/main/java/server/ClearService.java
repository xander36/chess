package server;

import dataaccess.*;

import java.util.UUID;

public class ClearService {

    private UserDAO userAccess;
    private AuthDAO authAccess;
    private GameDAO gameAccess;

    public ClearService(){
        userAccess = new MemoryUserDAO();
        authAccess = new MemoryAuthDAO();
        gameAccess = new MemoryGameDAO();
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
