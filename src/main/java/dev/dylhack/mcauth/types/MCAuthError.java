package dev.dylhack.mcauth.types;

import java.lang.Exception;
import org.json.JSONObject;

public class MCAuthError extends Exception {
  public final String code;
  public MCAuthError(JSONObject data) {
    super(data.getString("message"));
    String errCode = data.getString("errcode");
    this.code = errCode;
  }
}
