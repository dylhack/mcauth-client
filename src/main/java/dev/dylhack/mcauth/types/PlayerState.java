package dev.dylhack.mcauth.types;

/**
 * https://github.com/dylhack/mcauth/blob/production/docs/endpoints/Player%20Details.md#state
 */
public enum PlayerState {
  WHITELISTED("whitelisted"),
  ADMIN("admin"),
  ALT("alt_acc"),
  NOT_LINKED("no_link"),
  NOT_WHITELISTED("no_role"),
  AUTH_CODE("auth_code"),
  UNKNOWN("unknown");
  private final String str;

  private PlayerState(String str) {
    this.str = str;
  }

  public String toValue() {
    return PlayerState.toValue(this);
  }

  public boolean isVerified() {
    return this == PlayerState.WHITELISTED
        || this == PlayerState.ADMIN
        || this == PlayerState.ALT;
  }

  public static PlayerState fromValue(String state) {
    switch (state) {
      case "whitelisted":
        return WHITELISTED;
      case "admin":
        return ADMIN;
      case "alt_acc":
        return ALT;
      case "no_link":
        return NOT_LINKED;
      case "no_role":
        return NOT_WHITELISTED;
      case "auth_code":
        return AUTH_CODE;
      default:
        return UNKNOWN;
    }
  }

  public static String toValue(PlayerState state) {
    return state.str;
  }
}
