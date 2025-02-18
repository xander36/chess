package server;
import dataaccess.DataAccessException;
import spark.*;

import com.google.gson.Gson;

public class Server {

    private UserService userService;
    private ClearService clearService;

    public int run(int desiredPort) {
        this.userService = new UserService();
        this.clearService = new ClearService();

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
                if (e.toString().equals("Incorrect Password") || e.toString().contains("no password")){
                    res.status(400);
                    return "{ \"message\": \"Error: bad request\" }";
                }
                else if (e.toString().contains("username taken")){
                    res.status(403);
                    return "{ \"message\": \"Error: already taken\" }";
                }
            }
            /**
             * catch (DataAccessException e) {
             *                 if (e.toString().contains("Can't get nonexistent user")){
             *                     res.status(403);
             *                     return "{ \"message\": \"Error: already taken\" }";
             *                 }
             *             }
             */

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

                System.out.println("We give token");
                System.out.println(authToken);
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


            LogoutRequest request = gson.fromJson(req.body(), LogoutRequest.class);
            System.out.println("Built request:");
            System.out.println(request);

            try {
                userService.logout(request);

                res.status(200);
                return "{}";

            }  catch (CredentialsException e){
                if (e.toString().contains("bad authToken")){
                    res.status(401);
                    return "{ \"message\": \"Error: unauthorized\" }";

                }
            }
            res.status(500);
            return "{ \"message\": \"Error: bad request\" }";
        });
        Spark.get("/game", (req, res) -> {return null;});
        Spark.post("/game", (req, res) -> {return null;});
        Spark.put("/game", (req, res) -> {return null;});
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
