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
            System.out.println(e.toString());
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
        //How to fail
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
    @Order(12)
    public void updateGameTestPass(){Assertions.fail();}
    @Test
    @Order(13)
    public void updateGameTestFail(){Assertions.fail();}
    @Test
    @Order(14)
    public void gameClearTestPass(){
        try{
            userAccess.clear();
        }catch (Exception e){
            Assertions.fail();
        }
    }

    //Auth
    @Test
    @Order(15)
    public void createAuthTestPass(){Assertions.fail();}
    @Test
    @Order(16)
    public void createAuthTestFail(){Assertions.fail();}
    @Test
    @Order(17)
    public void getAuthTestPass(){Assertions.fail();}
    @Test
    @Order(18)
    public void getAuthTestFail(){Assertions.fail();}
    @Test
    @Order(19)
    public void deleteAuthTestPass(){Assertions.fail();}
    @Test
    @Order(20)
    public void deleteAuthTestFail(){Assertions.fail();}
    @Test
    @Order(21)
    public void authClearTestPass(){
        try{
            authAccess.clear();
        }catch (Exception e){
            Assertions.fail();
        }
    }
}
