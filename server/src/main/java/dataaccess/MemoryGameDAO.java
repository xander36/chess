package dataaccess;


import dataclasses.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    private ArrayList<GameData> games = new ArrayList<>();

    public void clear() {
        games.clear();
    }

    public ArrayList<GameData> listGames (String authToken){
        ArrayList<String> outList = new ArrayList<>();

        return games;
    }

    public int makeGame(String gameName){
        int newId = games.size()+1;
        GameData newGame = new GameData(newId, null, null, gameName, null);
        games.add(newGame);

        return newId;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        for (GameData game: games){
            if (game.gameID() == gameID){
                return game;
            }
        }
        throw new DataAccessException("No game with that ID");
    }

    public void updateGame(int gameID, GameData newGame) throws DataAccessException{
        games.remove(getGame(gameID));
        games.add(newGame);
    }
}
