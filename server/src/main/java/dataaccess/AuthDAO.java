package dataaccess;

public interface AuthDAO {
    void createAuth(AuthData data);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData data);
    void clear();
}
