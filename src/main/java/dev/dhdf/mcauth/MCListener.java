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

    @EventHandler
    public void onMemberJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();
        try {
            JSONObject isValidRes = this.client.isValidPlayer(player);

            boolean isValid = isValidRes.getBoolean("valid");


            if (!isValid) {
                String kickReason;

                String reason = isValidRes.getString("reason");
                if (reason.equals("no_link")) {
                    kickReason = "Please link your Minecraft account via Discord";
                } else {
                    kickReason = "To be able to join you must be a Tier 3 Member.";
                }

                player.kickPlayer(kickReason);
            }
        } catch (IOException err) {
            player.kickPlayer("Failed to connect to authentication servers.");
        } catch (JSONException err) {
            err.printStackTrace();
        }
    }
}
