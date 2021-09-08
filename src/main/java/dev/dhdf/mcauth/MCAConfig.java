package dev.dhdf.mcauth;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MCAConfig {
  private final JavaPlugin plugin;
  public final String host;
  public final int port;
  public final String authScheme;
  public final String token;
  public final List<String> whitelist; 

  private static final String defaultHost = "127.0.0.1";
  private static final int defaultPort = 8080;
  private static final String defaultAuth = "dependent";
  private static final String defaultToken = "";

  public MCAConfig(JavaPlugin plugin, FileConfiguration file) {
    this.plugin = plugin;
    FileConfiguration config = this.fix(file);
    this.token = config.getString("token", MCAConfig.defaultToken);
    this.host = config.getString("address", MCAConfig.defaultHost);
    this.port = config.getInt("port", MCAConfig.defaultPort);
    this.authScheme = config.getString("auth_scheme", MCAConfig.defaultAuth);
    this.whitelist = config.getStringList("whitelist");
  }

  private FileConfiguration fix(FileConfiguration file) {
    File data = plugin.getDataFolder();
    String target = String.format("%s/config.yml", data.getAbsolutePath());
    Logger log = plugin.getLogger();
    Consumer<String> warnThem = (s) -> {
      String msg = String.format("Please set the %s in %s", s, target);
      log.warning(msg);
    };

    if (!file.contains("token")) {
      file.set("token", MCAConfig.defaultToken);
      warnThem.accept("token");
    }

    if (!file.contains("address")) {
      file.set("address", MCAConfig.defaultHost);
      warnThem.accept("address");
    }

    if (!file.contains("port")) {
      file.set("port", MCAConfig.defaultPort);
      warnThem.accept("port");
    }

    if (!file.contains("auth_scheme")) {
      file.set("auth_scheme", MCAConfig.defaultAuth);
      warnThem.accept("auth_scheme");
    }

    if (!file.contains("whitelist")) {
      ArrayList<String> empty = new ArrayList<>();
      file.set("whitelist", empty);
      warnThem.accept("whitelist");
    }

    plugin.saveConfig();
    return file;
  }
}
