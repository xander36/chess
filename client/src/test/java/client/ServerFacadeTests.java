package client;

import org.junit.jupiter.api.*;
import request.*;
import result.*;
import server.Server;
import facade.ServerFacade;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade("http://localhost:" + port);

        serverFacade.clear(new ClearRequest());

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
        serverFacade.clear(new ClearRequest());
        //Attempt to register "user"
        RegisterRequest request = new RegisterRequest("user", "password", "address@gmail.com");
        RegisterResult res = serverFacade.register(request);

        Assertions.assertTrue(res.authToken() != null);
    }

    @Test
    @Order(2)
    @DisplayName("serverFacade: register failure test")
    public void registerFailure(){
        serverFacade.clear(new ClearRequest());
        //Register a user
        RegisterRequest request = new RegisterRequest("me", "you lol", "me@gmail.com");
        serverFacade.register(request);

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
        serverFacade.clear(new ClearRequest());
        //Register the test user: "me"
        quickRegister();

        LoginRequest req = new LoginRequest("me", "you lol");
        LoginResult res= serverFacade.login(req);

        Assertions.assertTrue(res.authToken() != null);
    }

    @Test
    @Order(4)
    @DisplayName("serverFacade: login failure test")
    public void loginFailure(){
        serverFacade.clear(new ClearRequest());
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
    @DisplayName("serverFacade: logout success test")
    public void logoutSuccess(){
        serverFacade.clear(new ClearRequest());
        //Register the test user: "me" and hold onto its authtoken output
        String userAuthToken = quickRegister();

        //Attempt to log out of user
        LogoutRequest request = new LogoutRequest(userAuthToken);
        serverFacade.logout(request);

    }

    @Test
    @Order(6)
    @DisplayName("serverFacade: logout failure test")
    public void logoutFailure(){
        serverFacade.clear(new ClearRequest());
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
    @DisplayName("serverFacade: list games success test")
    public void listGamesSuccess(){
        serverFacade.clear(new ClearRequest());

        String userAuthToken = quickRegister();
        int newGameID = quickStartGame(userAuthToken);

        try{
            ListRequest request = new ListRequest(userAuthToken);
            ListResult result = serverFacade.listGames(request);

            Assertions.assertEquals("[gameID:1 " +
                    "whiteUsername:null " +
                    "blackUsername:null " +
                    "gameName:chess2 game:WHITE\n" +
                    "RNBQKBNR\n" +
                    "PPPPPPPP\n" +
                    "********\n" +
                    "********\n" +
                    "********\n" +
                    "********\n" +
                    "pppppppp\n" +
                    "rnbqkbnr\n]", result.games().toString());

        } catch (Exception e){
            System.out.println(e.toString());
            //If listing the games throws an error fail the test by failing an assertion
            Assertions.fail();
        }


    }

    @Test
    @Order(8)
    @DisplayName("serverFacade: list games failure test")
    public void listGamesFailure(){
        serverFacade.clear(new ClearRequest());

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
    @DisplayName("serverFacade: make game success test")
    public void makeGameSuccess(){
        serverFacade.clear(new ClearRequest());
        String userAuthToken = quickRegister();

        try{
            MakeGameRequest request = new MakeGameRequest(userAuthToken, "a chess game");
            MakeGameResult result = serverFacade.makeGame(request);


        } catch (Exception e){
            //If making a game throws an error fail the test by failing an assertion
            Assertions.fail();
        }
    }

    @Test
    @Order(10)
    @DisplayName("GameserverFacade: make game failure test")
    public void makeGameFailure(){
        serverFacade.clear(new ClearRequest());
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
    @DisplayName("serverFacade: join game success test")
    public void joinGameSuccess(){
        serverFacade.clear(new ClearRequest());
        String userAuthToken = quickRegister();
        int newGameID = quickStartGame(userAuthToken);

        try{
            JoinGameRequest request = new JoinGameRequest(userAuthToken, "WHITE", newGameID);
            serverFacade.joinGame(request);


        } catch (Exception e){
            //If joining a game throws an error fail the test by failing an assertion
            Assertions.fail();
        }
    }

    @Test
    @Order(12)
    @DisplayName("serverFacade: join game failure test")
    public void joinGameFailure(){
        serverFacade.clear(new ClearRequest());
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
    @DisplayName("serverFacade: clear test")
    public void clearserverFacadePositive(){
        serverFacade.clear(new ClearRequest());
        String userAuthToken = quickRegister();

        ClearRequest request = new ClearRequest();
        serverFacade.clear(request);

        try {
            serverFacade.login(new LoginRequest("me", "pass"));
        } catch (Exception e){}
    }

}
