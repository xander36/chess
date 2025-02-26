package dataaccess;

public record AuthData(String username, String authToken) {
    @Override
    public String toString() {
        return "AuthData{" +
                "username='" + username + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}
