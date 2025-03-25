import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import dataclasses.GameData;
import facade.ServerFacade;
import facade.ServerFacadeException;
import request.*;
import result.*;

import java.util.ArrayList;

import static ui.EscapeSequences.*;

public class Client {

    private ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

    private String status = "LOGGED_OUT";
    private boolean running = true;
    private String username = null;
    private String authToken = null;
    private GameData game = null;
    private ArrayList<GameData> recentGameListing = null;

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
                    return doRegister(input);
                } else if (input.startsWith("login")) {
                    return doLogin(input);
                }
            } else if (status.equals("LOGGED_IN")) {
                if (input.startsWith("create")) {
                    return doCreate(input);
                } else if (input.startsWith("list")) {
                    return doList();
                } else if (input.startsWith("join")) {
                    return doJoin(input);
                } else if (input.startsWith("observe")) {
                    return doObserve(input);
                } else if (input.startsWith("logout")) {
                    return doLogout();
                }
            }
        }
        return "Alright Curtis you forgot to implement something";
    }

    private String doLogout() {
        LogoutRequest req = new LogoutRequest(authToken);
        serverFacade.logout(req);

        status = "LOGGED_OUT";
        String outMsg = "Logged out successfully. So long, " + username + "!";
        username = null;
        authToken = null;

        return outMsg;
    }

    private String doObserve(String input) {
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
    }

    private String doJoin(String input) {
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

                return "User " + username + " has joined game #" + game.gameID() + " as the " + parts[2] + " player" + "\n" + getBoard(parts[2]);

            } catch (ServerFacadeException e) {
                if (e.toString().contains("taken")){
                    return "That player spot is taken";
                } else if (e.toString().contains("bad")){
                    return "No game with that ID, try again";
                }
            }

        }
        return null;
    }

    private String doList() {
        ListRequest req = new ListRequest(authToken);
        ListResult res = serverFacade.listGames(req);

        recentGameListing = res.games();

        StringBuilder outMsg = new StringBuilder();

        outMsg.append("All current games:\n");
        for (GameData game : res.games()){


            int gameNum = game.gameID();
            String whitePlayer = game.whiteUsername();
            String blackPlayer = game.blackUsername();
            String gameName = game.gameName();


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
    }

    private String doCreate(String input) {
        String[] parts = input.split(" ");
        if (parts.length != 2) {
            return "Invalid, please provide a name for the game to create";
        } else {

            MakeGameRequest req = new MakeGameRequest(authToken, parts[1]);
            MakeGameResult res = serverFacade.makeGame(req);

            recentGameListing = serverFacade.listGames(new ListRequest(authToken)).games();

            return "Game called " + parts[1] + " has been started";
        }
    }

    private String doLogin(String input) {
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
                recentGameListing = serverFacade.listGames(new ListRequest(authToken)).games();
                return "Logged in user " + res.username();
            } catch(ServerFacadeException e) {
                if (e.toString().contains("unauthorized")){
                    return "That combination of username and password is unrecognized, try logging in again";
                }
                System.out.println(e.toString());
            }

        }
        return null;
    }

    private String doRegister(String input) {
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

                recentGameListing = serverFacade.listGames(new ListRequest(authToken)).games();

                return "Registered user " + res.username() + " and logged in";
            } catch (ServerFacadeException e) {
                if (e.toString().contains("taken")){
                    return "That username is taken, try another";
                }
            }

        }
        return null;
    }

    private String getBoard(String team){

        String gameString = game.game().toString();

        StringBuilder fancyString = new StringBuilder();
        String letterRow;
        String[] numberColumn;
        if (team.equals("WHITE")){
            letterRow = "    a  b  c  d  e  f  g  h    ";
            numberColumn = new String[]{" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};

        } else if (team.equals("BLACK")){
            letterRow = "    h  g  f  e  d  c  b  a     ";
            numberColumn = new String[]{" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        } else{
            return "C'mon son";
        }

        letterRow = SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_WHITE + letterRow;
        for (int i = 0; i < 8; i++){
            numberColumn[i] = SET_BG_COLOR_DARK_GREEN + SET_TEXT_COLOR_WHITE + numberColumn[i];
        }


        fancyString.append(letterRow + RESET_BG_COLOR + "\n");

        ChessBoard board = new ChessBoard(gameString.substring(6));

        for (int i = 1; i < 9; i++){
            fancyString.append(numberColumn[i-1]);
            for (int j = 1; j < 9; j++){
                if ((i+j) % 2 == 0){
                    fancyString.append(SET_BG_COLOR_WHITE);
                } else {
                    fancyString.append(SET_BG_COLOR_BLACK);
                }
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if (piece == null){
                    fancyString.append("   ");
                } else {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                        fancyString.append(SET_TEXT_COLOR_RED);
                    } else{
                        fancyString.append(SET_TEXT_COLOR_BLUE);
                    }
                    fancyString.append(" " + piece.toString() + " ");
                }
            }
            fancyString.append(numberColumn[i-1] + RESET_BG_COLOR + "\n");
        }
        fancyString.append(letterRow + RESET_BG_COLOR);

        return fancyString.toString();
    }

    private GameData getGameWithID(int id){

        for (GameData game : recentGameListing){

            if (game.gameID() == id){
                return game;
            }
        }
        return null;
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
