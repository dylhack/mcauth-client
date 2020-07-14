package dev.dhdf.mcauth;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONException;
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
            this.client.isValidPlayer(player)
                    .thenAccept(isValidRes -> {
                        boolean isAuthorized = isValidRes.getBoolean("valid");


                        if (!isAuthorized) {
                            String kickReason;

                            String reason = isValidRes.getString("reason");


                            switch (reason) {
                                // They didn't link their Discord acc.
                                case "no_link" -> kickReason = "Please link your Minecraft account via Discord";
                                // They don't have the right perms to join
                                case "no_role" -> kickReason = "You don't have the right roles to join the server.";
                                // They're not admin during "maintenance mode"
                                case "auth_code" -> {
                                    String authCode = isValidRes.getString("auth_code");
                                    kickReason = "Here is your auth code: \"" + authCode + "\"";
                                }
                                case "banned" -> kickReason = "Your Discord is banned from this Minecraft server";
                                // last is "maintenance mode is on"
                                default -> kickReason = "The server is currently under maintenance.";
                            }

                            player.kickPlayer(kickReason);
                        }
                    });
        } catch (JSONException err) {
            // Something went wrong will accessing a response attribute.
            err.printStackTrace();
        }
    }
}
