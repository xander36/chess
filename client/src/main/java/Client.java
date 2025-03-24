import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import facade.ServerFacade;
import facade.ServerFacadeException;
import request.*;
import result.*;

import java.util.ArrayList;
import java.util.Objects;

public class Client {

    private ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

    private String status = "LOGGED_OUT";
    private boolean running = true;
    private String username = null;
    private String authToken = null;
    private String game = null;
    private ArrayList<String> recentGameListing = null;

    public String eval(String inString){
        String input = inString.trim();

        if (input.equals("help")) {
            return getHelpString();
        } else if (input.equals("quit")) {
            running = false;
            return "Session over. See ya!";
        } else {
            if (status.equals("LOGGED_OUT")) {
                if (input.startsWith("register")) {
                    String[] parts = input.split(" ");
                    if (parts.length != 4) {
                        return "Please provide a username, password, and email";
                    } else {
                        RegisterRequest req = new RegisterRequest(parts[1], parts[2], parts[3]);
                        try {
                            RegisterResult res = serverFacade.register(req);
                            status = "LOGGED_IN";
                            username = res.username();
                            authToken = res.authToken();
                            return "Registered user " + res.username() + " and logged in";
                        } catch (ServerFacadeException e) {
                            if (e.toString().contains("taken")){
                                return "That username is taken, try another";
                            }
                        }

                    }
                } else if (input.startsWith("login")) {
                    String[] parts = input.split(" ");
                    if (parts.length != 3) {
                        return "Please provide a username and password";
                    } else {
                        LoginRequest req = new LoginRequest(parts[1], parts[2]);
                        try{
                            LoginResult res = serverFacade.login(req);

                            status = "LOGGED_IN";
                            username = res.username();
                            authToken = res.authToken();
                            return "Logged in user " + res.username();
                        } catch(ServerFacadeException e) {
                            if (e.toString().contains("unauthorized")){
                                return "That combination of username and password is unrecognized, try logging in again";
                            }
                            System.out.println(e.toString());
                        }

                    }
                }
            } else if (status.equals("LOGGED_IN")) {
                if (input.startsWith("create")) {
                    String[] parts = input.split(" ");
                    if (parts.length != 2) {
                        return "Invalid, please provide a name for the game to create";
                    } else {

                        MakeGameRequest req = new MakeGameRequest(authToken, parts[1]);
                        MakeGameResult res = serverFacade.makeGame(req);

                        return "Game called " + parts[1] + " has been started";
                    }
                } else if (input.startsWith("list")) {
                    ListRequest req = new ListRequest(authToken);
                    ListResult res = serverFacade.listGames(req);



                    recentGameListing = res.games();

                    StringBuilder outMsg = new StringBuilder();

                    outMsg.append("All current games:\n");
                    for (String game : res.games()){
                        String[] infos = game.split(" ");

                        String gameNum = infos[0].split(":")[1];
                        String whitePlayer = infos[1].split(":")[1];
                        String blackPlayer = infos[2].split(":")[1];
                        String gameName = infos[3].split(":")[1];


                        outMsg.append(gameNum);
                        outMsg.append("- ");
                        outMsg.append(gameName);
                        outMsg.append(": ");
                        if (whitePlayer.equals("null")){
                            outMsg.append("<waiting for player>");
                        } else{
                            outMsg.append(whitePlayer);
                        }
                        outMsg.append(" (WHITE) vs ");
                        if (blackPlayer.equals("null")){
                            outMsg.append("<waiting for player>");
                        } else{
                            outMsg.append(blackPlayer);
                        }
                        outMsg.append(" (BLACK)\n");
                    }

                    return outMsg.toString().trim();

                } else if (input.startsWith("join")) {
                    String[] parts = input.split(" ");
                    if (parts.length != 3) {
                        return "Invalid arguments";
                    } else if (!(parts[2].equals("WHITE") || parts[2].equals("BLACK"))){
                        return "Invalid team name. try \"WHITE\" or \"BLACK\"";
                    } else {
                        int gameID = -1;
                        try{
                            gameID = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e){
                            return "Game ID is not a number... did you put the name instead?";
                        }

                        JoinGameRequest req = new JoinGameRequest(authToken, parts[2], gameID);
                        try{
                            serverFacade.joinGame(req);
                            game = getGameWithID(Integer.parseInt(parts[1]));
                            if (game == null){
                                return "No game with that ID";
                            }

                            return "User " + username + " has joined game #" + parts[1] + " as the " + parts[2] + " player" + "\n" + getBoard(parts[2]);

                        } catch (ServerFacadeException e) {
                            if (e.toString().contains("taken")){
                                return "That player spot is taken";
                            } else if (e.toString().contains("bad")){
                                return "No game with that ID, try again";
                            }
                        }

                    }
                } else if (input.startsWith("observe")) {
                    String[] parts = input.split(" ");
                    if (parts.length != 2) {
                        return "Please provide a game ID to observe";
                    } else {
                        game = getGameWithID(Integer.parseInt(parts[1]));
                        if (game == null){
                            return "No game with that ID";
                        }

                        return "Lets observe game #" + parts[1] + "\n" + getBoard("WHITE");
                    }
                } else if (input.startsWith("logout")) {
                    LogoutRequest req = new LogoutRequest(authToken);
                    serverFacade.logout(req);

                    status = "LOGGED_OUT";
                    String outMsg = "Logged out successfully. So long, " + username + "!";
                    username = null;
                    authToken = null;

                    return outMsg;
                }
            }
        }
        return "Alright Curtis you forgot to implement something";
    }

    private String getBoard(String team){
        String[] infos = game.split(" ");

        String gameString = infos[4].split(":")[1];

        StringBuilder fancyString = new StringBuilder();
        String LETTER_ROW;
        String[] NUMBER_COLUMN;
        if (team.equals("WHITE")){
            LETTER_ROW = "    a  b  c  d  e  f  g  h   ";
            NUMBER_COLUMN = new String[]{" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};

        } else if (team.equals("BLACK")){
            LETTER_ROW = "    h  g  f  e  d  c  b  a    ";
            NUMBER_COLUMN = new String[]{" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        } else{
            return "C'mon son";
        }


        fancyString.append(LETTER_ROW + "\n");

        ChessBoard board = new ChessBoard(gameString.substring(6));

        for (int i = 1; i < 9; i++){
            fancyString.append(NUMBER_COLUMN[i-1]);
            for (int j = 1; j < 9; j++){
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece == null){
                    fancyString.append("   ");
                } else {
                    fancyString.append(" " + piece.toString() + " ");
                }
            }
            fancyString.append(NUMBER_COLUMN[i-1] + "\n");
        }
        fancyString.append(LETTER_ROW);

        return fancyString.toString();
    }

    private String getGameWithID(int id){
        String gameListRepresentation = "";

        for (String gameString : recentGameListing){
            String[] infos = gameString.split(" ");

            String gameNum = infos[0].split(":")[1];
            if (Integer.parseInt(gameNum) == id){
                gameListRepresentation = gameString;
            }
        }

        if (gameListRepresentation.isEmpty()){
            return null;
        }
        return gameListRepresentation;
    }

    private String getHelpString(){
        if (status.equals("LOGGED_OUT")){
            return "Valid commands:\n\tregister <username> <password> <email>\n\tlogin <username> <password>\n\tquit\n\thelp";
        }else if (status.equals("LOGGED_IN")){
            return "Valid commands:\n\tcreate <name>\n\tlist\n\tjoin <ID#> <team>\n\tobserve <ID#>\n\tlogout\n\tquit\n\thelp";
        }
        return "You broke the FSM, there can be no help for you";
    }

    public String getPrompt(){
        if (!running){
            return "";
        }
        String prompt = "[";
        prompt += status;

        prompt += "] >>> ";
        return prompt;
    }
}
