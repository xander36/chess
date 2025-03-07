package dataaccess;

import java.util.ArrayList;

public interface GameDAO {
    ArrayList<String> listGames (String authToken);
    void clear();
    int makeGame(String gameName);
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData newGame) throws DataAccessException;
}
