package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.system.ISystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Core.AVoxelRegistry;
import dev.VoxelTales.Systems.DamageDealingSystem;
import dev.VoxelTales.Systems.DamageTrackingSystem;
import dev.VoxelTales.Systems.MemoriesUnlockedSystem;
import dev.VoxelTales.Systems.MobDeathXPSystem;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelSystemsRegistry extends AVoxelRegistry {
    public void init(VoxelTalesPlugin plugin) {
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = plugin.getEntityStoreRegistry();

        registerSystem(entityStoreRegistry, new DamageTrackingSystem());
        registerSystem(entityStoreRegistry, new MobDeathXPSystem());
        registerSystem(entityStoreRegistry, new DamageDealingSystem());
        registerSystem(entityStoreRegistry, new MemoriesUnlockedSystem());

        LoggerUtil.getLogger().info("[VoxelSystemRegistry] Registered " + super.getRegistryCount() + " systems.");
    }

    private void registerSystem(ComponentRegistryProxy<EntityStore> entityStoreRegistry, ISystem<EntityStore> system) {
        entityStoreRegistry.registerSystem(system);
        super.incrementRegistryCount();
    }


}
