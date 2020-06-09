package dev.dhdf.mcauth;

import dev.dhdf.mcauth.types.AltAcc;
import org.bukkit.entity.Player;
import org.json.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class Client {
    private final String baseURL;
    private final String token;

    public Client(String address, int port, String token) {
        this.baseURL = "http://" + address + ":" + port;
        this.token = token;
    }

    /**
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Player%20Validation.md#post-isplayervalid
     * @param player The player being validated
     * @return JSONObject (see provided link)
     * @throws IOException The HTTP request failed
     */
    public JSONObject isValidPlayer(Player player) throws IOException {
        String uuid = player.getUniqueId().toString().replace("-", "");
        String body = new JSONStringer()
                .object()
                .key("player_id")
                .value(uuid)
                .endObject()
                .toString();

        return this.doRequest(
                this.baseURL + "/isValidPlayer",
                "POST",
                body
        );
    }

    /////////////////////////
    // Alt Account Management
    /////////////////////////

    /**
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#post-newalt
     * @param owner The owner claiming the alt account
     * @param altName The name of the alt account being claimed
     * @return JSONObject
     * @throws IOException The HTTP request failed
     */
    public JSONObject addAltAccount(Player owner, String altName) throws IOException {
        String body = new JSONStringer()
                .object()
                .key("player_name")
                .value(altName)
                .key("owner")
                .value(owner.getName())
                .endObject()
                .toString();

        return this.doRequest(
                this.baseURL + "/newAlt",
                "POST",
                body
        );
    }

    /**
     * This deletes a given alt account
     * @param altName The name of the alt account to remove
     * @return JSONObject
     * @throws IOException If the HTTP request failed
     */
    public JSONObject remAltAccount(String altName) throws IOException {
        String body = new JSONStringer()
                .object()
                .key("player_name")
                .value(altName)
                .endObject()
                .toString();

        return this.doRequest(
                this.baseURL + "/delAlt",
                "DELETE",
                body
        );
    }

    /**
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#get-getaltsofowner
     * @param owner Alt account owner
     * @return ArrayList<AltAcc>
     * @throws IOException If the HTTP request failed
     * @throws JSONException If the server doesn't recognize the owner
     */
    public ArrayList<AltAcc> listAltAccounts(String owner) throws IOException, JSONException {
        ArrayList<AltAcc> accounts = new ArrayList<AltAcc>();
        JSONObject result = this.doGetRequest(
                this.baseURL + "/getAltsOf/" + owner
        );
        JSONArray alts = result.getJSONArray("alt_accs");

        for (int i = 0; i < alts.length(); ++i) {
            JSONObject rawAltAcc = (JSONObject) alts.get(i);
            String altName = rawAltAcc.getString("alt_name");
            String altUUID = rawAltAcc.getString("alt_id");
            AltAcc altAcc = new AltAcc(altName, altUUID, owner);

            accounts.add(altAcc);
        }

        return accounts;
    }


    ///////////////////
    // Utility Methods
    ///////////////////


    private HttpURLConnection buildRequest(String target, String method) throws IOException {
        URL url = new URL(target);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "Bearer " + this.token);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Spigot Plugin");

        return connection;
    }

    private JSONObject doGetRequest(String target) throws IOException {
        HttpURLConnection connection = buildRequest(target, "GET");

        InputStream stream = connection.getInputStream();

        try {
            JSONTokener parsing = new JSONTokener(stream);
            return new JSONObject(parsing);
        } catch (JSONException e) {
            return new JSONObject();
        }

    }

    private JSONObject doRequest(String target, String method, String body) throws IOException {
        HttpURLConnection connection = buildRequest(target, method);

        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(body);
        writer.flush();
        writer.close();

        InputStream stream = connection.getInputStream();

        try {
            JSONTokener parsing = new JSONTokener(stream);
            return new JSONObject(parsing);
        } catch (JSONException e) {
            return new JSONObject();
        }


    }
}
