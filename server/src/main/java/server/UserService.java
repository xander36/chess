package server;

import dataaccess.*;

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

        UserData existingUser = userAccess.getUser(username);

        if (existingUser != null){
            throw new CredentialsException("username taken");
            //other stauses?
        }

        UserData newUser = new UserData(username, password, email);
        userAccess.createUser(newUser);

        AuthData newAuth = new AuthData(username, UUID.randomUUID().toString());
        authAccess.createAuth(newAuth);

        return new RegisterResult(username, newAuth.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws CredentialsException, DataAccessException{
        String username = loginRequest.username();
        String password = loginRequest.password();

        UserData existingUser = userAccess.getUser(username);

        if (existingUser == null){
            throw new CredentialsException("no account");
            //other stauses?
        }

        if (!existingUser.password().equals(password)){
            //Error
            throw new CredentialsException("Incorrect Password");
        }

        AuthData newAuth = new AuthData(username, UUID.randomUUID().toString());
        authAccess.createAuth(newAuth);

        return new LoginResult(newAuth.username(), newAuth.authToken());
    }

    public void logout(LogoutRequest logoutRequest) throws CredentialsException{
        String authToken = logoutRequest.authToken();

        AuthData authorization = authAccess.getAuth(authToken);

        authAccess.deleteAuth(authorization);
    }
}

record RegisterRequest(String username,
                       String password,
                       String email){

}

record RegisterResult(String username,
                      String authToken){

}

record LoginRequest(String username,
                    String password){

}

record LoginResult(String username,
                   String authToken){

}

record LogoutRequest(String authToken){

}