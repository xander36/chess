package server;
import spark.*;

import

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> {
            res.type("application/json");

            UserRequest userRequest = gson.fromJson(req.body(), UserRequest.class);

            String username = "";
            String authToken = "";

            res.status(200);
            return String.format("{\"username\": \"%s\", \"authToken\": \"%s\"}", username, authToken);
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
