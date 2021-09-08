package dev.dylhack.mcauth;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.dylhack.mcauth.types.MCAException;
import dev.dylhack.mcauth.types.MCAPlayer;
import dev.dylhack.mcauth.types.PlayerDetails;
import dev.dylhack.mcauth.types.PlayerState;

public class LoginHandler implements Listener {
    private final JavaPlugin plugin;

    private final MCAClient client;

    private final MCAStore store;

    private final MCAConfig config;

    public LoginHandler(
        JavaPlugin plugin,
        MCAConfig config,
        MCAClient client
    ) {
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        this.store = new MCAStore();
    }

    private void kick(PlayerLoginEvent loginEvent, String reason) {
        loginEvent.disallow(PlayerLoginEvent.Result.KICK_OTHER, reason);
    }

    public PlayerState authenticate(PlayerLoginEvent ev, String id) {
        Player player = ev.getPlayer();
        Logger log = this.plugin.getLogger();
        String playerSum = String.format("%s (%s)", player.getName(), id);

        try {
            PlayerDetails details = this.client.getDetails(player);
            boolean whitelisted = details.state.isVerified();

            this.store.setPlayer(player, details);

            if (whitelisted) {
                return details.state;
            }

            return notVerified(ev, details);
        } catch (IOException|InterruptedException|MCAException ex) {
            if (ex instanceof MCAException) {
                MCAException exc = (MCAException) ex;
                log.severe(
                    String.format(
                        "An MCAuth Error occurred while authenticating %s\n"
                        + " code: %s\n"
                        + " message: %s",
                        playerSum,
                        exc.code,
                        exc.getMessage()
                    )
                );
            } else if (ex instanceof IOException) {
                log.severe(
                    String.format(
                        "An IO exception occurred while authenticating %s"
                        + " this is possibly because we can't communiate"
                        + " with the MCAuth server at the moment.",
                        playerSum
                    )
                );
            } else if (ex instanceof InterruptedException) {
                log.severe(
                    String.format(
                        "MCAuth plugin ran out of time while communicating"
                        + " with the MCAuth server. Player in relation to: %s",
                        playerSum
                    )
                );
            }
            log.severe(
                String.format("Utilizing cache for %s for now", playerSum)
            );
            return this.checkStore(ev, id, playerSum);
        }
    }

    public PlayerState checkStore(
        PlayerLoginEvent ev,
        String id,
        String playerSum
    ) {
        Logger log = this.plugin.getLogger();
        Optional<MCAPlayer> plOpt = this.store.getPlayer(id);

        try {
            MCAPlayer player = plOpt.get();
            if (player.getState().isVerified()) {
                return player.getState();
            }

            return notVerified(ev, player);
        } catch (NoSuchElementException ex) {
            log.warning(
                String.format(
                    "Couldn't find user in cache: %s"
                    + " returning no_link for now.",
                    playerSum
                )
            );
            kick(
                ev,
                "Unable to communicate with MCAuth right now, try again later."
            );
            return PlayerState.NOT_LINKED;
        }
    }

    public PlayerState notVerified(
        PlayerLoginEvent ev,
        PlayerDetails details
    ) {
        PlayerState state = details.getState();
        Logger log = this.plugin.getLogger();
        switch (state) {
            case NOT_LINKED:
            case AUTH_CODE:
                kick(
                    ev,
                    String.format(
                        "Here is your auth code: \"%s\"",
                        details.authCode
                    )
                );
                break;
            case NOT_WHITELISTED:
                // if MCAuth is running independent mode let's
                // check if the user has a role that's in the
                // configuration file.
                if (!this.config.authScheme.equals("independent")) {
                    kick(ev, "You're not whitelisted.");
                    break;
                }

                boolean whitelisted = false;
                for (String roleId : details.roles) {
                    if (config.whitelist.contains(roleId)) {
                        whitelisted = true;
                        break;
                    }
                }

                if (!whitelisted) {
                    kick(ev, "You're not whitelisted.");
                }
                break;
            default:
                log.severe(
                    String.format(
                        "unknown player state received %s",
                        state.toValue()
                    )
                );
                break;
        }

        return state;
    }

    /**
     * This handles all the join events it will check if the player joining is
     * authorized to join.
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent ev) {
        Logger log = this.plugin.getLogger();
        String id = MCAPlayer.format(ev.getPlayer());
        PlayerState result = this.authenticate(ev, id);
        log.info(
            String.format("%s: %s", id, result.toValue())
        );
    }
}
