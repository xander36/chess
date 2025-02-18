package dataaccess;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData user);
    void clear();
}
