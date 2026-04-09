package dev.VoxelTales.Utils;

import irai.mod.DynamicFloatingDamageFormatter.DamageNumbers;

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
