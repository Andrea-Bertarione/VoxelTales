package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import dev.VoxelTales.Assets.Passives.OnHit.Burn;
import dev.VoxelTales.Assets.Passives.VoxelPassive;
import dev.VoxelTales.Core.Interfaces.IVoxelPassiveEffect;
import dev.VoxelTales.Core.AVoxelRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VoxelPassivesRegistry extends AVoxelRegistry<VoxelPassive> {
    private final Map<String, IVoxelPassiveEffect> PASSIVES = new ConcurrentHashMap<>();

    private static VoxelPassivesRegistry INSTANCE;

    public void init(VoxelTalesPlugin plugin) {
        INSTANCE = this;

        register(new Burn());

        LoggerUtil.getLogger().info("[VoxelPassivesRegistry] Initialized with " + super.getRegistryCount() + " passives.");
    }

    private void register(VoxelPassive passive) {
        PASSIVES.put(passive.getName(), passive);
        super.incrementRegistryCount();
    }

    public IVoxelPassiveEffect get(String key) { return PASSIVES.get(key); }
    public Set<String> getRegisteredKeys() { return PASSIVES.keySet(); }

    //Static direct access methods
    public static IVoxelPassiveEffect staticGet(String key) { return INSTANCE.get(key); }
    public static Set<String> staticGetRegisteredKeys() { return INSTANCE.getRegisteredKeys(); }
}