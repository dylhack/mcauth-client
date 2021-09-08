package dev.dhdf.mcauth;

import org.bukkit.entity.Player;
import org.json.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import dev.dhdf.mcauth.types.*;
import java.time.Duration;

public class Client {
    private final static Duration timeout = Duration.ofSeconds(1);
    private final String baseURL;
    private final String token;
    private final HttpClient client;

    public Client(String address, int port, String token) {
        this.baseURL = String.format("http://%s:%d", address, port);
        this.token = token;
        this.client = HttpClient.newHttpClient();
    }

    /**
     * This checks if the player that joined is authorized to be joining.
     *
     * @param player The player being validated
     * @return JSONObject (see provided link)
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Player%20Validation.md#post-isplayervalid
     */
    public HttpResponse<String> verifyPlayer(Player player) throws IOException, InterruptedException {
        String uuid = player.getUniqueId().toString().replace("-", "");

        return this.doGetRequest(this.baseURL + "/verify/" + uuid);
    }

    /* Alt Account Management */

    /**
     * @param owner   The owner claiming the alt account
     * @param altName The name of the alt account being claimed
     * @return JSONObject
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#post-newalt
     */
    public HttpResponse<String> addAltAccount(Player owner, String altName) throws IOException, InterruptedException {
        return this.doRequest(
                this.baseURL + String.format("/alts/%s/%s", owner.getName(), altName),
                "POST"
        );
    }

    /**
     * This deletes a given alt account
     *
     * @param altName The name of the alt account to remove
     * @return JSONObject
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#delete-delalt
     */
    public HttpResponse<String> remAltAccount(String altName) throws IOException, InterruptedException {
        return this.doRequest(
                this.baseURL + "/alts/" + altName,
                "DELETE"
        );
    }

    /**
     * This lists all the alt accounts under a given owner
     *
     * @param owner Alt account owner
     * @return ArrayList<AltAcc>
     * @throws JSONException If the server doesn't recognize the owner
     * @link https://github.com/dhghf/mc-discord-auth/blob/master/docs/endpoints/Alt%20Accounts.md#get-getaltsofowner
     */
    public HttpResponse<String> getAltsOf(String owner) throws IOException, InterruptedException {
        return this.doGetRequest(this.baseURL + "/alts/" + owner);
    }

    public HttpResponse<String> listAlts() throws IOException, InterruptedException {
        return this.doGetRequest(this.baseURL + "/alts");
    }

    public PlayerDetails getDetails(
        Player player
    ) throws IOException, InterruptedException, MCAuthError {
        String uuid = player.getUniqueId().toString().replace("-", "");
        String target = String.format(
            "%s/details/%s",
            this.baseURL,
            uuid
        );
        HttpResponse<String> resp = this.doGetRequest(target);
        String body = resp.body();
        JSONObject data = new JSONObject(body);

        if (data.has("errcode")) {
            throw new MCAuthError(data);
        }

        return new PlayerDetails(data);
    }

    /* Utility Methods */

    /**
     * This handles all the HTTP GET requests
     */
    private HttpResponse<String> doGetRequest(String target) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(Client.timeout)
                .uri(URI.create(target))
                .header("Content-Type", "application/json")
                .header("User-Agent", "MCAuth Client")
                .header("Authorization", this.token)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * This does all the HTTP requests that aren't related to GET requests
     * @return JSONObject
     */
    private HttpResponse<String> doRequest(String target, String method) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(Client.timeout)
                .uri(URI.create(target))
                .header("Content-Type", "application/json")
                .header("User-Agent", "MCAuth Client")
                .header("Authorization", this.token)
                .method(method, HttpRequest.BodyPublishers.ofString(""))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
