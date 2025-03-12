package service;

import dataaccess.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.UUID;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessTests {

    UserDAO userAccess;
    AuthDAO authAccess;
    GameDAO gameAccess;

    //Utility functions reset and quickRegister

    public void reset(){
        //Reset the database
        userAccess = new DatabaseUserDAO();
        authAccess = new DatabaseAuthDAO();
        gameAccess = new DatabaseGameDAO();

        userAccess.clear();
        authAccess.clear();
        gameAccess.clear();
    }


    //User

    @Test
    @Order(1)
    public void createUserTestPass(){
        reset();

        UserData user = new UserData("mr fun", "password", "lol@aol.com");
        try {
            userAccess.createUser(user);

            UserData retrieve = userAccess.getUser("mr fun");
            Assertions.assertEquals(user.username(), retrieve.username());
            Assertions.assertEquals(user.password(), retrieve.password());
            Assertions.assertEquals(user.email(), retrieve.email());
        } catch (Exception e){
            Assertions.fail();
        }

    }
    @Test
    @Order(2)
    public void createUserTestFail(){
        reset();

        try {
            UserData user = new UserData("mr fun", "password", "lol@aol.com");
            UserData repeatUser = new UserData("mr fun", "password1", "him@aol.com");

            userAccess.createUser(user);
            userAccess.createUser(repeatUser);

            Assertions.fail();
        } catch (Exception e){
            //The second create should throw an error, and if it does this block
            //will be called and save the test from assertion failure
        }
    }

    @Test
    @Order(3)
    public void getUserTestPass(){
        reset();

        UserData user = new UserData("mr fun", "password", "lol@aol.com");
        try {
            userAccess.createUser(user);
            UserData retrieve = userAccess.getUser("mr fun");
            Assertions.assertEquals(user.username(), retrieve.username());
            Assertions.assertEquals(user.password(), retrieve.password());
            Assertions.assertEquals(user.email(), retrieve.email());
        } catch (Exception e){
            System.out.println(e.toString());
            Assertions.fail();
        }

    }
    @Test
    @Order(4)
    public void getUserTestFail(){
        reset();

        UserData user = new UserData("mr fun", "password", "lol@aol.com");
        try {
            userAccess.createUser(user);
            UserData retrieve = userAccess.getUser("mr not-fun");
            Assertions.fail();
        } catch (Exception e){
            //if the data access has a problem finding the non-existent mr not-fun
            //its doing its job right
        }

    }


    @Test
    @Order(5)
    public void userClearTestPass(){
        reset();
        try{
            userAccess.clear();
        }catch (Exception e){
            Assertions.fail();
        }
    }

    //Game
    @Test
    @Order(6)
    public void makeGameTestPass(){
        reset();
        try {
            gameAccess.makeGame("fun game");

            GameData retrieve = gameAccess.getGame(1);

            Assertions.assertEquals("fun game", retrieve.gameName());
            Assertions.assertNull(retrieve.whiteUsername());
            Assertions.assertNull(retrieve.blackUsername());
            Assertions.assertEquals(1, retrieve.gameID());

            gameAccess.makeGame("another game");

            retrieve = gameAccess.getGame(2);

            Assertions.assertEquals("another game", retrieve.gameName());
            Assertions.assertNull(retrieve.whiteUsername());
            Assertions.assertNull(retrieve.blackUsername());
            Assertions.assertEquals(2, retrieve.gameID());


        }catch(Exception e){
            Assertions.fail();
        }
    }
    @Test
    @Order(7)
    public void makeGameTestFail(){
        reset();
        try {
            gameAccess.makeGame("");
            Assertions.fail();
        }catch(Exception e){
            //Trying to make a blank-named game should throw an error
        }
    }

    @Test
    @Order(8)
    public void getGameTestPass(){
        reset();
        try {
            gameAccess.makeGame("fun game");

            GameData retrieve = gameAccess.getGame(1);

            Assertions.assertEquals("fun game", retrieve.gameName());
            Assertions.assertNull(retrieve.whiteUsername());
            Assertions.assertNull(retrieve.blackUsername());
            Assertions.assertEquals(1, retrieve.gameID());

            gameAccess.makeGame("another game");

            retrieve = gameAccess.getGame(2);

            Assertions.assertEquals("another game", retrieve.gameName());
            Assertions.assertNull(retrieve.whiteUsername());
            Assertions.assertNull(retrieve.blackUsername());
            Assertions.assertEquals(2, retrieve.gameID());
        }catch(Exception e){
            Assertions.fail();
        }
    }
    @Test
    @Order(9)
    public void getGameTestFail(){
        reset();
        try {
            gameAccess.makeGame("fun game");

            GameData retrieve = gameAccess.getGame(2);

            Assertions.fail();

        }catch(Exception e){
            //Good, an error was thrown when trying to get a noneistant game
        }
    }

    @Test
    @Order(10)
    public void listGamesTestPass(){
        reset();
        try {
            UserData user = new UserData("Alex", "strongpassword", "arc@byu.edu");
            userAccess.createUser(user);
            AuthData data = new AuthData("Alex", UUID.randomUUID().toString());
            authAccess.createAuth(data);

            gameAccess.makeGame("fun game");
            gameAccess.makeGame("another game");

            ArrayList<String> gameList  = gameAccess.listGames(data.authToken());

        }catch(Exception e){
            Assertions.fail();
        }

    }

    @Test
    @Order(11)
    public void listGamesTestFail(){
        reset();
        try {


            gameAccess.makeGame("fun game");
            gameAccess.makeGame("another game");

            ArrayList<String> gameList  = gameAccess.listGames("");

            Assertions.fail();

        }catch(Exception e){
            //Attempting to get a game list without even trying to provide an authToken should fail
        }

    }


    @Test
    @Order(12)
    public void updateGameTestPass(){
        reset();
        try {
            gameAccess.makeGame("fun game");
            GameData game = gameAccess.getGame(1);

            game = new GameData(1, game.whiteUsername(), game.blackUsername(), "fun game with a cooler name", game.game());

            gameAccess.updateGame(1, game);

            GameData retrieve = gameAccess.getGame(1);

            Assertions.assertEquals(game.gameID(), retrieve.gameID());
            Assertions.assertEquals(game.whiteUsername(), retrieve.whiteUsername());
            Assertions.assertEquals(game.blackUsername(), retrieve.blackUsername());
            Assertions.assertEquals(game.gameName(), retrieve.gameName());
            Assertions.assertEquals(game.game(), retrieve.game());

        }catch(Exception e){
            Assertions.fail();
        }
    }
    @Test
    @Order(13)
    public void updateGameTestFail(){
        reset();
        try {
            gameAccess.makeGame("fun game");
            GameData game = gameAccess.getGame(1);

            game = new GameData(1, game.whiteUsername(), game.blackUsername(), "fun game with a cooler name", game.game());

            gameAccess.updateGame(2, game);

            Assertions.fail();

        }catch(Exception e){

        }
    }
    @Test
    @Order(14)
    public void gameClearTestPass(){
        reset();
        try{
            gameAccess.clear();
        }catch (Exception e){
            Assertions.fail();
        }
    }

    //Auth
    @Test
    @Order(15)
    public void createAuthTestPass(){
        reset();

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData("mr fun", token);
        try {
            authAccess.createAuth(auth);

            AuthData retrieve = authAccess.getAuth(token);
            Assertions.assertEquals(auth.username(), retrieve.username());
            Assertions.assertEquals(auth.authToken(), retrieve.authToken());
        } catch (Exception e){
            Assertions.fail();
        }
    }
    @Test
    @Order(16)
    public void createAuthTestFail(){
        reset();

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData("mr fun", token);
        try {
            //try tp register the same auth twice
            authAccess.createAuth(auth);
            authAccess.createAuth(auth);

            Assertions.fail();
        } catch (Exception e){
            //Double registration should end in failure,
            //but thrwoing an error and catching out should pass the test
        }

    }
    @Test
    @Order(17)
    public void getAuthTestPass(){
        reset();

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData("mr fun", token);
        try {
            authAccess.createAuth(auth);

            AuthData retrieve = authAccess.getAuth(token);
            Assertions.assertEquals(auth.username(), retrieve.username());
            Assertions.assertEquals(auth.authToken(), retrieve.authToken());
        } catch (Exception e){
            Assertions.fail();
        }
    }
    @Test
    @Order(18)
    public void getAuthTestFail(){
        reset();

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData("mr fun", token);
        try {

            AuthData retrieve = authAccess.getAuth(token);

            Assertions.fail();

        } catch (Exception e){
            //The try block is supposed to fail - its trying to access an authdata that doesnt exist
            //If this catch block executes and skips the fail, the code works
        }
    }
    @Test
    @Order(19)
    public void deleteAuthTestPass(){
        reset();

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData("mr fun", token);
        try {
            authAccess.createAuth(auth);

            authAccess.deleteAuth(auth);

            try{
                authAccess.getAuth(token);
                Assertions.fail();
            } catch (DataAccessException e) {
                //If attempting to get the authdata after its been deleted throws an error,
                //The code is doing its job
            }
        } catch (Exception e){
            Assertions.fail();
        }
    }
    @Test
    @Order(20)
    public void deleteAuthTestFail(){
        reset();

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData("mr fun", token);
        try {

            authAccess.deleteAuth(auth);

            Assertions.fail();
        } catch (Exception e){
            //If attempting to delete authdata that doesnt exist throws an error
            //The code is doing its job
        }
    }
    @Test
    @Order(21)
    public void authClearTestPass(){
        reset();
        try{
            authAccess.clear();
        }catch (Exception e){
            Assertions.fail();
        }
    }
}
