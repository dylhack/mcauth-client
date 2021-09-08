package dev.dylhack.mcauth.types;

import java.lang.Exception;
import org.json.JSONObject;

public class MCAException extends Exception {
  public final String code;

  public MCAException(JSONObject data) {
    super(data.getString("message"));
    String errCode = data.getString("errcode");
    this.code = errCode;
  }
}
