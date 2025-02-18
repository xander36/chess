package server;

import dataaccess.*;

import java.util.UUID;

public class UserService {

    private UserDAO userAccess;
    private AuthDAO authAccess;

    public UserService(){
        userAccess = new MemoryUserDAO();
        authAccess = new MemoryAuthDAO();

    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException{
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        UserData existingUser = userAccess.getUser(username);


        if (existingUser == null){
            throw new DataAccessException("username taken");
            //other stauses?
        }

        UserData newUser = new UserData(username, password, email);
        userAccess.createUser(newUser);

        AuthData newAuth = new AuthData(username, UUID.randomUUID().toString());
        authAccess.createAuth(newAuth);

        return new RegisterResult(username, newAuth.authToken());
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
        String username = loginRequest.username();
        String password = loginRequest.password();

        UserData existingUser = userAccess.getUser(username);

        if (!existingUser.password().equals(password)){
            //Error
            throw new DataAccessException("incorrect password");
        }

        AuthData newAuth = new AuthData(username, UUID.randomUUID().toString());
        authAccess.createAuth(newAuth);

        return new LoginResult(username, password);
    }
    public void logout(LogoutRequest logoutRequest) {

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

record LogoutRequest(){

}