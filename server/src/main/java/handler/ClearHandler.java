package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;

public class ClearHandler {

    Gson gson = new Gson();

    UserService userService;
    ClearService clearService;
    GameService gameService;


    public ClearHandler(UserService userService, ClearService clearService, GameService gameService){
        this.userService = userService;
        this.clearService = clearService;
        this.gameService = gameService;
    }

    public String clear(Request req, Response res) {
        res.type("application/json");

        ClearRequest request = gson.fromJson(req.body(), ClearRequest.class);

        ClearResult result =  clearService.clear(request);
        res.status(200);
        return "{}";
    }
}
