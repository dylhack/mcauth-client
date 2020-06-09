package dev.dhdf.mcauth.types;

public class AltAcc {
    public final String altName;
    public final String altUUID;
    public final String owner;

    public AltAcc(String altName, String altUUID, String owner) {
        this.altName = altName;
        this.altUUID = altUUID;
        this.owner = owner;
    }
}
