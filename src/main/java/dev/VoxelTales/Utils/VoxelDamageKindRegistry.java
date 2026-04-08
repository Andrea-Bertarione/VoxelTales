package dev.VoxelTales.Utils;

import irai.mod.DynamicFloatingDamageFormatter.DamageNumbers;

public class VoxelDamageKindRegistry {
    public static void registerDamageKinds() {
        DamageNumbers.kind("Voxel_Physical")
                .format("{amount}")
                .ui("VoxelTales_CombatText_Physical")
                .color("#FFFFFF")
                .precision(2)
                .dot(true)
                .register();

        DamageNumbers.kind("Voxel_Fire")
                .format("{amount}")
                .ui("VoxelTales_CombatText_Fire")
                .color("#FFAA00")
                .precision(2)
                .dot(true)
                .register();


    }
}
