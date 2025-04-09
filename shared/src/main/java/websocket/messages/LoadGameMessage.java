package websocket.messages;

import chess.ChessGame;
import dataclasses.GameData;

public class LoadGameMessage extends ServerMessage{
    private GameData game;

    public LoadGameMessage(GameData game){
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
