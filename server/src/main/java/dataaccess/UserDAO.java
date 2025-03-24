package dataaccess;

import dataclasses.UserData;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    void clear();
}
