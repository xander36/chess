package dataaccess;

import dataclasses.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseUserDAO implements UserDAO {

    public DatabaseUserDAO(){
        String[] userCreationSQL = {
                """
                CREATE TABLE IF NOT EXISTS user (
                  `username` VARCHAR(256) NOT NULL,
                  `password` VARCHAR(256) NOT NULL,
                  `email` VARCHAR(256) NOT NULL,
                   PRIMARY KEY (`username`),
                   INDEX(username)
                 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 
                """
        };
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (String statementSQL : userCreationSQL) {
                    var preparedUserCreationStatement = conn.prepareStatement(statementSQL);
                    preparedUserCreationStatement.executeUpdate();

                }
            } catch(Exception e){
                System.out.println("connection problem");
            }
        } catch (DataAccessException e) {
            System.out.println("Database connection had a problem");
        }
    }

    public UserData getUser(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {

            String addStatement = "SELECT username, password, email FROM user WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("No user with that name");
                } else {
                    String password = rs.getString(2);
                    String email = rs.getString(3);

                    return new UserData(username, password, email);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser(UserData user) throws DataAccessException{
        boolean usernameAvailable = true;
        try {
            getUser(user.username());
            usernameAvailable = false;

        } catch (DataAccessException e){

        }

        if (!usernameAvailable){
            throw new DataAccessException("username unavailable");
        }


        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.password());
                preparedStatement.setString(3, user.email());

                var rs = preparedStatement.executeUpdate();
                return;
            }
        } catch (SQLException ee) {
            throw new RuntimeException(ee);
        }

    }

    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "TRUNCATE TABLE user";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                var rs = preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
