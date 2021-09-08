package dev.dylhack.mcauth.types;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class PlayerDetails {
  public final String discordId;
  public final String authCode;
  public final PlayerState state;
  public final ArrayList<String> roles;

  public PlayerDetails(PlayerDetails details) {
    this.discordId = details.discordId;
    this.authCode = details.authCode;
    this.state = details.state;
    this.roles = details.roles;
  }

  public PlayerDetails(JSONObject data) {
    this.state = PlayerState.fromValue(data.getString("state"));

    // Add roles
    this.roles = new ArrayList<String>();
    JSONArray roleArr = data.getJSONArray("roles");

    for (int i = 0; i < roleArr.length(); i++) {
      String role = roleArr.getString(i);
      this.roles.add(role);
    }

    // Add auth_code
    if (data.has("auth_code")) {
      this.authCode = data.getString("auth_code");
    } else {
      this.authCode = "";
    }

    // Add id
    if (data.has("id")) {
      this.discordId = data.getString("id");
    } else {
      this.discordId = "";
    }
  }

  public String getDiscordId() {
    return this.discordId;
  }

  public PlayerState getState() {
    return this.state;
  }

  public ArrayList<String> getRoles() {
    return this.roles;
  }

  public String getAuthCode() {
    return this.authCode;
  }
}
