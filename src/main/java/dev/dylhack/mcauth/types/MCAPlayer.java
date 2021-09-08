package dev.dylhack.mcauth.types;

import java.time.Instant;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.json.JSONObject;

public class MCAPlayer extends PlayerDetails {
  private final String id;

  private final long expires;

  private static final long expireOffset = 60;

  public MCAPlayer(Player pl, JSONObject data) {
    super(data);
    this.id = MCAPlayer.format(pl);
    this.expires = Instant.now().getEpochSecond() + expireOffset;
  }

  public MCAPlayer(Player pl, PlayerDetails details) {
    super(details);
    this.id = MCAPlayer.format(pl);
    this.expires = Instant.now().getEpochSecond() + expireOffset;
  }

  public String getId() {
    return this.id;
  }

  public boolean isExpired() {
    long now = Instant.now().getEpochSecond();
    return now >= expires;
  }

  public static String format(UUID id) {
    return id.toString().replace("-", "");
  }

  public static String format(Player pl) {
    return MCAPlayer.format(pl.getUniqueId());
  }
}
