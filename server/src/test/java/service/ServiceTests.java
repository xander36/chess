package java.service;

import dataaccess.*;
import org.junit.jupiter.api.*;

import service.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    UserDAO userAccess;
    AuthDAO authAccess;
    GameDAO gameAccess;
    UserService userService;
    ClearService clearService;
    GameService gameService;

    //Utility functions reset and quickRegister

    public void reset(){
        //Reset the database and service classes
        userAccess = new MemoryUserDAO();
        authAccess = new MemoryAuthDAO();
        gameAccess = new MemoryGameDAO();
        userService = new UserService(userAccess, authAccess, gameAccess);
        clearService = new ClearService(userAccess, authAccess, gameAccess);
        gameService = new GameService(userAccess, authAccess, gameAccess);
    }

    private String quickRegister(){
        RegisterRequest request = new RegisterRequest("me", "you lol", "me@gmail.com");
        RegisterResult result = null;
        try {
            result = userService.register(request);
            return result.authToken();
        } catch (Exception e){
            throw new RuntimeException("Somehow you screwed up the reigistartion process. nice.");
        }
    }

    @Test
    @Order(1)
    @DisplayName("UserService: register success test")
    public void registerSuccess(){
        reset();

        //Attempt to register "user"
        RegisterRequest request = new RegisterRequest("user", "password", "address@gmail.com");
        RegisterResult result = null;
        try {
            userService.register(request);
            try{
                //Use the DAO to see if the user was added
                UserData user = userAccess.getUser("user");
                Assertions.assertEquals("user", user.username());
                Assertions.assertEquals("password", user.password());
                Assertions.assertEquals("address@gmail.com", user.email());
            } catch (DataAccessException e) {
                //If for some reason accessing the DAO gives an error, fail an assertion
                Assertions.fail();
            }
        } catch (Exception e){
            //If the registration process throws an error, fail an assertion
            Assertions.fail();
        }
    }

    @Test
    @Order(2)
    @DisplayName("UserService: register failure test")
    public void registerFailure(){
        reset();

        //Register a user
        RegisterRequest request = new RegisterRequest("me", "you lol", "me@gmail.com");
        RegisterResult result = null;
        try {
            result = userService.register(request);
        } catch (Exception e){
            //In this test it is assumed that registering to a blank database will always work, so this block is blank
        }

        //Try to re-register the same user again. If all is correct, it should throw an error
        //If no error is thrown, fail an assertion
        RegisterRequest request2 = new RegisterRequest("me", "you lol", "me@gmail.com");
        RegisterResult result2 = null;
        try {
            result2 = userService.register(request);
            //If the above line ran succesfully, the service class failed to stop a repeat user
            Assertions.fail();
        } catch (Exception e){
            //If an exception is thrown, that means that
        }
    }

    @Test
    @Order(3)
    @DisplayName("UserService: login success test")
    public void loginSuccess(){
        //Register the test user: "me"
        reset();
        quickRegister();

        //Attempt to log in as "me"
        LoginRequest request = new LoginRequest("me", "you lol");
        LoginResult result = null;
        try {
            result = userService.login(request);
            try{
                //Use the DAO to see if the user was added
                AuthData data = authAccess.getAuth(result.authToken());
                Assertions.assertEquals("me", data.username());
                Assertions.assertEquals(result.authToken(), data.authToken());

            } catch (DataAccessException e) {
                //If for some reason accessing the DAO gives an error, fail an assertion
                Assertions.fail();
            }
        } catch (Exception e){
            //If the login process throws an error, fail an assertion
            Assertions.fail();
        }
    }

    @Test
    @Order(4)
    @DisplayName("UserService: login failure test")
    public void loginFailure(){
        //Register the test user: "me"
        reset();
        quickRegister();

        //Attempt to log in as "me" with the wrong password
        LoginRequest request = new LoginRequest("me", "wrong password");
        LoginResult result = null;
        try {
            result = userService.login(request);
            //If the login process doesnt throw an error, fail an assertion
            //You gave it the wrong password and it let you off scott-free!
            Assertions.fail();

        } catch (Exception e){
            //Good, it caught the wrong password
        }
    }

    @Test
    @Order(5)
    @DisplayName("UserService: logout success test")
    public void logoutSuccess(){
        //Register the test user: "me" and hold onto its authtoken output
        reset();
        String userAuthToken = quickRegister();

        //Attempt to log out of user
        LogoutRequest request = new LogoutRequest(userAuthToken);

        try {
            userService.logout(request);
            try{
                //Use the DAO to see if the user has been removed from the list of authorzations
                AuthData data = authAccess.getAuth(userAuthToken);
                //If for some reason the DAO can still find that its logged in, fail an assertion
                Assertions.fail();

            } catch (DataAccessException e) {
                //if the logout process throws no errors, you should avoid all the failures and end up here.
            }
        } catch (Exception e){
            //If the logout process throws an error, fail an assertion
            Assertions.fail();
        }
    }

    @Test
    @Order(6)
    @DisplayName("UserService: logout failure test")
    public void logoutFailure(){
        //Register the test user: "me" and hold onto its authtoken output
        reset();
        String userAuthToken = quickRegister();


        //Attempt to log out using a blatantly incorrect authtoken
        String madeUpAuthToken = "splorkenstein";
        LogoutRequest request = new LogoutRequest(madeUpAuthToken);

        try {
            userService.logout(request);
            //If the logout process doesnt throw an error, fail an assertion,
            //since thr program clearly believes that "splorkenstein" is a valid authToken
            Assertions.fail();
        } catch (Exception e){
            //If no exceptions are thrown, pass the test
        }
    }

    @Test
    @Order(7)
    @DisplayName("GameService: list games success test")
    public void listGamesSuccess(){
        Assertions.fail();
    }

    @Test
    @Order(8)
    @DisplayName("GameService: list games failure test")
    public void listGamesFailure(){
        Assertions.fail();
    }

    @Test
    @Order(9)
    @DisplayName("GameService: make game success test")
    public void makeGameSuccess(){
        Assertions.fail();
    }

    @Test
    @Order(10)
    @DisplayName("GameService: make game failure test")
    public void makeGameFailure(){
        Assertions.fail();
    }

    @Test
    @Order(11)
    @DisplayName("GameService: join game success test")
    public void joinGameSuccess(){
        Assertions.fail();
    }

    @Test
    @Order(12)
    @DisplayName("GameService: join game failure test")
    public void joinGameFailure(){
        Assertions.fail();
    }

    @Test
    @Order(13)
    @DisplayName("ClearService: clear test")
    public void clearServicePositive(){
        reset();

        RegisterRequest request = new RegisterRequest("me", "you lol", "me@gmail.com");
        RegisterResult result = null;
        try {
            result = userService.register(request);
        } catch (Exception e){
            Assertions.fail();
        }

    }

}
