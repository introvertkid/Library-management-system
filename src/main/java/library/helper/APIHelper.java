package library.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIHelper {
    private static final String API_KEY = "AIzaSyD6WrJZy3vLWjW9DREQxpBuo49pgPxcVhM";
    private static final String API_KEY_URL = "&key=" + API_KEY;
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";

    public static JsonObject fetchBookData(String query) {
        String urlString = BASE_URL + "?q=" + query + API_KEY_URL;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) //OK = 200
            {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();
                return jsonResponse;
            } else {
                System.out.println("HTTP error: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
