package service;

import dataaccess.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

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
            Assertions.assertEquals(user.password(), retrieve.username());
            Assertions.assertEquals(user.email(), retrieve.username());
        } catch (Exception e){
            Assertions.fail();
        }

    }
    @Test
    @Order(1)
    public void createUserTestFail(){
        //How does this fail?
        Assertions.fail();
    }

    @Test
    @Order(1)
    public void getUserTestPass(){
        reset();

        UserData user = new UserData("mr fun", "password", "lol@aol.com");
        try {
            userAccess.createUser(user);
            UserData retrieve = userAccess.getUser("mr fun");
            Assertions.assertEquals(user.username(), retrieve.username());
            Assertions.assertEquals(user.username(), retrieve.username());
            Assertions.assertEquals(user.username(), retrieve.username());
        } catch (Exception e){
            Assertions.fail();
        }

    }
    @Test
    @Order(1)
    public void getUserTestFail(){
        //How does this fail?
        Assertions.fail();
    }

    @Test
    @Order(1)
    public void userClearTestPass(){
        try{
            userAccess.clear();
        }catch (Exception e){
            Assertions.fail();
        }
    }



    //Game
    @Test
    @Order(1)
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
    @Order(1)
    public void makeGameTestFail(){
        //How to fail
    }

    @Test
    @Order(1)
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
    @Order(1)
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
    @Order(1)
    public void listGamesTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void listGamesTestFail(){Assertions.fail();}


    @Test
    @Order(1)
    public void updateGameTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void updateGameTestFail(){Assertions.fail();}
    @Test
    @Order(1)
    public void gameClearTestPass(){
        try{
            userAccess.clear();
        }catch (Exception e){
            Assertions.fail();
        }
    }

    //Auth
    @Test
    @Order(1)
    public void createAuthTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void createAuthTestFail(){Assertions.fail();}
    @Test
    @Order(1)
    public void getAuthTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void getAuthTestFail(){Assertions.fail();}
    @Test
    @Order(1)
    public void deleteAuthTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void deleteAuthTestFail(){Assertions.fail();}
    @Test
    @Order(1)
    public void authClearTestPass(){
        try{
            authAccess.clear();
        }catch (Exception e){
            Assertions.fail();
        }
    }
}
