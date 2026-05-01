package dev.VoxelTales.Registries.RegistryEnums;

public enum CacheEnum {
    SLOT_CACHE("Slot_Cache"),
    HUD_CACHE("HUD_Cache"),
    WeaponConfigurationPage("WeaponConfigurationPage"),
    WeaponForgerPage("WeaponForgerPage"),
    DialoguePage("DialoguePage"),
    VoxelPlayerWeaponProgressCache("VoxelPlayerWeaponProgressCache");


    private final String name;

    CacheEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
