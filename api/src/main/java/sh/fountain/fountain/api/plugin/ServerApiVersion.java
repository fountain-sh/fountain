package sh.fountain.fountain.api.plugin;

public enum ServerApiVersion {
    v1_13("1.13"),
    v1_14("1.14"),
    v1_15("1.15"),
    v1_16("1.16"),
    v1_17("1.17"),
    v1_18("1.18"),
    v1_19("1.19"),
    v1_20("1.20");

    private final String version;

    private ServerApiVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return this.version;
    }
}
