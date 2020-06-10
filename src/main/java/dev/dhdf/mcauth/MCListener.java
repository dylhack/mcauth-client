/**
 * @LICENSE GPL-3.0
 * @author Dylan Hackworth <dhpf@pm.me>
 */
package dev.dhdf.mcauth;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class MCListener implements Listener {
    private final Client client;

    public MCListener(Client client) { this.client = client; }

    /**
     * This handles all the join events it will check if the player joining is
     * authorized to join.
     */
    @EventHandler
    public void onMemberJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();
        try {
            JSONObject isValidRes = this.client.isValidPlayer(player);
            boolean isAuthorized = isValidRes.getBoolean("valid");


            if (!isAuthorized) {
                String kickReason;

                String reason = isValidRes.getString("reason");

                // They didn't link their Discord acc.
                switch (reason) {
                    case "no_link":
                        kickReason = "Please link your Minecraft account via Discord";
                        // They don't have the right perms to join
                        break;
                    case "no_role":
                        kickReason = "To be able to join you must be a Tier 3 Member.";
                        // They're not admin during "maintenance mode"
                        break;
                    case "auth_code":
                        String authCode = isValidRes.getString("auth_code");
                        kickReason = "Here is your auth code: \"" + authCode + "\"";
                        break;
                    case "banned":
                        kickReason = "Your Discord is banned from this Minecraft server";
                        // last is "maintenance"
                        break;
                    default:
                        kickReason = "Floor Gang - The server is currently under maintenance.";
                        break;
                }

                player.kickPlayer(kickReason);
            }
        } catch (IOException err) {
            // Kick if the authentication server is down
            player.kickPlayer("Failed to connect to authentication servers.");
        } catch (JSONException err) {
            // Something went wrong will accessing a response attribute.
            err.printStackTrace();
        }
    }
}
