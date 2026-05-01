package dev.VoxelTales.Registries.RegistryEnums;

public enum ComponentEnum {
    VOXEL_PLAYER_COMPONENT("VoxelTales:VoxelPlayerComponent"),
    WEAPON_HANDLER_COMPONENT("VoxelTales:WeaponHandlerComponent"),
    COMBAT_TRACKER_COMPONENT("VoxelTales:CombatTrackerComponent"),
    PLAYER_WEAPON_PROGRESS_COMPONENT("VoxelTales:PlayerWeaponProgressComponent"),
    DIALOGUE_STATE_COMPONENT("VoxelTales:DialogueStateComponent");

    private final String name;
    ComponentEnum(String name) {
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
