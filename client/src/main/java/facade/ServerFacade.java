package facade;

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
        var path = "/user";
        return this.makeRequest("POST", path, registerRequest, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest){
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, LoginResult.class);
    }

    public void logout(LogoutRequest logoutRequest){
        var path = "/session";
        this.makeRequest("DELETE", path, logoutRequest, null);
    }


    public ListResult listGames(ListRequest listRequest) {
        var path = "/game";
        return this.makeRequest("GET", path, listRequest, ListResult.class);
    }

    public MakeGameResult makeGame(MakeGameRequest makeGameRequest){
        var path = "/game";
        return this.makeRequest("POST", path, makeGameRequest, MakeGameResult.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest){
        var path = "/game";
        this.makeRequest("PUT", path, joinGameRequest, null);
    }

    public ClearResult clear(ClearRequest clearRequest){
        var path = "/db";
        return this.makeRequest("DELETE", path, clearRequest, ClearResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ServerFacadeException{
        try {
            URL url = (new URI(this.url + path)).toURL();
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
            throw new ServerFacadeException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String requestJson = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(requestJson.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ServerFacadeException {
        System.out.println("serverfcade issues check");
        var status = http.getResponseCode();
        System.out.println(status);
        if (!(status/100 == 2)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ServerFacadeException.fromJson(respErr);
                }
            }

            throw new ServerFacadeException(status, "other failure: " + status);
        }
        System.out.println("no issues");
    }
}
