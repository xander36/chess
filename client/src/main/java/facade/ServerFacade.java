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
        return this.makeRequest("POST", path, registerRequest, null, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest){
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, null, LoginResult.class);
    }

    public void logout(LogoutRequest logoutRequest){
        var path = "/session";
        this.makeRequest("DELETE", path, logoutRequest, logoutRequest.authToken(), null);
    }


    public ListResult listGames(ListRequest listRequest) {
        System.out.println("list games facade");
        var path = "/game";
        return this.makeRequest("GET", path, listRequest, listRequest.authToken(), ListResult.class);
    }

    public MakeGameResult makeGame(MakeGameRequest makeGameRequest){
        System.out.println("make game facade");
        var path = "/game";
        return this.makeRequest("POST", path, makeGameRequest, makeGameRequest.authToken(), MakeGameResult.class);
    }

    public void joinGame(JoinGameRequest joinGameRequest){
        var path = "/game";
        this.makeRequest("PUT", path, joinGameRequest, joinGameRequest.authToken(), null);
    }

    public ClearResult clear(ClearRequest clearRequest){
        var path = "/db";
        return this.makeRequest("DELETE", path, clearRequest, null, ClearResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, String authHeader, Class<T> responseClass) throws ServerFacadeException{
        System.out.println("Wizardry begins. we build a " + method + " request");
        System.out.println("we'll get back a " + responseClass);


        try {
            URL url = (new URI(this.url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authHeader != null){
                System.out.println("Authorization will be bundled in");
                http.setRequestProperty("Authorization", authHeader);
            }

            writeBody(request, http);
            http.connect();
            System.out.println("connection wizardy happened");
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
        System.out.println("serverside issues check - we got something back at least");
        var status = http.getResponseCode();
        System.out.println(status);
        if (!(status/100 == 2)) {
            try (InputStream respErr = http.getErrorStream()) {
                System.out.println(respErr);
                if (respErr != null) {
                    System.out.println("There was a problem, imma build a nice error to wrap it up");
                    throw ServerFacadeException.fromJson(status, respErr);
                }
            }

            throw new ServerFacadeException(status, "other failure: " + status);
        }
        System.out.println("no issues");
    }
}
