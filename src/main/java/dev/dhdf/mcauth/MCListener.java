package dev.dhdf.mcauth;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MCListener implements Listener {
    private final Client client;

    public MCListener(Client client) { this.client = client; }

    @EventHandler
    public void onMemberJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();
        boolean isValid = this.client.isValid(player);

        if (!isValid) {
            player.kickPlayer("Please authenticate your Minecraft account via Discord.");
        }
    }
}
