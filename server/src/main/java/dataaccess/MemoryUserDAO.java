package dataaccess;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {

    private ArrayList<UserData> users = new ArrayList<>();

    public UserData getUser(String username) throws DataAccessException{
        for (UserData user : users){
            if (user.username().equals(username)){
                return user;
            }
        }

        return null;
    }

    public void createUser(UserData user){
        users.add(user);
    }

    public void clear() {
        users.clear();
    }
}
