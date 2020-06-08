package dev.dhdf.mcauth;

import org.bukkit.entity.Player;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Client {
    private final String baseURL;
    private final String token;

    public Client(String address, int port, String token) {
        this.baseURL = "http://" + address + ":" + port;
        this.token = token;
    }

    public JSONObject getIsValid(Player player) {
        String uuid = player.getUniqueId().toString().replace("-", "");
        String body = new JSONStringer()
                .object()
                .key("player_id")
                .value(uuid)
                .endObject()
                .toString();

        JSONObject response = this.doRequest(
                this.baseURL + "/isValidPlayer",
                body
        );

        return response;
    }

    private JSONObject doRequest(String target, String body) {
        try {
            URL url = new URL(target);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + this.token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Spigot Plugin");


            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body.toString());
            writer.flush();
            writer.close();

            InputStream stream = connection.getInputStream();

            JSONTokener parsing = new JSONTokener(stream);
            return new JSONObject(parsing);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
