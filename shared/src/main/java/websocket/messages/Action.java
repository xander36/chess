package websocket.messages;

import chess.ChessMove;
import com.google.gson.Gson;

public record Action(Type type, String username, ChessMove move) {
    public enum Type {
        LEAVE,
        RESIGN,
        MOVE
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
