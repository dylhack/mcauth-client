package dev.dhdf.mcauth;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONObject;

public class MCListener implements Listener {
    private final Client client;

    public MCListener(Client client) { this.client = client; }

    @EventHandler
    public void onMemberJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();
        JSONObject isValidRes = this.client.getIsValid(player);

        boolean isValid = isValidRes.getBoolean("valid");
        String reason = isValidRes.getString("reason");

        if (!isValid) {
            String kickReason;

            if (reason.equals("no_link")) {
                kickReason = "Please link your Minecraft account via Discord";
            } else {
                kickReason = "To be able to join you must be a Tier 3 Member.";
            }

            player.kickPlayer(kickReason);
        }

    }
}
