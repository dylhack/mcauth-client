package dev.dhdf.mcauth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;
import org.json.JSONObject;

public class MCListener implements Listener {
    private final Client client;
    private final Plugin plugin;

    public MCListener(Client client, Plugin plugin) {
        this.client = client;
        this.plugin = plugin;
    }

    private void kick(Player player, String reason) {
        Bukkit.getScheduler().runTaskLater(this.plugin, new KickTask(
                reason,
                player
        ), 20);
    }

    /**
     * This handles all the join events it will check if the player joining is
     * authorized to join.
     */
    @EventHandler
    public void onMemberJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();
        try {
            this.client.verifyPlayer(player)
                    .thenAcceptAsync(response -> {
                        String kickReason = "";
                        try {
                            String body = response.body();
                            JSONObject isValidRes = new JSONObject(body);
                            boolean isAuthorized = isValidRes.getBoolean("valid");

                            if (!isAuthorized) {
                                String reason = isValidRes.getString("reason");

                                switch (reason) {
                                    // They didn't link their Discord acc.
                                    case "no_link":
                                        kickReason = "Please link your Minecraft account via Discord";
                                        break;
                                    // They don't have the right perms to join
                                    case "no_role":
                                        kickReason = "You don't have the right roles to join the server.";
                                        break;
                                    // They're not admin during "maintenance mode"
                                    case "auth_code":
                                        String authCode = isValidRes.getString("auth_code");
                                        kickReason = "Here is your auth code: \"" + authCode + "\"";
                                        break;
                                    case "banned":
                                        kickReason = "Your Discord is banned from this Minecraft server";
                                        break;
                                    // last is "maintenance mode is on"
                                    default:
                                        kickReason = "The server is currently under maintenance.";
                                }
                            }
                        } catch (JSONException err) {
                            kickReason = "Failed to communicate to mcauth, try again later.";
                            err.printStackTrace();
                        }
                        kick(player, kickReason);
                    }).join();
        } catch (Exception e) {
            kick(player, "Failed to communicate to mcauth, try again later.");
            System.out.println("Failed to communicate with mcauth, is the configuration correct?");
            e.printStackTrace();
        }
    }
}
