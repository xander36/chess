package dataaccess;

import java.util.ArrayList;

public interface GameDAO {
    ArrayList<String> listGames (String authToken);
    void clear();
}
