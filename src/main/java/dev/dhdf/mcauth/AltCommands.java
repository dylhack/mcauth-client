 /**
 * @LICENSE GPL-3.0
 * @author Dylan Hackworth <dhpf@pm.me>
 */
package dev.dhdf.mcauth;

import dev.dhdf.mcauth.types.AltAcc;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;

public class AltCommands implements CommandExecutor {
    private static final String permComplaint = "You do not have permissions to run this command.";

    private final Client client;

    public AltCommands(Client client) {
        this.client = client;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (label) {
            case "addalt":
                return this.addAlt(sender, args);
            case "remalt":
                return this.remAlt(sender, args);
            case "listalts":
                return this.listAlts(sender, args);
            default:
                return false;
        }
    }

    /**
     * Add a given alt account
     */
    private boolean addAlt(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mc-discord-auth.addalt")) {
            sender.sendMessage(permComplaint);
            return true;
        }
        Player claimer = (Player) sender;

        if (args.length > 0) {
            String altAcc = args[0];
            try {
                this.client.addAltAccount(claimer, altAcc);
                sender.sendMessage("Added account.");
                return true;
            } catch (IOException err) {
                sender.sendMessage("Failed to add alt account, is that a valid player name?");
                return false;
            }
        } else {
            sender.sendMessage("Please provide a player name");
            return false;
        }
    }

    /**
     * Remove a given alt account
     */
    private boolean remAlt(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mc-discord-auth.remalt")) {
            sender.sendMessage(permComplaint);
            return true;
        }

        if (args.length > 0) {
            String altName = args[0];

            try {
                this.client.remAltAccount(altName);
                sender.sendMessage("Removed account.");
            } catch (IOException err) {
                sender.sendMessage("Failed to remove account, is the auth server up?");
            }

            return true;
        } else {
            sender.sendMessage("Please provide a player name");
            return false;
        }
    }

    /**
     * List alts of another player
     */
    private boolean listAlts(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mc-discord-auth.listalts")) {
            sender.sendMessage(permComplaint);
            return true;
        }

        if (args.length > 0) {
            String owner = args[0];

            try {
                ArrayList<AltAcc> alts = client.listAltAccounts(owner);
                StringBuilder list = new StringBuilder(owner + "'s claimed alts:\n");

                for (AltAcc acc : alts) {
                    list.append(" - ").append(acc.altName).append("\n");
                }

                sender.sendMessage(list.toString());
            } catch (IOException err) {
                sender.sendMessage("Something went wrong");
            }
            return true;
        } else {
            return false;
        }
    }

}
