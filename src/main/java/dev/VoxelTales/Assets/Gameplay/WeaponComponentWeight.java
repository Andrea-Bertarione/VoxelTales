package dev.VoxelTales.Assets.Gameplay;

public enum WeaponComponentWeight {
    LIGHT("Small"),
    MEDIUM("Medium"),
    HEAVY("Heavy");

    private final String displayName;

    public String getDisplayName() {
        return this.displayName;
    }

    public static WeaponComponentWeight fromDisplay(String displayName) {
        for (WeaponComponentWeight weight : values()) {
            if (weight.displayName.equalsIgnoreCase(displayName)) {
                return weight;
            }
        }
        throw new IllegalArgumentException("Unknown WeaponComponentWeight display name: " + displayName);
    }

    WeaponComponentWeight(String displayName) {
        this.displayName = displayName;
    }
}
