package vertgreen.util;

public enum DistributionEnum {
    MAIN("production", false),
    MUSIC("music", false),
    DEVELOPMENT("beta", true),
    PATRON("patron", true);

    private final String id;
    private final boolean volumeSupported;

    DistributionEnum(String id, boolean volumeSupported) {
        this.id = id;
        this.volumeSupported = volumeSupported;
    }

    public String getId() {
        return id;
    }

    public boolean volumeSupported() {
        return volumeSupported;
    }
}
