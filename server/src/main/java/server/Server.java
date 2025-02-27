package server;
import dataaccess.*;
import service.*;
import spark.*;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import service.*;

public class Server {

    private UserService userService;
    private ClearService clearService;
    private GameService gameService;


    public int run(int desiredPort) {

        UserDAO userAccess = new MemoryUserDAO();
        AuthDAO authAccess = new MemoryAuthDAO();
        GameDAO gameAccess = new MemoryGameDAO();
        this.userService = new UserService(userAccess, authAccess, gameAccess);
        this.clearService = new ClearService(userAccess, authAccess, gameAccess);
        this.gameService = new GameService(userAccess, authAccess, gameAccess);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Gson gson = new Gson();

        // Register your endpoints and handle exceptions here.

        Spark.post("/user", (req, res) -> {
            res.type("application/json");

            RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);

            try {
                RegisterResult result = userService.register(request);

                String username = result.username();
                String authToken = result.authToken();

                res.status(200);
                return String.format("{\"username\": \"%s\", \"authToken\": \"%s\"}", username, authToken);

            }  catch (CredentialsException e){
                if (e.toString().contains("no authToken")){
                    res.status(400);
                    return "{ \"message\": \"Error: bad request\" }";
                }
                else if (e.toString().contains("username taken")){
                    res.status(403);
                    return "{ \"message\": \"Error: already taken\" }";
                }
            }

            res.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        });

        Spark.post("/session", (req, res) -> {
            res.type("application/json");

            LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);

            try {
                LoginResult result = userService.login(request);


                String username = result.username();
                String authToken = result.authToken();

                res.status(200);
                return String.format("{\"username\": \"%s\", \"authToken\": \"%s\"}", username, authToken);

            }  catch (CredentialsException e){
                if (e.toString().contains("Incorrect Password") || e.toString().contains("no account")){
                    res.status(401);
                    return "{ \"message\": \"Error: unauthorized\" }";

                }
            }
            res.status(500);
            return "{ \"message\": \"Error: bad request\" }";
        });

        Spark.delete("/session", (req, res) -> {
            res.type("application/json");

            String authToken = req.headers("Authorization");

            AuthData data = authAccess.getAuth(authToken);

            if (data == null) {
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }


            userService.logout(new LogoutRequest(authToken));

            res.status(200);
            return "{}";


        });

        Spark.get("/game", (req, res) -> {
            String authToken = req.headers("Authorization");

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
                return outString.toString();
            }
            catch (CredentialsException e){
                res.status(401);
                return "{ \"message\": \"Error: unauthorized\" }";
            }
        });

        Spark.post("/game", (req, res) -> {
            res.type("application/json");

            String authToken = req.headers("Authorization");

            JsonObject jsonObject = gson.fromJson(req.body(), JsonObject.class);
            String gameName = jsonObject.get("gameName").getAsString();

            System.out.println(jsonObject);
            System.out.println(gameName);
            System.out.println(authToken);



            MakeGameRequest request = new MakeGameRequest(authToken, gameName);

            try {

                MakeGameResult result = gameService.makeGame(request);

                int id = result.gameID();
                res.status(200);
                return String.format("{\"gameID\": \"%d\"}", id);

            }  catch (CredentialsException e){
                if (e.toString().contains("Not an authorized user")){
                    res.status(401);
                    return "{ \"message\": \"Error: unauthorized\" }";

                }
            }
            res.status(500);
            return "{ \"message\": \"Error: bad request\" }";

        });

        Spark.put("/game", (req, res) -> {
            res.type("application/json");

            String authToken = req.headers("Authorization");

            JoinGameRequest prelimRequest = gson.fromJson(req.body(), JoinGameRequest.class);

            JoinGameRequest request = new JoinGameRequest(authToken, prelimRequest.playerColor(), prelimRequest.gameID());

            try {
                gameService.joinGame(request);
                res.status(200);
                return "{}";
            } catch (CredentialsException e){
                System.out.println(e);
                if (e.toString().contains("Not an authorized user")){
                    res.status(401);
                    return "{\"message\": \"Error: unauthorized\"}";
                } else{
                    res.status(500);
                    return "{\"message\": \"Error: \"}";
                }
            } catch (ChessMatchException e){
                System.out.println(e);
                if (e.toString().contains("Null team") || e.toString().contains("team color") || e.toString().contains("Game with that ID doesnt exist")){
                    res.status(400);
                    return "{\"message\": \"Error: bad request\"}";
                } else if (e.toString().contains("taken")){
                    res.status(403);
                    return "{\"message\": \"Error: already taken\"}";
                }else{
                    res.status(500);
                    return "{\"message\": \"Error: \"}";
                }
            }
        });

        Spark.delete("/db", (req, res) -> {
            res.type("application/json");

            ClearRequest request = gson.fromJson(req.body(), ClearRequest.class);

            ClearResult result =  clearService.clear(request);
            res.status(200);
            return "{}";
        });

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
