package dev.dhdf.mcauth;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.json.JSONException;
import org.json.JSONObject;
import dev.dhdf.mcauth.types.*;

import java.net.http.HttpResponse;

public class MCListener implements Listener {
    private final Client client;

    private final MCAConfig config;

    public MCListener(MCAConfig config, Client client) {
        this.client = client;
        this.config = config;
    }

    private void kick(PlayerLoginEvent loginEvent, String reason) {
        loginEvent.disallow(PlayerLoginEvent.Result.KICK_OTHER, reason);
    }

    public void independent(PlayerLoginEvent ev) {
        Player player = ev.getPlayer();
        try {
            PlayerDetails details = this.client.getDetails(player);
            boolean whitelisted = false;

            switch (details.state) {
                // They're an administrator
                case "admin":
                    return;
                // MCAuth Server thinks they're whitelisted
                case "whitelisted":
                    return;
                // They're an alternative account of an administrator
                case "alt_acc":
                    return;
                // They need to link their account
                case "auth_code":
                    kick(
                        ev,
                        String.format(
                            "Here is your auth code: \"%s\"",
                            details.authCode
                        )
                    );
                    return;
            }

            for (String roleId : details.roles) {
                if (config.whitelist.contains(roleId)) {
                    whitelisted = true;
                    break;
                }
            }

            if (!whitelisted) {
                kick(ev, "You're not whitelisted.");
            }
        } catch (Exception err) {
            System.out.println("Error " + err.getMessage());
        }
    }

    public void dependent(PlayerLoginEvent ev) {
        Player player = ev.getPlayer();
        try {
            HttpResponse<String> response = this.client.verifyPlayer(player);
            String body = response.body();
            JSONObject isValidRes = new JSONObject(body);
            boolean isAuthorized = isValidRes.getBoolean("valid");

            if (!isAuthorized) {
                String reason = isValidRes.getString("reason");

                switch (reason) {
                    // They didn't link their Discord acc.
                    case "no_link":
                        kick(ev, "Please link your Minecraft account via Discord");
                        break;
                    // They don't have the right perms to join
                    case "no_role":
                        kick(ev, "You don't have the right roles to join the server.");
                        break;
                    // They're not admin during "maintenance mode"
                    case "auth_code":
                        String authCode = isValidRes.getString("auth_code");
                        kick(ev, "Here is your auth code: \"" + authCode + "\"");
                        break;
                    case "banned":
                        kick(ev, "Your Discord is banned from this Minecraft server");
                        break;
                    // last is "maintenance mode is on"
                    default:
                        kick(ev, "The server is currently under maintenance.");
                }
            }
        } catch (JSONException err) {
            kick(ev, "Failed to communicate to mcauth, try again later.");
            err.printStackTrace();
        } catch (Exception e) {
            kick(ev, "Failed to communicate to mcauth, try again later.");
            System.out.println("Failed to communicate with mcauth, is the configuration correct?");
            e.printStackTrace();
        }
    }

    /**
     * This handles all the join events it will check if the player joining is
     * authorized to join.
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent ev) {
        if (this.config.authScheme == "dependent") {
            this.dependent(ev);
        } else {
            this.independent(ev);
        }
    }
}
