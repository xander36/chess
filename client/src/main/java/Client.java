import chess.ChessBoard;
import facade.ServerFacade;
import request.*;
import result.*;

import java.util.ArrayList;

public class Client {

    private ServerFacade serverFacade = new ServerFacade("splorken");

    private final String HELP_STRING = "Valid commands:\n\tregister <username> <password> <email>\n\tlogin <username> <password>\n\tquit\n\thelp";
    private String status = "LOGGED_OUT";
    private boolean running = true;

    public String eval(String inString){
        String input = inString.trim();

        if (status.equals("LOGGED_OUT")) {
            if (input.equals("help")) {
                return HELP_STRING;
            } else if (input.equals("quit")) {
                running = false;
                return "Session over. See ya!";
            } else if (input.startsWith("register")) {
                String[] parts = input.split("\n");
                if (parts.length != 4){
                    return "Invalid registration data";
                } else {
                    RegisterRequest req = new RegisterRequest(parts[1], parts[2], parts[3]);
                    RegisterResult res = serverFacade.register(req);

                    status = "LOGGED_IN";

                    return "Registered user " + res.username() + " and logged in";
                }
            } else if (input.startsWith("login")) {
                String[] parts = input.split("\n");
                if (parts.length != 3){
                    return "Invalid login data";
                } else {
                    LoginRequest req = new LoginRequest(parts[1], parts[2]);
                    LoginResult res = serverFacade.login(req);

                    status = "LOGGED_IN";

                    return "Logged in user " + res.username();
                }
            }
        } else if (status.equals("LOGGED_IN")) {
            
        }
        return "F in the chat, what did you dooooooo to meeeeee";
    }

    private String getBoard(ChessBoard board){
        return "beautiful board pic";
    }

    public String getPrompt(){
        if (!running){
            return "";
        }
        String prompt = "[";
        prompt += status;

        prompt += "] >>>";
        return prompt;
    }
}
