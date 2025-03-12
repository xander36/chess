package dataaccess;

import java.sql.SQLException;
import java.util.ArrayList;


public class DatabaseAuthDAO implements AuthDAO {

    public DatabaseAuthDAO(){
        String[] creationSQL = {
                """
                CREATE TABLE IF NOT EXISTS auth (
                  `id` int NOT NULL AUTO_INCREMENT,
                  `username` VARCHAR(256) NOT NULL,
                  `authToken` VARCHAR(256) NOT NULL,
                   PRIMARY KEY (`id`),
                   INDEX(username),
                   INDEX(authToken)
                 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 
                """
        };
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

    public void createAuth(AuthData data) throws DataAccessException{
        boolean tokenAlreadyExists = false;
        try{
            AuthData possibleDuplicate = getAuth(data.authToken());
            tokenAlreadyExists = true;
        } catch (DataAccessException e) {

        }

        if (tokenAlreadyExists){
            throw new DataAccessException("authToken already registered");
        }

        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(1, data.username());
                preparedStatement.setString(2, data.authToken());

                var rs = preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AuthData getAuth(String token) throws DataAccessException{

        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "SELECT username FROM auth WHERE authToken = ?";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(1, token);

                var rs = preparedStatement.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("No matching auth");
                } else {
                    String username = rs.getString(1);
                    return new AuthData(username, token);
                }


            }
        } catch (SQLException e) {
            throw new DataAccessException("empty?");
        }
    }

    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "TRUNCATE TABLE auth";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {

                var rs = preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAuth(AuthData data) throws DataAccessException{

        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "DELETE FROM auth WHERE username = ? AND authToken = ?";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(1, data.username());
                preparedStatement.setString(2, data.authToken());

                var rs = preparedStatement.executeUpdate();

                if (rs == 0){
                    throw new DataAccessException("attempted to delete something that doesnt exist");
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
