package service;

import dataaccess.AuthData;
import dataaccess.DataAccessException;
import dataaccess.GameData;
import dataaccess.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessTests {
    
    //User
    @Test
    @Order(1)
    public void getUserTestPass(){

        Assertions.fail();
    }
    @Test
    @Order(1)
    public void getUserTestFail(){Assertions.fail();}
    @Test
    @Order(1)
    public void createUserTestPass(){Assertions.fail();}
    @Test
    @Order(1)
    public void createUserTestFail(){Assertions.fail();}
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
