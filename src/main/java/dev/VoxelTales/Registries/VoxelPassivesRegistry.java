package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import dev.VoxelTales.Assets.Passives.OnHit.Burn;
import dev.VoxelTales.Assets.Passives.VoxelPassive;
import dev.VoxelTales.Interfaces.IVoxelPassiveEffect;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VoxelPassivesRegistry implements IVoxelRegistry {
    private static final Map<String, IVoxelPassiveEffect> PASSIVES = new HashMap<>();

    public static void init(VoxelTalesPlugin plugin) {
        register(new Burn());

        LoggerUtil.getLogger().info("[VoxelPassivesRegistry] Initialized with " + PASSIVES.size() + " passives.");
    }

    private static void register(VoxelPassive passive) { PASSIVES.put(passive.getName(), passive); }

    public static IVoxelPassiveEffect get(String key) { return PASSIVES.get(key); }
    public static Set<String> getRegisteredKeys() { return PASSIVES.keySet(); }
}