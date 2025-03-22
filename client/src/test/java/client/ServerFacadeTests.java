package client;

import org.junit.jupiter.api.*;
import request.*;
import result.*;
import server.Server;
import facade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }


    private String quickRegister(){
        RegisterRequest request = new RegisterRequest("me", "you lol", "me@gmail.com");
        try {
            RegisterResult result = serverFacade.register(request);
            return result.authToken();
        } catch (Exception e){
            throw new RuntimeException("Somehow you screwed up the reigistartion process. nice.");
        }
    }
    
    private int quickStartGame(String authToken){
        MakeGameRequest request = new MakeGameRequest(authToken, "chess2");
        try {
            MakeGameResult result = serverFacade.makeGame(request);
            return result.gameID();
        } catch (Exception e){
            throw new RuntimeException("Somehow you screwed up the start of a game. nice.");
        }
    }

    @Test
    @Order(1)
    @DisplayName("UserserverFacade: register success test")
    public void registerSuccess(){

        //Attempt to register "user"
        RegisterRequest request = new RegisterRequest("user", "password", "address@gmail.com");
        try {
            serverFacade.register(request);
            try{
                //Use the DAO to see if the user was added
                UserData user = userAccess.getUser("user");
                Assertions.assertEquals("user", user.username());
                Assertions.assertTrue(BCrypt.checkpw("password", user.password()));
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
    @DisplayName("UserserverFacade: register failure test")
    public void registerFailure(){
        reset();

        //Register a user
        RegisterRequest request = new RegisterRequest("me", "you lol", "me@gmail.com");
        try {
            System.out.println("try register");
            serverFacade.register(request);
        } catch (Exception e){
            //In this test it is assumed that registering to a blank database will always work, so this block is blank
        }

        //Try to re-register the same user again. If all is correct, it should throw an error
        //If no error is thrown, fail an assertion
        RegisterRequest request2 = new RegisterRequest("me", "you lol", "me@gmail.com");

        try {
            serverFacade.register(request2);
            //If the above line ran succesfully, the serverFacade class failed to stop a repeat user
            Assertions.fail();
        } catch (Exception e){
            //If an exception is thrown, that means that
        }
    }

    @Test
    @Order(3)
    @DisplayName("serverFacade: login success test")
    public void loginSuccess(){
        //Register the test user: "me"
        quickRegister();

        //Attempt to log in as "me"
        LoginRequest request = new LoginRequest("me", "you lol");
        try {
            LoginResult result = serverFacade.login(request);
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
    @DisplayName("UserserverFacade: login failure test")
    public void loginFailure(){
        //Register the test user: "me"
        quickRegister();

        //Attempt to log in as "me" with the wrong password
        LoginRequest request = new LoginRequest("me", "wrong password");
        try {
            serverFacade.login(request);
            //If the login process doesnt throw an error, fail an assertion
            //You gave it the wrong password and it let you off scott-free!
            Assertions.fail();

        } catch (Exception e){
            //Good, it caught the wrong password
        }
    }

    @Test
    @Order(5)
    @DisplayName("UserserverFacade: logout success test")
    public void logoutSuccess(){
        //Register the test user: "me" and hold onto its authtoken output
        String userAuthToken = quickRegister();

        //Attempt to log out of user
        LogoutRequest request = new LogoutRequest(userAuthToken);

        try {
            serverFacade.logout(request);
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
    @DisplayName("UserserverFacade: logout failure test")
    public void logoutFailure(){
        //Register the test user: "me" and hold onto its authtoken output
        String userAuthToken = quickRegister();


        //Attempt to log out using a blatantly incorrect authtoken
        String madeUpAuthToken = "splorkenstein";
        LogoutRequest request = new LogoutRequest(madeUpAuthToken);

        try {
            serverFacade.logout(request);
            //If the logout process doesnt throw an error, fail an assertion,
            //since thr program clearly believes that "splorkenstein" is a valid authToken
            Assertions.fail();
        } catch (Exception e){
            //If no exceptions are thrown, pass the test
        }
    }

    @Test
    @Order(7)
    @DisplayName("GameserverFacade: list games success test")
    public void listGamesSuccess(){
        String userAuthToken = quickRegister();
        int newGameID = quickStartGame(userAuthToken);

        try{
            ListRequest request = new ListRequest(userAuthToken);
            ListResult result = serverFacade.listGames(request);

            Assertions.assertEquals("[{\"gameID\": 1, " +
                    "\"whiteUsername\": null, " +
                    "\"blackUsername\": null, " +
                    "\"gameName\": \"chess2\"}]", result.games().toString());

        } catch (Exception e){
            //If listing the games throws an error fail the test by failing an assertion
            Assertions.fail();
        }


    }

    @Test
    @Order(8)
    @DisplayName("GameserverFacade: list games failure test")
    public void listGamesFailure(){
        //Register the test user: "me" and hold onto its authtoken output
        String userAuthToken = quickRegister();


        //Attempt to make a game using a blatantly incorrect authtoken
        String madeUpAuthToken = "splorkenstein";
        ListRequest request = new ListRequest(madeUpAuthToken);

        try {
            serverFacade.listGames(request);
            //If making a game without authorization doesnt throw an error, fail an assertion,
            //since thr program clearly believes that "splorkenstein" is a valid authToken
            Assertions.fail();
        } catch (Exception e){
            //If no exceptions are thrown, pass the test
        }
    }

    @Test
    @Order(9)
    @DisplayName("GameserverFacade: make game success test")
    public void makeGameSuccess(){
        String userAuthToken = quickRegister();

        try{
            MakeGameRequest request = new MakeGameRequest(userAuthToken, "a chess game");
            MakeGameResult result = serverFacade.makeGame(request);

            GameData game = gameAccess.getGame(result.gameID());

            Assertions.assertEquals(result.gameID(), game.gameID());
            Assertions.assertEquals("a chess game", game.gameName());
            Assertions.assertEquals(null, game.whiteUsername());
            Assertions.assertEquals(null, game.blackUsername());

        } catch (Exception e){
            //If making a game throws an error fail the test by failing an assertion
            Assertions.fail();
        }
    }

    @Test
    @Order(10)
    @DisplayName("GameserverFacade: make game failure test")
    public void makeGameFailure(){
        //Register the test user: "me" and hold onto its authtoken output
        String userAuthToken = quickRegister();


        //Attempt to make a game using a blatantly incorrect authtoken
        String madeUpAuthToken = "splorkenstein";
        MakeGameRequest request = new MakeGameRequest(madeUpAuthToken, "fun game");

        try {
            serverFacade.makeGame(request);
            //If making a game without authorization doesnt throw an error, fail an assertion,
            //since thr program clearly believes that "splorkenstein" is a valid authToken
            Assertions.fail();
        } catch (Exception e){
            //If no exceptions are thrown, pass the test
        }
    }

    @Test
    @Order(11)
    @DisplayName("GameserverFacade: join game success test")
    public void joinGameSuccess(){
        String userAuthToken = quickRegister();
        int newGameID = quickStartGame(userAuthToken);

        try{
            JoinGameRequest request = new JoinGameRequest(userAuthToken, "WHITE", newGameID);
            serverFacade.joinGame(request);

            GameData game = gameAccess.getGame(newGameID);
            //Make sure that the new white player is "me"
            Assertions.assertEquals("me", game.whiteUsername());

        } catch (Exception e){
            //If joining a game throws an error fail the test by failing an assertion
            Assertions.fail();
        }
    }

    @Test
    @Order(12)
    @DisplayName("GameserverFacade: join game failure test")
    public void joinGameFailure(){
        //Register the test user: "me" and hold onto its authtoken output
        String userAuthToken = quickRegister();
        int newGameID = quickStartGame(userAuthToken);


        //Attempt to join a game using a blatantly incorrect authtoken
        String madeUpAuthToken = "splorkenstein";
        JoinGameRequest request = new JoinGameRequest(madeUpAuthToken, "WHITE", newGameID);

        try {
            serverFacade.joinGame(request);
            //If joining a game without authorization doesnt throw an error, fail an assertion,
            //since thr program clearly believes that "splorkenstein" is a valid authToken
            Assertions.fail();
        } catch (Exception e){
            //If no exceptions are thrown, pass the test
        }
    }

    @Test
    @Order(13)
    @DisplayName("ClearserverFacade: clear test")
    public void clearserverFacadePositive(){
        String userAuthToken = quickRegister();

        ClearRequest request = new ClearRequest();
        serverFacade.clear(request);

        try {
            userAccess.getUser("me");
            //If the user data for "me" is still there, the clear failed
            Assertions.fail();
        } catch (Exception e){}
        try {
            authAccess.getAuth(userAuthToken);
            //If the authoriztion of "me"s login is still there, the clear failed
            Assertions.fail();
        }catch (Exception e){}


    }

}
