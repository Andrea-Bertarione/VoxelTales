package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.VoxelTalesPlugin;
import irai.mod.DynamicFloatingDamageFormatter.DamageNumbers;

//This class is kept for compatibility with existing mods and should not need to be modified.
public class VoxelDamageKindsRegistry implements IVoxelRegistry {
    private static final String PHYSICAL_KIND = "VOXEL_PHYSICAL";
    private static final String FIRE_KIND = "VOXEL_FIRE";

    private static final String UI_PREFIX = "VoxelTales_CombatText_";

    static short kindCount = 0;

    public static void init(VoxelTalesPlugin plugin) {
        newKind(PHYSICAL_KIND);
        newKind(FIRE_KIND);

        LoggerUtil.getLogger().info("[VoxelDamageKindRegistry] Registered " + kindCount + " damage kinds.");
    }

    private static void newKind(String kind) {
        DamageNumbers.kind(kind)
                .format("{amount}")
                .ui(UI_PREFIX + kind.replace("VOXEL_", "").toLowerCase())
                .precision(2)
                .register();

        kindCount++;
    }
}
