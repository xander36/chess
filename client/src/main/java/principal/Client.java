package principal;

import chess.*;
import dataclasses.GameData;
import facade.*;
import request.*;
import result.*;

import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class Client {
    private String status = "LOGGED_OUT";
    private boolean running = true;
    private String username = null;
    private String authToken = null;
    private GameData game = null;
    private ChessGame.TeamColor team = null;
    private ArrayList<GameData> recentGameListing = null;

    String url;
    private ServerFacade serverFacade;
    private WebSocketFacade webSocketFacade;

    public Client(String url, Repl repl){
        serverFacade  = new ServerFacade(url);
        webSocketFacade = new  WebSocketFacade(url, repl);
    }

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
            } else if (status.equals("PLAY")) {
                if (input.startsWith("redraw")) {
                    return doRedraw();
                } else if (input.startsWith("leave")) {
                    return doLeave();
                } else if (input.startsWith("move")) {
                    return doMove(input);
                } else if (input.startsWith("resign")) {
                    return doResign();
                } else if (input.startsWith("show")) {
                    return doShowMoves(input);
                }
            }
        }
        return "Unknown command - check your spelling!";
    }

    private String doShowMoves(String input) {
        String[] parts = input.split(" ");

        if (parts.length < 2){
            return "Please provide a position for which to show legal moves";
        } else if (parts.length > 2){
            return "Invalid arguments";
        } else{
            String pos = parts[1];

            if (pos.length() != 2){
                return "Invalid position";
            }

            int col = Character.toLowerCase(pos.charAt(0)) + 1 - 'a';
            int row = Integer.parseInt(pos.charAt(1)+"");

            if (row < 1 || row > 8 || col < 1 || col > 8){
                return "Invalid position";
            }

            ChessPosition chessPos = new ChessPosition(row, col);

            Collection<ChessMove> moves = game.game().validMoves(chessPos);

            String out = "";

            ArrayList<ChessPosition> positions = new ArrayList<>();
            positions.add(chessPos);

            for (ChessMove move: moves){
                positions.add(move.getEndPosition());
            }



            return getBoard(positions);
        }

    }

    private String doResign() {
        try {
            webSocketFacade.resign(username);
            status = "LOGGED_IN";
            return "imma resign";
        } catch (WebSocketException e) {
            return "I didnt because " + e.toString();
        }
    }

    private String doMove(String input) {
        ChessMove move = null;

        try {

            webSocketFacade.move(username, move);
            status = "LOGGED_IN";
            return "imma resign";
        } catch (WebSocketException e) {
            return "I didnt because " + e.toString();
        }
    }

    private String doLeave() {
        try {
            webSocketFacade.leaveGame(username);
            status = "LOGGED_IN";
            return "Left current game";
        } catch (WebSocketException e) {
            return "I didnt because " + e.toString();
        }
    }

    private String doRedraw() {
        return getBoard();
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
            team = ChessGame.TeamColor.WHITE;
            return "Lets observe game #" + parts[1] + "\n" + getBoard();
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
                if (parts[2].equals("BLACK")){
                    team = ChessGame.TeamColor.BLACK;
                }
                else if (parts[2].equals("WHITE")){
                    team = ChessGame.TeamColor.WHITE;
                }
                status = "PLAY";
                return "User " + username + " has joined game #" + game.gameID() + " as the " + parts[2] + " player" + "\n" + getBoard();

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
            if (whitePlayer == null){
                outMsg.append("<waiting for player>");
            } else{
                outMsg.append(whitePlayer);
            }
            outMsg.append(" (WHITE) vs ");
            if (blackPlayer == null){
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

    private String getBoard(){
        return getBoard(new ArrayList<ChessPosition>());
    }

    private String getBoard(ArrayList<ChessPosition> positions){

        String gameString = game.game().toString();

        StringBuilder fancyString = new StringBuilder();
        String letterRow;
        String[] numberColumn;
        if (team == ChessGame.TeamColor.WHITE){
            letterRow = "    a  b  c  d  e  f  g  h    ";
            numberColumn = new String[]{" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};

        } else if (team == ChessGame.TeamColor.BLACK){
            letterRow = "    h  g  f  e  d  c  b  a    ";
            numberColumn = new String[]{" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
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



                if (team == ChessGame.TeamColor.BLACK){
                    i = 9-i;
                    j = 9-j;
                }

                boolean highlightHere = false;

                for (ChessPosition pos: positions){
                    if (pos.getRow() == i && pos.getColumn() == j){
                        highlightHere = true;
                    }
                }

                if ((i+j) % 2 == 0){
                    if (highlightHere){
                        fancyString.append(SET_BG_COLOR_YELLOW);
                    }else{
                        fancyString.append(SET_BG_COLOR_WHITE);
                    }
                } else {
                    if (highlightHere){
                        fancyString.append(SET_BG_COLOR_ORANGE);
                    }else{
                        fancyString.append(SET_BG_COLOR_BLACK);
                    }
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

                if (team == ChessGame.TeamColor.BLACK){
                    i = 9-i;
                    j = 9-j;
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
        }else if (status.equals("PLAY")){
            return "Valid commands:\n\tshow <position>\n\tmove <MOVE>\n\tredraw\n\tresign\n\tleave\n\thelp";
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
