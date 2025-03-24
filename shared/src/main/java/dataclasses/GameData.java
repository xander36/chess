package dataclasses;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){
    @Override
    public String toString() {
        return gameID +
                "&" + whiteUsername +
                "&" + blackUsername +
                "&" + gameName +
                "&" + game;
    }
}
