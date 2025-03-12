package dataaccess;

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
        System.out.println("get");
        try (var conn = DatabaseManager.getConnection()) {

            System.out.println("connection made");
            String addStatement = "SELECT username, password, email FROM user WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                System.out.println("teterent ready");
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                System.out.println("query executed");
                if (!rs.next()) {
                    System.out.println("no user with name");
                    throw new DataAccessException("No user with that name");
                } else {
                    System.out.println("got it");
                    String password = rs.getString(2);
                    String email = rs.getString(3);

                    System.out.println("successful");
                    return new UserData(username, password, email);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser(UserData user) throws DataAccessException{
        System.out.println("creation prep");
        boolean usernameAvailable = true;
        try {
            getUser(user.username());
            usernameAvailable = false;

        } catch (DataAccessException e){

        }

        if (!usernameAvailable){
            throw new DataAccessException("username unavailable");
        }

        System.out.println("creation time");

        try (var conn = DatabaseManager.getConnection()) {
            System.out.println("connection made");
            String addStatement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                System.out.println("teterent ready");
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.password());
                preparedStatement.setString(3, user.email());

                var rs = preparedStatement.executeUpdate();
                System.out.println("insert complete");
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
            System.out.println("error");
            throw new RuntimeException(e);
        }
    }
}
