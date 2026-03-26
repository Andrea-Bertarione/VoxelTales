package dev.VoxelTales.Utils;

import dev.VoxelTales.Configs.VoxelWeaponLookup;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.Set;

public class VoxelWeaponConfigsHelper {
    public static void deleteEntry(String type, String name) {
        var wrapper = VoxelTalesPlugin.get().getWeaponLookupConfig();
        VoxelWeaponLookup config = wrapper.get();

        if (type.equals("blades")) {
            config.removeBladeStats(name);
        } else if (type.equals("handles")) {
            config.removeHandleStats(name);
        }

        wrapper.save();
    }

    public static void renameEntry(String type, String name, String newName) {
        var wrapper = VoxelTalesPlugin.get().getWeaponLookupConfig();
        VoxelWeaponLookup config = wrapper.get();

        if (type.equals("blades")) {
            var stats = config.getBladeStats(name);
            config.removeBladeStats(name);
            config.setBladeStats(newName, stats);
        } else if (type.equals("handles")) {
            var stats = config.getHandleStats(name);
            config.removeHandleStats(name);
            config.setHandleStats(newName, stats);
        }

        wrapper.save();
    }

    public static void saveStatsOf(String type, String name, VoxelWeaponLookup.ComponentStats data) {
        var wrapper = VoxelTalesPlugin.get().getWeaponLookupConfig();
        VoxelWeaponLookup config = wrapper.get();

        if (type.equals("blades")) {
            config.setBladeStats(name, data);
        } else if (type.equals("handles")) {
            config.setHandleStats(name, data);
        }

        wrapper.save();
    }

    public static VoxelWeaponLookup.ComponentStats getStatsOf(String type, String name) {
        return type.equals("blades") ? getBladeStats(name) : type.equals("handles") ? getHandleStats(name) : null;
    }

    /**
     * Gets blade stats or generates a default entry if missing.
     */
    public static VoxelWeaponLookup.ComponentStats getBladeStats(String bladeId) {
        var wrapper = VoxelTalesPlugin.get().getWeaponLookupConfig();
        VoxelWeaponLookup config = wrapper.get();

        if (config.getBlades().containsKey(bladeId)) {
            return config.getBlades().get(bladeId);
        }

        // Generate Default
        VoxelWeaponLookup.ComponentStats defaultStats = createDefaultTemplate();
        config.getBlades().put(bladeId, defaultStats);

        // Save the updated map back to the JSON file
        wrapper.save();

        return defaultStats;
    }

    /**
     * Gets handle stats or generates a default entry if missing.
     */
    public static VoxelWeaponLookup.ComponentStats getHandleStats(String handleId) {
        var wrapper = VoxelTalesPlugin.get().getWeaponLookupConfig();
        VoxelWeaponLookup config = wrapper.get();

        if (config.getHandles().containsKey(handleId)) {
            return config.getHandles().get(handleId);
        }

        // Generate Default
        VoxelWeaponLookup.ComponentStats defaultStats = createDefaultTemplate();
        config.getHandles().put(handleId, defaultStats);

        wrapper.save();

        return defaultStats;
    }

    public static Set<String> getListOfNames(String type) {
        var wrapper = VoxelTalesPlugin.get().getWeaponLookupConfig();
        VoxelWeaponLookup config = wrapper.get();

        if (type.equals("blades")) {
            return config.getBlades().keySet();
        } else if (type.equals("handles")) {
            return config.getHandles().keySet();
        }

        return Set.of();
    }

    private static VoxelWeaponLookup.ComponentStats createDefaultTemplate() {
        VoxelWeaponLookup.ComponentStats stats = new VoxelWeaponLookup.ComponentStats();
        stats.getBaseDamage().put("Physical", 5.0f);
        stats.getDamageScaling().put("Strength", 1.0f);
        return stats;
    }
}