package dev.dhdf.mcauth;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MCAConfig {
  public final String host;
  public final int port;
  public final String authScheme;
  public final String token;
  public final List<String> whitelist; 

  private static final String defaultHost = "127.0.0.1";
  private static final int defaultPort = 8080;
  private static final String defaultAuth = "dependent";
  private static final String defaultToken = "";

  public MCAConfig(FileConfiguration file) {
    FileConfiguration config = MCAConfig.fix(file);
    this.token = config.getString("token", MCAConfig.defaultToken);
    this.host = config.getString("address", MCAConfig.defaultHost);
    this.port = config.getInt("port", MCAConfig.defaultPort);
    this.authScheme = config.getString("auth_scheme", MCAConfig.defaultAuth);
    this.whitelist = config.getStringList("whitelist");
  }

  private static FileConfiguration fix(FileConfiguration file) {
    if (!file.contains("token")) {
      file.set("token", MCAConfig.defaultToken);
    }

    if (!file.contains("address")) {
      file.set("address", MCAConfig.defaultHost);
    }

    if (!file.contains("port")) {
      file.set("port", MCAConfig.defaultPort);
    }

    if (!file.contains("auth_scheme")) {
      file.set("auth_scheme", MCAConfig.defaultAuth);
    }

    if (!file.contains("whitelist")) {
      ArrayList<String> empty = new ArrayList<>();
      file.set("whitelist", empty);
    }

    try {
      file.save("config.yml");
    } catch (IOException err) {
      System.out.println("Failed to save config.yml\n" + err);
    }
    return file;
  }
}
