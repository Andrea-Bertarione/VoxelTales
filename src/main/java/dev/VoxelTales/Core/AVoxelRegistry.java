package dev.VoxelTales.Core;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.VoxelTalesPlugin;

public abstract class AVoxelRegistry<T> {
    private int registryCount = 0;

    public abstract void init(VoxelTalesPlugin plugin);

    protected void incrementRegistryCount() { registryCount++; };
    public int getRegistryCount() { return registryCount; };
}
