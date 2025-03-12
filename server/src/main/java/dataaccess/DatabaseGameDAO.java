package dataaccess;


import chess.ChessGame;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DatabaseGameDAO implements GameDAO {

    int size = 0;

    public DatabaseGameDAO(){
        String[] creationSQL = {
                """
                CREATE TABLE IF NOT EXISTS game (
                    `gameID` int NOT NULL AUTO_INCREMENT,
                    `whiteUsername` VARCHAR(256),
                    `blackUsername` VARCHAR(256),
                    `gameName` VARCHAR(256) NOT NULL,
                    `game` VARCHAR(256),
                    PRIMARY KEY (`gameID`),
                    INDEX(gameID)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
                //System.out.println("game had connection problem");
            }
        } catch (DataAccessException e) {
            //System.out.println("Database connection had a problem");
        }
    }

    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "TRUNCATE TABLE game";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                System.out.println(preparedStatement.toString());
                var rs = preparedStatement.executeUpdate();
                size = 0;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> listGames (String authToken) throws DataAccessException{
        if(authToken.equals("")){
            throw new DataAccessException("no authtoken");
        }

        ArrayList<String> outList = new ArrayList<String>();
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                System.out.println(preparedStatement.toString());
                var rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    int gameID = rs.getInt(1);
                    String whiteUsername = rs.getString(2);
                    String blackUsername = rs.getString(3);
                    String gameName = rs.getString(4);
                    String chessGameString = rs.getString(5);

                    ChessGame gameObj = null;

                    if (chessGameString != null && !chessGameString.equals("null")){
                        gameObj = new ChessGame(chessGameString);
                    }

                    GameData game = new GameData(gameID, whiteUsername, blackUsername, gameName, gameObj);

                    outList.add(String.format("{\"gameID\": %s, \"whiteUsername\": %s, \"blackUsername\": %s, \"gameName\": \"%s\"}",
                            game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
                }

            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }

        return outList;
    }

    public int makeGame(String gameName) throws DataAccessException{
        if (gameName.equals("")){
            throw new DataAccessException("empty game name - invalid");
        }

        int newId = size+1;
        System.out.println("Make number");
        System.out.println(newId);
        GameData newGame = new GameData(newId, null, null, gameName, null);

        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setInt(1, newGame.gameID());
                preparedStatement.setString(2,null);
                preparedStatement.setString(3, null);
                preparedStatement.setString(4, newGame.gameName());
                preparedStatement.setString(5, "null");

                System.out.println(preparedStatement.toString());

                var rs = preparedStatement.executeUpdate();

                size++;
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
        return newId;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "SELECT whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setInt(1, gameID);
                System.out.println(preparedStatement.toString());
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {

                    String whiteUsername = rs.getString(1);
                    String blackUsername = rs.getString(2);
                    String gameName = rs.getString(3);
                    String chessGameString = rs.getString(4);

                    ChessGame game = null;
                    if (!chessGameString.equals("null")) {
                        game = new ChessGame(chessGameString);
                    }


                    //Unblank the chess game
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                }
                throw new DataAccessException("No game with that ID");
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateGame(int gameID, GameData newGame) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? where gameID = ?";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(1, newGame.whiteUsername());
                preparedStatement.setString(2, newGame.blackUsername());
                preparedStatement.setString(3, newGame.gameName());

                System.out.println(preparedStatement.toString());
                if (newGame.game() == null){
                    preparedStatement.setString(4, "null");
                }else {
                    preparedStatement.setString(4, newGame.game().toString());
                }

                preparedStatement.setInt(5, gameID);
                var rs = preparedStatement.executeUpdate();
                System.out.println(rs);

                if (rs == 0){
                    throw new DataAccessException("Invalid update, no rows altered");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
