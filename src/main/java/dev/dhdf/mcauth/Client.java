package dev.dhdf.mcauth;

import org.bukkit.entity.Player;
import org.json.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    public CompletableFuture<HttpResponse<String>> verifyPlayer(Player player) {
        String uuid = player.getUniqueId().toString().replace("-", "");

        return this.doGetRequest(this.baseURL + "/verify/" + uuid);
    }

    /* Alt Account Management */

    /**
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#post-newalt
     * @param owner The owner claiming the alt account
     * @param altName The name of the alt account being claimed
     * @return JSONObject
     */
    public CompletableFuture<HttpResponse<String>> addAltAccount(Player owner, String altName) {
        return this.doRequest(
                this.baseURL + String.format("/alts/%s/%s", owner.getName(), altName),
                "POST"
        );
    }

    /**
     * This deletes a given alt account
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#delete-delalt
     * @param altName The name of the alt account to remove
     * @return JSONObject
     */
    public CompletableFuture<HttpResponse<String>> remAltAccount(String altName) {
        return this.doRequest(
                this.baseURL + "/alts/" + altName,
                "DELETE"
        );
    }

    /**
     * This lists all the alt accounts under a given owner
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#get-getaltsofowner
     * @param owner Alt account owner
     * @return ArrayList<AltAcc>
     * @throws JSONException If the server doesn't recognize the owner
     */
    public CompletableFuture<HttpResponse<String>> getAltsOf(String owner) {
        return this.doGetRequest(this.baseURL + "/alts/" + owner);
    }

    public CompletableFuture<HttpResponse<String>> listAlts() {
        return this.doGetRequest(this.baseURL + "/alts");
    }

    /* Utility Methods */

    /**
     * This handles all the HTTP GET requests
     */
    private CompletableFuture<HttpResponse<String>> doGetRequest(String target) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(target))
                .header("Content-Type", "application/json")
                .header("User-Agent", "Spigot Plugin")
                .header("Authorization", this.token)
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * This does all the HTTP requests that aren't related to GET requests
     * @return JSONObject
     */
    private CompletableFuture<HttpResponse<String>> doRequest(String target, String method) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(target))
                .header("Content-Type", "application/json")
                .header("User-Agent", "Spigot Plugin")
                .header("Authorization", this.token)
                .method(method, HttpRequest.BodyPublishers.ofString(""))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
