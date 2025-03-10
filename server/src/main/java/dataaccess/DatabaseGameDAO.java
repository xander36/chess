package dataaccess;


import chess.ChessGame;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DatabaseGameDAO implements GameDAO {

    public DatabaseGameDAO(){
        String[] creationSQL = {
                """
                CREATE TABLE IF NOT EXISTS game (
                  `id` int NOT NULL AUTO_INCREMENT,
                  `gameID` int NOT NULL,
                  `whiteUsername` VARCHAR(256),
                  `blackUsername` VARCHAR(256),
                  `gameName` VARCHAR(256) NOT NULL,
                  'game' VARCHAR(256) NOT NULL,
                   PRIMARY KEY (`id`),
                   INDEX(gameID),
                 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 
                """
        };
        //int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (String statementSQL : creationSQL) {
                    try (var preparedStatement = conn.prepareStatement(statementSQL)) {
                        preparedStatement.executeUpdate();
                    }
                }
            } catch(Exception e){
                System.out.println("connection problem");
            }
        } catch (DataAccessException e) {
            System.out.println("Database connection had a problem");
        }
    }

    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "TRUNCATE TABLE game";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {

                var rs = preparedStatement.executeQuery();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> listGames (String authToken){
        ArrayList<String> outList = new ArrayList<String>();

        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "SELECT username FROM auth";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {

                var rs = preparedStatement.executeQuery();
                System.out.println(rs);


            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return outList;
    }

    public int makeGame(String gameName){
        int newId = 15;
        GameData newGame = new GameData(newId, null, null, gameName, null);

        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setInt(1, newGame.gameID());
                preparedStatement.setString(2, newGame.whiteUsername());
                preparedStatement.setString(3, newGame.whiteUsername());
                preparedStatement.setString(4, newGame.gameName());
                preparedStatement.setString(5, newGame.game().toString());

                var rs = preparedStatement.executeQuery();
                System.out.println(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return newId;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "SELECT username FROM auth WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setInt(1, gameID);

                var rs = preparedStatement.executeQuery();
                String whiteUsername = rs.getString(2);
                String blackUsername = rs.getString(3);
                String gameName = rs.getString(4);
                String chessGame = rs.getString(5);

                //Unblank the chess game
                return new GameData(gameID, whiteUsername, blackUsername, gameName, new ChessGame());

                //throw new DataAccessException("No game with that ID");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void updateGame(int gameID, GameData newGame) throws DataAccessException{

        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?)";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(2, newGame.whiteUsername());
                preparedStatement.setString(3, newGame.whiteUsername());
                preparedStatement.setString(4, newGame.gameName());
                preparedStatement.setString(5, newGame.game().toString());

                var rs = preparedStatement.executeQuery();
                System.out.println(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
