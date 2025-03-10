package dataaccess;

import java.util.ArrayList;

public class DatabaseUserDAO implements UserDAO {

    public DatabaseUserDAO(){
        System.out.println("user");
        String[] creationSQL = {
                """
                CREATE TABLE IF NOT EXISTS auth (
                  `id` int NOT NULL AUTO_INCREMENT,
                  `username` VARCHAR(256) NOT NULL,
                  `password` VARCHAR(256) NOT NULL,
                  `email` VARCHAR(256) NOT NULL,
                   PRIMARY KEY (`id`),
                   INDEX(username)
                 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 
                """
        };
        try {
            System.out.println("try");
            DatabaseManager.createDatabase();
            System.out.println("created");
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

    public UserData getUser(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "SELECT username FROM user WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(1, username);

                var rs = preparedStatement.executeQuery();
                String password = rs.getString(2);
                String email = rs.getString(3);
                System.out.println(username);


                return new UserData(username, password, email);

                //throw new DataAccessException("No user with that name");

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser(UserData user){
        try (var conn = DatabaseManager.getConnection()) {
            String addStatement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.password());
                preparedStatement.setString(3, user.email());

                var rs = preparedStatement.executeQuery();
                System.out.println(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void clear() {
        System.out.println("let me be clear");
        System.out.println("try connecting");
        try (var conn = DatabaseManager.getConnection()) {
            System.out.println("connected");
            String addStatement = "TRUNCATE TABLE user";
            try (var preparedStatement = conn.prepareStatement(addStatement)) {

                var rs = preparedStatement.executeQuery();
            }
        } catch (Exception e) {
            System.out.println("error");
            throw new RuntimeException(e);
        }
    }
}
