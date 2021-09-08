package dev.dylhack.mcauth;

import java.util.logging.Logger;

import dev.dylhack.mcauth.commands.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MCAuthMain extends JavaPlugin {
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        FileConfiguration file = this.getConfig();
        MCAConfig config = new MCAConfig(this, file);

        MCAClient client = new MCAClient(
            config.host,
            config.port,
            config.token
        );
        LoginHandler listener = new LoginHandler(this, config, client);
        AltExecutor altCommands = new AltExecutor(client);

        // Print out the status of MCAuth
        this.status(config);
        getServer().getPluginManager().registerEvents(listener, this);
        this.getCommand("addalt").setExecutor(altCommands);
        this.getCommand("remalt").setExecutor(altCommands);
        this.getCommand("listalts").setExecutor(altCommands);
        this.getCommand("getalts").setExecutor(altCommands);
    }

    private void status(MCAConfig config) {
        Logger log = this.getLogger();
        String message = String.format(
            "MCAuth Configuration\n"
            + "auth_scheme: %s\n"
            + "host: %s:%d\n"
            + "whitelist:",
            config.authScheme,
            config.host, config.port
        );
        for (String id : config.whitelist) {
            message += "\n";
            message += String.format(" - %s", id);
        }

        log.info(message);
    }
}
