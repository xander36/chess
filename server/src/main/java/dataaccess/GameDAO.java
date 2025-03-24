package dataaccess;

import dataclasses.GameData;

import java.util.ArrayList;

public interface GameDAO {
    ArrayList<GameData> listGames (String authToken) throws DataAccessException;
    void clear();
    int makeGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData newGame) throws DataAccessException;
}
