package dev.dhdf.mcauth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            case "getalts":
                return this.getAlts(sender, args);
            case "listalts":
                return this.listAlts(sender);
            default:
                return false;
        }
    }

    /**
     * Add a given alt account
     */
    private boolean addAlt(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mcauth.addalt") && !Main.debug) {
            sender.sendMessage(permComplaint);
            return true;
        }
        Player claimer = (Player) sender;

        if (args.length > 0) {
            String altAcc = args[0];
            try {
                this.client.addAltAccount(claimer, altAcc)
                        .thenAcceptAsync(response -> {
                            System.out.println(response.body());
                            try {
                                JSONObject error = new JSONObject(response.body());
                                String errorMessage = error.getString("message");

                                sender.sendMessage(errorMessage);
                            } catch (JSONException e) {
                                sender.sendMessage("Added account.");
                            }
                        })
                        .join();
            } catch (Exception err) {
                sender.sendMessage("Failed to tell mcauth to add that account.");
                System.out.println("Failed to communicate with mcauth, is the configuration correct?");
                err.printStackTrace();
            }
            return true;
        } else {
            sender.sendMessage("Please provide a player name");
            return false;
        }
    }

    /**
     * Remove a given alt account
     */
    private boolean remAlt(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mcauth.remalt") && !Main.debug) {
            sender.sendMessage(permComplaint);
            return true;
        }

        if (args.length > 0) {
            String altName = args[0];

            try {
                this.client.remAltAccount(altName)
                        .thenAcceptAsync(response -> {
                            try {
                                JSONObject error = new JSONObject(response.body());
                                String errorMessage = error.getString("message");

                                sender.sendMessage(errorMessage);
                            } catch (JSONException e) {
                                sender.sendMessage("Removed account.");
                            }
                        })
                        .join();
            } catch (Exception err) {
                sender.sendMessage("Failed to tell mcauth to remove that account.");
                System.out.println("Failed to communicate with mcauth, is the configuration correct?");
                err.printStackTrace();
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
    private boolean getAlts(CommandSender sender, String[] args) {
        if (!sender.hasPermission("mcauth.getalts") && !Main.debug) {
            sender.sendMessage(permComplaint);
            return true;
        }

        if (args.length > 0) {
            String owner = args[0];

            try {
                client.getAltsOf(owner)
                        .thenAcceptAsync(response -> {
                            JSONObject result = new JSONObject(response.body());
                            if (result.has("errcode")) {
                                String errorMessage = result.getString("message");
                                sender.sendMessage(errorMessage);
                                return;
                            }

                            JSONArray alts = result.getJSONArray("alt_accs");
                            StringBuilder list = new StringBuilder(owner + "'s claimed alts:\n");

                            for (int i = 0; i < alts.length(); ++i) {
                                JSONObject rawAltAcc = (JSONObject) alts.get(i);
                                String altName = rawAltAcc.getString("alt_name");

                                list.append(" - ").append(altName).append("\n");
                            }

                            sender.sendMessage(list.toString());
                        }).join();
            } catch (Exception e) {
                sender.sendMessage("Failed to communicate with mcauth to list the alt accounts.");
                System.out.println("Failed to communicate with mcauth, is the configuration correct?");
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean listAlts(CommandSender sender) {
        if (!sender.hasPermission("mcauth.listalts") && !Main.debug) {
            sender.sendMessage(permComplaint);
            return true;
        }

        try {
            client.listAlts()
                    .thenAcceptAsync(response -> {
                        JSONObject result = new JSONObject(response.body());
                        if (result.has("errcode")) {
                            String errorMessage = result.getString("message");
                            sender.sendMessage(errorMessage);
                            return;
                        }

                        JSONArray alts = result.getJSONArray("alt_accs");
                        StringBuilder list = new StringBuilder("Alt Accounts:\n");

                        for (int i = 0; i < alts.length(); ++i) {
                            JSONObject rawAltAcc = (JSONObject) alts.get(i);
                            String altName = rawAltAcc.getString("alt_name");
                            String ownerUUID = rawAltAcc.getString("alt_owner");

                            list.append(
                                    String.format(" - %s (owner: %s)", altName, ownerUUID)
                            ).append("\n");
                        }

                        sender.sendMessage(list.toString());
                    }).join();
        } catch (Exception e) {
            sender.sendMessage("Failed to communicate with mcauth to get all the accounts.");
            System.out.println("Failed to communicate with mcauth, is the configuration correct?");
            e.printStackTrace();
        }
        return true;
    }

}
