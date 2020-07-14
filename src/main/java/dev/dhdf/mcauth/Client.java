package dev.dhdf.mcauth;

import dev.dhdf.mcauth.types.AltAcc;
import org.bukkit.entity.Player;
import org.json.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Client {
    private final String baseURL;
    private final String token;
    private final HttpClient client;

    public Client(String address, int port, String token) {
        this.baseURL = "http://" + address + ":" + port;
        this.token = token;
        this.client = HttpClient.newHttpClient();
    }

    /**
     * This checks if the player that joined is authorized to be joining.
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Player%20Validation.md#post-isplayervalid
     * @param player The player being validated
     * @return JSONObject (see provided link)
     */
    public CompletableFuture<JSONObject> isValidPlayer(Player player) {
        String uuid = player.getUniqueId().toString().replace("-", "");

        return this.doGetRequest(this.baseURL + "/isValidPlayer/" + uuid);
    }

    /////////////////////////
    // Alt Account Management
    /////////////////////////

    /**
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#post-newalt
     * @param owner The owner claiming the alt account
     * @param altName The name of the alt account being claimed
     * @return JSONObject
     */
    public CompletableFuture<JSONObject> addAltAccount(Player owner, String altName) {
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
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#delete-delalt
     * @param altName The name of the alt account to remove
     * @return JSONObject
     */
    public CompletableFuture<JSONObject> remAltAccount(String altName) {
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
     * This lists all the alt accounts under a given owner
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#get-getaltsofowner
     * @param owner Alt account owner
     * @return ArrayList<AltAcc>
     * @throws JSONException If the server doesn't recognize the owner
     */
    public CompletableFuture<ArrayList<AltAcc>> listAltAccounts(String owner) throws JSONException {
        return this.doGetRequest(this.baseURL + "/getAltsOf/" + owner)
                .thenApply(result -> {
                    ArrayList<AltAcc> accounts = new ArrayList<>();
                    JSONArray alts = result.getJSONArray("alt_accs");

                    for (int i = 0; i < alts.length(); ++i) {
                        JSONObject rawAltAcc = (JSONObject) alts.get(i);
                        String altName = rawAltAcc.getString("alt_name");
                        String altUUID = rawAltAcc.getString("alt_id");
                        AltAcc altAcc = new AltAcc(altName, altUUID, owner);

                        accounts.add(altAcc);
                    }

                    return accounts;
                });

    }


    ///////////////////
    // Utility Methods
    ///////////////////



    /**
     * This handles all the HTTP GET requests
     */
    private CompletableFuture<JSONObject> doGetRequest(String target) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(target))
                .header("Content-Type", "application/json")
                .header("User-Agent", "Spigot Plugin")
                .header("Authorization", "Bearer " + this.token)
                .method("GET", null)
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new JSONObject(response.body()));
    }

    /**
     * This does all the HTTP requests that aren't related to GET requests
     * @return JSONObject
     */
    private CompletableFuture<JSONObject> doRequest(String target, String method, String body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(target))
                .header("Content-Type", "application/json")
                .header("User-Agent", "Spigot Plugin")
                .header("Authorization", "Bearer " + this.token)
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> new JSONObject(response.body()));
    }
}
