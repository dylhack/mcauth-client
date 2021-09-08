package dev.dylhack.mcauth;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.bukkit.entity.Player;
import dev.dylhack.mcauth.types.MCAPlayer;
import dev.dylhack.mcauth.types.PlayerDetails;

/**
 * MCAuth Store will be used as backup storage
 * for authentication just incase the plugin
 * can not communicate with the server.
 */
public class MCAStore {
  private final HashMap<String, MCAPlayer> players;

  public MCAStore() {
    this.players = new HashMap<String, MCAPlayer>();
  }

  public boolean verify(String id) {
    Optional<MCAPlayer> playerOpt = this.getPlayer(id);

    try {
      MCAPlayer player = playerOpt.get();
      return player.getState().isVerified();
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  public boolean verify(Player pl) {
    String id = MCAPlayer.format(pl);
    return this.verify(id);
  }

  public Optional<MCAPlayer> getPlayer(Player pl) {
    String id = MCAPlayer.format(pl);
    return this.getPlayer(id);
  }

  public Optional<MCAPlayer> getPlayer(String id) {
    MCAPlayer player = this.players.get(id);

    if (player == null) {
      return Optional.empty();
    }
    return Optional.of(player);
  }

  public void setPlayer(MCAPlayer player) {
    String id = player.getId();
    this.players.put(id, player);
  }

  public void setPlayer(Player pl, PlayerDetails details) {
    MCAPlayer player = new MCAPlayer(pl, details);
    String id = player.getId();
    this.players.put(id, player);
  }
}
