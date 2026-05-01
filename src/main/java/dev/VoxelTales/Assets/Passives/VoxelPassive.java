package dev.VoxelTales.Assets.Passives;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Interfaces.IVoxelPassiveEffect;

public abstract class VoxelPassive implements IVoxelPassiveEffect {
    private final String name;

    public VoxelPassive(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public abstract void onHit(Ref<EntityStore> attackerRef, Ref<EntityStore> targetRef, float statValue, ComponentAccessor<EntityStore> accessor);
}
