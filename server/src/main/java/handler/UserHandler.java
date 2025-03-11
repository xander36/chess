package handler;

import dataaccess.*;
import server.*;
import service.*;
import spark.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class UserHandler {

    Gson gson = new Gson();

    UserService userService;
    ClearService clearService;
    GameService gameService;


    public UserHandler(UserService userService, ClearService clearService, GameService gameService){
        this.userService = userService;
        this.clearService = clearService;
        this.gameService = gameService;
    }

    public String register(Request req, Response res){

        res.type("application/json");

        RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);

        try {
            RegisterResult result = userService.register(request);

            String username = result.username();
            String authToken = result.authToken();

            res.status(200);
            return String.format("{\"username\": \"%s\", \"authToken\": \"%s\"}", username, authToken);

        }  catch (CredentialsException e){
            System.out.println(e.toString());
            if (e.toString().contains("no authToken")){
                res.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            }
            else if (e.toString().contains("username taken") ){
                res.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            }
        }

        res.status(400);
        return "{ \"message\": \"Error: bad request\" }";

    }

    public String login(Request req, Response res) {
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
    }

    public String logout(Request req, Response res){
        res.type("application/json");

        String authToken = req.headers("Authorization");

        try {
            userService.logout(new LogoutRequest(authToken));
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        }
    }


}
