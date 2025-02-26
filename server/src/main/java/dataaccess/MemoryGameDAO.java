package dataaccess;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    private ArrayList<GameData> games = new ArrayList<>();

    public GameData getUser(int gameID) throws DataAccessException{
        for (GameData game : games){
            if (game.gameID() == gameID){
                return game;
            }
        }
        return null;
    }

    public void createGame(GameData game){
        games.add(game);
    }

    public void clear() {
        games.clear();
    }

    public ArrayList<String> listGames (String authToken){
        ArrayList<String> outList = new ArrayList<String>();

        for (GameData game : games){
            outList.add(String.format("{\"gameID\": %s, \"whiteUsername\": %s, \"blackUsername\": %s, \"gameName\": %s,}", ""+game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }

        return outList;
    }
}
