package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import request.JoinGameRequest;
import request.ListRequest;
import request.MakeGameRequest;
import result.ListResult;
import result.MakeGameResult;
import server.ChessMatchException;
import service.*;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

public class GameHandler {

    Gson gson = new Gson();

    UserService userService;
    ClearService clearService;
    GameService gameService;


    public GameHandler(UserService userService, ClearService clearService, GameService gameService){
        this.userService = userService;
        this.clearService = clearService;
        this.gameService = gameService;
    }

    public String listGames(Request req, Response res) {
        System.out.println("I want a list");
        String authToken = req.headers("Authorization");

        System.out.println("List authorized by " + authToken);
        ListRequest request = new ListRequest(authToken);

        try {
            ListResult result = gameService.listGames(request);
            ArrayList<String> list = result.games();



            StringBuilder outString = new StringBuilder();
            outString.append("{ \"games\": [");
            String gamesString = String.join(",", list);
            outString.append(gamesString);
            outString.append("]}");

            res.status(200);
            System.out.println(outString.toString());
            return outString.toString();
        }
        catch (DataAccessException e){
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }

    }

    public String makeGame(Request req, Response res) {
        System.out.println("make a game?");
        res.type("application/json");

        String authToken = req.headers("Authorization");
        System.out.println("handler claims to be authorized by the request's authToken " + authToken);
        JsonObject jsonObject = gson.fromJson(req.body(), JsonObject.class);
        String gameName = jsonObject.get("gameName").getAsString();

        MakeGameRequest request = new MakeGameRequest(authToken, gameName);


        try {
            MakeGameResult result = gameService.makeGame(request);

            int id = result.gameID();
            res.status(200);
            return String.format("{\"gameID\": \"%d\"}", id);

        }catch (DataAccessException e){
            if (e.toString().contains("Invalid Authorization") || e.toString().contains("No matching auth")){
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }
        }
        res.status(500);
        return "{ \"message\": \"Error: bad request\" }";


    }

    public String joinGame(Request req, Response res) {
        res.type("application/json");

        String authToken = req.headers("Authorization");

        JoinGameRequest prelimRequest = gson.fromJson(req.body(), JoinGameRequest.class);

        JoinGameRequest request = new JoinGameRequest(authToken, prelimRequest.playerColor(), prelimRequest.gameID());

        try {
            gameService.joinGame(request);
            res.status(200);
            return "{}";
        } catch (DataAccessException e){
            if (e.toString().contains("Invalid Authorization") || e.toString().contains("No matching auth")){
                res.status(401);
                return "{\"message\": \"Error: unauthorized\"}";
            }
        } catch (ChessMatchException e){
            if (e.toString().contains("Null team") || e.toString().contains("team color") || e.toString().contains("Game with that ID doesnt exist")){
                res.status(400);
                return "{\"message\": \"Error: bad request\"}";
            } else if (e.toString().contains("taken")){
                res.status(403);
                return "{\"message\": \"Error: already taken\"}";
            }
        }
        res.status(500);
        return "{\"message\": \"Error: \"}";

    }



}
