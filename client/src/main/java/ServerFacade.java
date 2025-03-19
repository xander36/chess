import java.util.UUID;

import request.*;
import result.*;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private String url;

    public ServerFacade(String url) {
        this.url = url;
    }

    public RegisterResult register(RegisterRequest registerRequest){

    }

    public LoginResult login(LoginRequest loginRequest){

    }

    public void logout(LogoutRequest logoutRequest){

    }

    public ListResult listGames(ListRequest listRequest) {

    }

    public MakeGameResult makeGame(MakeGameRequest makeGameRequest){

    }

    public void joinGame(JoinGameRequest joinGameRequest){

    }

    public ClearResult clear(ClearRequest registerRequest){
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) {
        try {
            URL url = (new URI(url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }
}
