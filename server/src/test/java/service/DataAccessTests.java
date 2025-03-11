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
        userAccess.createUser(user);
        try {
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
    public void createUserTestFail(){
        //How does this fail?
        Assertions.fail();
    }

    @Test
    @Order(1)
    public void getUserTestPass(){
        reset();

        UserData user = new UserData("mr fun", "password", "lol@aol.com");
        userAccess.createUser(user);
        try {
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
    public void getUserTestFail(){Assertions.fail();}

    @Test
    @Order(1)
    public void userClearTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void userClearTestFail(){Assertions.fail();}


    //Game
    @Test
    @Order(1)
    public void listGamesTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void listGamesTestFail(){Assertions.fail();}
    public void gameClearTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void gameClearTestFail(){Assertions.fail();}
    public void makeGameTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void makeGameTestFail(){Assertions.fail();}
    public void getGameTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void getGameTestFail(){Assertions.fail();}
    @Test
    @Order(1)
    public void updateGameTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void updateGameTestFail(){Assertions.fail();}

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
    public void authClearTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void authClearTestFail(){Assertions.fail();}
}
