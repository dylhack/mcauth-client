package dev.dhdf.mcauth;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        this.saveDefaultConfig();;

        FileConfiguration config = this.getConfig();

        String address = config.getString("address", "127.0.0.1");
        String token = config.getString("token", "");
        int port = config.getInt("port", 3001);

        Client client = new Client(address, port, token);
        MCListener listener = new MCListener(client);

        getServer().getPluginManager().registerEvents(listener, this);
    }
}
