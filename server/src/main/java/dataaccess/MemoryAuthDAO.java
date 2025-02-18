package dataaccess;

import java.util.ArrayList;


public class MemoryAuthDAO implements AuthDAO {

    private ArrayList<AuthData> authorizations = new ArrayList<>();

    public void createAuth(AuthData data){
        authorizations.add(data);
    }

    public AuthData getAuth(String token){
        for (AuthData auth : authorizations){
            if (auth.authToken().equals(token)){
                return auth;
            }
        }
        return null;
    }

    public void clear() {
        authorizations.clear();
    }

    public void deleteAuth(AuthData data){
        authorizations.remove(data);
    }
}
