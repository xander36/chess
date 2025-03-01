package service;

import dataaccess.*;
import server.CredentialsException;

import java.util.UUID;

public class UserService {

    private UserDAO userAccess;
    private AuthDAO authAccess;
    private GameDAO gameAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws CredentialsException, DataAccessException{
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if (password == null){
            throw new CredentialsException("no authToken");
        }

        try {
            UserData existingUser = userAccess.getUser(username);

            throw new CredentialsException("username taken");

        }
        catch (DataAccessException e){

            UserData newUser = new UserData(username, password, email);
            userAccess.createUser(newUser);

            AuthData newAuth = new AuthData(username, UUID.randomUUID().toString());
            authAccess.createAuth(newAuth);

            return new RegisterResult(username, newAuth.authToken());
        }


    }

    public LoginResult login(LoginRequest loginRequest) throws CredentialsException{
        String username = loginRequest.username();
        String password = loginRequest.password();

        try {
            UserData existingUser = userAccess.getUser(username);

            if (!existingUser.password().equals(password)){
                throw new CredentialsException("Incorrect Password");
            }

        } catch (DataAccessException e) {
            throw new CredentialsException("no account");
        }

        AuthData newAuth = new AuthData(username, UUID.randomUUID().toString());
        authAccess.createAuth(newAuth);

        return new LoginResult(newAuth.username(), newAuth.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException{
        String authToken = logoutRequest.authToken();

        AuthData authorization = authAccess.getAuth(authToken);

        authAccess.deleteAuth(authorization);
    }
}

