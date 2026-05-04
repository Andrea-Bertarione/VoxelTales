package dev.VoxelTales.Utils;

import dev.VoxelTales.Configs.VoxelWeaponConfigs;
import dev.VoxelTales.Registries.VoxelConfigsRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.Set;

public class VoxelWeaponConfigsHelper {
    private static final String DEFAULT_DAMAGE_SCALING = "Physical";
    private static final String DEFAULT_BASE_DAMAGE = "Physical";

    private static final float DEFAULT_DAMAGE_SCALING_VALUE = 1f;
    private static final float DEFAULT_BASE_DAMAGE_VALUE = 1f;

    public enum Type {
        BLADES, HANDLES
    }

    public static void deleteEntry(Type type, String name) {
        VoxelWeaponConfigs config = VoxelWeaponConfigs.get();

        if (type == Type.BLADES) {
            config.removeBladeStats(name);
        } else if (type == Type.HANDLES) {
            config.removeHandleStats(name);
        }

        VoxelConfigsRegistry.staticSave(VoxelWeaponConfigs.class);
    }

    public static void renameEntry(Type type, String name, String newName) {
        VoxelWeaponConfigs config = VoxelWeaponConfigs.get();

        if (type == Type.BLADES) {
            var stats = config.getBladeStats(name);
            config.removeBladeStats(name);
            config.setBladeStats(newName, stats);
        } else if (type == Type.HANDLES) {
            var stats = config.getHandleStats(name);
            config.removeHandleStats(name);
            config.setHandleStats(newName, stats);
        }

        VoxelConfigsRegistry.staticSave(VoxelWeaponConfigs.class);
    }

    public static void saveStatsOf(Type type, String name, VoxelWeaponConfigs.ComponentStats data) {
        VoxelWeaponConfigs config = VoxelWeaponConfigs.get();

        if (type == Type.BLADES) {
            config.setBladeStats(name, data);
        } else if (type == Type.HANDLES) {
            config.setHandleStats(name, data);
        }

        VoxelConfigsRegistry.staticSave(VoxelWeaponConfigs.class);
    }

    public static VoxelWeaponConfigs.ComponentStats getStatsOf(Type type, String name) {
        return type == Type.BLADES ? getBladeStats(name) : type == Type.HANDLES ? getHandleStats(name) : null;
    }

    /**
     * Gets blade stats or generates a default entry if missing.
     */
    public static VoxelWeaponConfigs.ComponentStats getBladeStats(String bladeId) {
        VoxelWeaponConfigs config = VoxelWeaponConfigs.get();

        if (config.getBlades().containsKey(bladeId)) {
            return config.getBlades().get(bladeId);
        }

        // Generate Default
        VoxelWeaponConfigs.ComponentStats defaultStats = createDefaultTemplate();
        config.getBlades().put(bladeId, defaultStats);

        // Save the updated map back to the JSON file
        VoxelConfigsRegistry.staticSave(VoxelWeaponConfigs.class);

        return defaultStats;
    }

    /**
     * Gets handle stats or generates a default entry if missing.
     */
    public static VoxelWeaponConfigs.ComponentStats getHandleStats(String handleId) {
        VoxelWeaponConfigs config = VoxelWeaponConfigs.get();

        if (config.getHandles().containsKey(handleId)) {
            return config.getHandles().get(handleId);
        }

        // Generate Default
        VoxelWeaponConfigs.ComponentStats defaultStats = createDefaultTemplate();
        config.getHandles().put(handleId, defaultStats);

        VoxelConfigsRegistry.staticSave(VoxelWeaponConfigs.class);

        return defaultStats;
    }

    public static Set<String> getListOfNames(Type type) {
        VoxelWeaponConfigs config = VoxelWeaponConfigs.get();

        if (type == Type.BLADES) {
            return config.getBlades().keySet();
        } else if (type == Type.HANDLES) {
            return config.getHandles().keySet();
        }

        return Set.of();
    }

    private static VoxelWeaponConfigs.ComponentStats createDefaultTemplate() {
        VoxelWeaponConfigs.ComponentStats stats = new VoxelWeaponConfigs.ComponentStats();
        stats.getBaseDamage().put(DEFAULT_BASE_DAMAGE, DEFAULT_BASE_DAMAGE_VALUE);
        stats.getDamageScaling().put(DEFAULT_DAMAGE_SCALING, DEFAULT_DAMAGE_SCALING_VALUE);
        return stats;
    }
}