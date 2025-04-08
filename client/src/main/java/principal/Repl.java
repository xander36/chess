package principal;

import chess.ChessGame;
import chess.ChessPiece;
import facade.NotificationHandler;

import java.util.Scanner;

import websocket.messages.Notification;

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
            System.out.print(client.getPrompt());
        }

        System.out.println();
    }

    public void notify(Notification notification) {
        System.out.println(notification.message());
        System.out.print(client.getPrompt());
    }

}
