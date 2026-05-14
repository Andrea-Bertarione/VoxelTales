package dev.VoxelTales.Assets.Gameplay;

public enum WeaponComponentWeight {
    LIGHT("Small",  "Light"),
    MEDIUM("Medium","Medium"),
    HEAVY("Heavy",  "Heavy");

    private final String configName; // what lives in JSON / assets
    private final String uiLabel;    // what you show in the UI

    WeaponComponentWeight(String configName, String uiLabel) {
        this.configName = configName;
        this.uiLabel = uiLabel;
    }

    public String getConfigName() {
        return configName;
    }

    public String getUiLabel() {
        return uiLabel;
    }

    // For decoding existing JSON values like "Small"
    public static WeaponComponentWeight fromConfig(String value) {
        for (WeaponComponentWeight weight : values()) {
            if (weight.configName.equalsIgnoreCase(value)) {
                return weight;
            }
        }
        throw new IllegalArgumentException("Unknown WeaponComponentWeight config name: " + value);
    }

    // Optional: if you ever want to map from UI label back
    public static WeaponComponentWeight fromUiLabel(String label) {
        for (WeaponComponentWeight weight : values()) {
            if (weight.uiLabel.equalsIgnoreCase(label)) {
                return weight;
            }
        }
        throw new IllegalArgumentException("Unknown WeaponComponentWeight UI label: " + label);
    }
}