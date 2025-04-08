package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand{
    public ChessMove move;

    public MakeMoveCommand(String authToken, int gameID, ChessMove move){
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }
}
