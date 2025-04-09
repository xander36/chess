package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private int game;

    public LoadGameMessage(int gameID){
        super(ServerMessageType.LOAD_GAME);
        this.game = gameID;
    }
}
