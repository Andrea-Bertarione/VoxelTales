package dev.VoxelTales.Core;

import dev.VoxelTales.VoxelTalesPlugin;

public abstract class AVoxelRegistry {
    private int registryCount = 0;

    public abstract void init(VoxelTalesPlugin plugin);

    protected void incrementRegistryCount() { registryCount++; }
    public int getRegistryCount() { return registryCount; }
}
