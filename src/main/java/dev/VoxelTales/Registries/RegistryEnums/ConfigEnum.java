package dev.VoxelTales.Registries.RegistryEnums;

public enum ConfigEnum {
    VOXELTALES_GENERAL_CONFIGS("VoxelTales_GeneralConfigs"),
    ENTITY_XP_CONFIGS("VoxelTales_EntityXPConfigs"),
    WEAPON_LOOKUP_CONFIGS("VoxelTales_WeaponLookupConfigs");

    private final String name;

    ConfigEnum(String name) {
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
