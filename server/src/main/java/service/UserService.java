package service;

import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;
import server.CredentialsException;

import javax.xml.crypto.Data;
import java.util.UUID;

import static org.mindrot.jbcrypt.BCrypt.gensalt;
import static org.mindrot.jbcrypt.BCrypt.hashpw;

public class UserService {

    private UserDAO userAccess;
    private AuthDAO authAccess;
    private GameDAO gameAccess;

    public UserService(UserDAO userAccess, AuthDAO authAccess, GameDAO gameAccess){
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws CredentialsException{
        System.out.println("Service wants to register");
        String username = registerRequest.username();
        String password = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
        String email = registerRequest.email();

        System.out.println(registerRequest.password());
        System.out.println("maps to");
        System.out.println(password);


        if (registerRequest.password() == null){
            throw new CredentialsException("no authToken");
        }

        try {
            UserData existingUser = userAccess.getUser(username);

            throw new CredentialsException("username taken");
        } catch (DataAccessException e){
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

            System.out.println("check plz");
            System.out.println(password);
            System.out.println(existingUser.password());
            System.out.println(BCrypt.hashpw(password, gensalt()));
            if (!BCrypt.checkpw(password, existingUser.password())) {
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

