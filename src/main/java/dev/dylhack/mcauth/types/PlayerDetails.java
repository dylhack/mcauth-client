package dev.dylhack.mcauth.types;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class PlayerDetails {
  public final String authCode;
  public final String state;
  public final ArrayList<String> roles;

  public PlayerDetails(JSONObject data) {
    this.state = data.getString("state");
    this.roles = new ArrayList<String>();
    JSONArray roleArr = data.getJSONArray("roles");

    for (int i = 0; i < roleArr.length(); i++) {
      String role = roleArr.getString(i);
      this.roles.add(role);
    }

    if (data.has("auth_code")) {
      this.authCode = data.getString("auth_code");
    } else {
      this.authCode = "";
    }
  }
}
