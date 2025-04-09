package principal;

import chess.ChessGame;
import chess.ChessPiece;
import facade.NotificationHandler;

import java.util.Scanner;

import websocket.messages.*;


public class Repl implements NotificationHandler {

    private Client client;

    public Repl(String serverUrl) {
        this.client = new Client(serverUrl, this);
    }

    public void run() {

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess principal.Client: " + piece);

        System.out.print("Welcome!\n" + client.getPrompt());

        Scanner scan = new Scanner(System.in);
        String result = "";
        while (!result.startsWith("Session over")){
            result = client.eval(scan.nextLine());
            System.out.println(result);
            if (!result.trim().isEmpty()) {
                System.out.print(client.getPrompt());
            }
        }

        System.out.println();
    }

    public void notify(ServerMessage notification) {
        if (notification instanceof NotificationMessage){
            notNotify((NotificationMessage) notification);
        } else if (notification instanceof ErrorMessage) {
            errNotify((ErrorMessage) notification);
        } else if (notification instanceof LoadGameMessage) {
            loadNotify((LoadGameMessage) notification);
        }else{
            System.out.println("recieved an ambiguous message");
            System.out.print(client.getPrompt());
        }
    }

    public void notNotify(NotificationMessage not){
        System.out.println();
        System.out.println("NOTICE: " + not.message);
        System.out.print(client.getPrompt());
    }

    public void errNotify(ErrorMessage err){
        System.out.println();
        System.out.println("Error: " + err.errorMessage);
        System.out.print(client.getPrompt());

    }

    public void loadNotify(LoadGameMessage load){
        System.out.println();
        System.out.println("Current Board:");
        client.setGame(load.game);
        System.out.println(client.boardString());
        System.out.print(client.getPrompt());
    }



}
