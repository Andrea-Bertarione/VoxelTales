package dev.VoxelTales.Assets.Gameplay;

public enum WeaponComponentWeight {
    LIGHT("Small"),
    MEDIUM("Medium"),
    HEAVY("Heavy");

    private final String displayName;

    public String getDisplayName() {
        return this.displayName;
    }

    WeaponComponentWeight(String displayName) {
        this.displayName = displayName;
    }
}
