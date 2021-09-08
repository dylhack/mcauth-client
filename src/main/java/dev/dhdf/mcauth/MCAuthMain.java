package dev.dhdf.mcauth;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MCAuthMain extends JavaPlugin {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        FileConfiguration file = this.getConfig();
        MCAConfig config = new MCAConfig(this, file);

        Client client = new Client(config.host, config.port, config.token);
        MCListener listener = new MCListener(config, client);
        AltCommands altCommands = new AltCommands(client);

        getServer().getPluginManager().registerEvents(listener, this);
        this.getCommand("addalt").setExecutor(altCommands);
        this.getCommand("remalt").setExecutor(altCommands);
        this.getCommand("listalts").setExecutor(altCommands);
        this.getCommand("getalts").setExecutor(altCommands);
    }
}
