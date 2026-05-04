package dev.VoxelTales.Registries.RegistryEnums;

public enum CacheEnum {
    SLOT_CACHE("Slot_Cache"),
    HUD_CACHE("HUD_Cache"),
    WEAPON_CONFIGURATION_PAGE("WeaponConfigurationPage"),
    WEAPON_FORGER_PAGE("WeaponForgerPage"),
    DIALOGUE_PAGE("DialoguePage"),
    VOXEL_PLAYER_WEAPON_PROGRESS_CACHE("VoxelPlayerWeaponProgressCache"),
    VOXEL_PLAYER_DAMAGE_CACHE("VoxelPlayerDamageCache"),
    VOXEL_PLAYER_SCALING_CACHE("VoxelPlayerScalingCache"),
    VOXEL_PLAYER_ATKSPEED_CACHE("VoxelPlayerAttackSpeedCache");

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
