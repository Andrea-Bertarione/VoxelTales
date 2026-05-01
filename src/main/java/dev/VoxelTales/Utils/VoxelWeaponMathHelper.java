package dev.VoxelTales.Utils;

import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelWeaponMathHelper {
    public static int getRequiredXP(int level) {
        VoxelTalesConfigs configs = VoxelTalesConfigs.get();
        return (int) (30 + (configs.getXpBaseValue() * Math.pow(level, configs.getXpExponent())));
    }

    public static float getXPProgress(int currentXP, int level) {
        return ((float) currentXP / getRequiredXP(level));
    }
}
