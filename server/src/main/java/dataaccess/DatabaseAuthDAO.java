package dataaccess;

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

    public void createAuth(AuthData data){
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(1, data.username());
                preparedStatement.setString(2, data.authToken());

                var rs = preparedStatement.executeUpdate();
                System.out.println(rs);
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
                rs.next();
                String username = rs.getString(1);

                if (username == null){
                    System.out.println("Cant get it out");
                }

                return new AuthData(username, token);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    public void deleteAuth(AuthData data){
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "DELETE FROM auth WHERE username = ?, authData = ?";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(1, data.username());
                preparedStatement.setString(2, data.authToken());

                var rs = preparedStatement.executeUpdate();
                /*
                String username = rs.getString(1);
                System.out.println(username);

                if (username == null){
                    System.out.println("Cant get it out");
                }
                 */
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
