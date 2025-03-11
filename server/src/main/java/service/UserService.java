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
        String username = registerRequest.username();
        String password = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
        String email = registerRequest.email();

        System.out.println("here goes");
        if (registerRequest.password() == null){
            throw new CredentialsException("no authToken");
        }
        System.out.println("no probs");

        try {
            System.out.println("attempt creation");
            UserData newUser = new UserData(username, password, email);
            userAccess.createUser(newUser);
            System.out.println("no problems");

            AuthData newAuth = new AuthData(username, UUID.randomUUID().toString());
            authAccess.createAuth(newAuth);

            return new RegisterResult(username, newAuth.authToken());
        } catch (DataAccessException e){
            System.out.println(e.toString());
            throw new CredentialsException("username taken");
        }

    }

    public LoginResult login(LoginRequest loginRequest) throws CredentialsException{
        String username = loginRequest.username();
        String password = loginRequest.password();

        try {
            UserData existingUser = userAccess.getUser(username);

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

