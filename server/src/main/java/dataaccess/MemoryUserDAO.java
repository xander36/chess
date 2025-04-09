package dataaccess;

import dataclasses.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {

    private ArrayList<UserData> users = new ArrayList<>();

    public UserData getUser(String username) throws DataAccessException{
        for (UserData user : users){
            if (user.username().equals(username)){
                return user;
            }
        }
        throw new DataAccessException("No user with that name");
    }

    public void createUser(UserData user) throws DataAccessException{
        try{
            getUser(user.username());

        }catch (DataAccessException e){
            //If the user doesnt exist then we're good to add it
            users.add(user);
            return;
        }
        throw new DataAccessException("username taken");

    }

    public void clear() {
        users.clear();
    }
}
