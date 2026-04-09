package dev.VoxelTales.Registries;

import irai.mod.DynamicFloatingDamageFormatter.DamageNumbers;

//This class is kept for compatibility with existing mods and should not need to be modified.
public class VoxelDamageKindRegistry {
    public static void registerDamageKinds() {
        DamageNumbers.kind("VOXEL_PHYSICAL")
                .format("{amount}")
                .ui("VoxelTales_CombatText_Physical")
                .dot(true)
                .register();

        DamageNumbers.kind("VOXEL_FIRE")
                .format("{amount}")
                .ui("VoxelTales_CombatText_Fire")
                .precision(2)
                .register();


    }
}
