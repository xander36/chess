import chess.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Client client = new Client();

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

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
}