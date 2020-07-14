package dev.dhdf.mcauth.types;

/**
 * This represents an alt account
 */
public class AltAcc {
    // The Minecraft player name
    public final String altName;
    // The Minecraft player UUID
    public final String altUUID;
    // The person who claimed this Minecraft account (the owner is a Minecraft player name)
    public final String owner;

    public AltAcc(String altName, String altUUID, String owner) {
        this.altName = altName;
        this.altUUID = altUUID;
        this.owner = owner;
    }
}
