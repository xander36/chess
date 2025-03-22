package server;
import dataaccess.*;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import service.*;
import spark.*;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Server {

    private UserHandler userHandler;
    private ClearHandler clearHandler;
    private GameHandler gameHandler;


    public int run(int desiredPort) {

        UserDAO userAccess = new DatabaseUserDAO();
        AuthDAO authAccess = new DatabaseAuthDAO();
        GameDAO gameAccess = new DatabaseGameDAO();
        UserService userService = new UserService(userAccess, authAccess, gameAccess);
        ClearService clearService = new ClearService(userAccess, authAccess, gameAccess);
        GameService gameService = new GameService(userAccess, authAccess, gameAccess);

        this.userHandler = new UserHandler(userService, clearService, gameService);
        this.clearHandler = new ClearHandler(userService, clearService, gameService);
        this.gameHandler = new GameHandler(userService, clearService, gameService);

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Gson gson = new Gson();

        Spark.post("/user", (req, res) -> {return userHandler.register(req, res);});

        Spark.post("/session", (req, res) -> {return userHandler.login(req, res);});

        Spark.delete("/session", (req, res) -> {return userHandler.logout(req, res);});

        Spark.get("/game", (req, res) -> {System.out.println("I am telling you I got a GET request"); return gameHandler.listGames(req, res);});

        Spark.post("/game", (req, res) -> {System.out.println("I am telling you I got a POST request"); return gameHandler.makeGame(req, res);});

        Spark.put("/game", (req, res) -> {return gameHandler.joinGame(req, res);});

        Spark.delete("/db", (req, res) -> {return clearHandler.clear(req, res);});

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
