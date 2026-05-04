package dev.VoxelTales.Core.Interfaces;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public interface IVoxelPassiveEffect {
    void onHit(Ref<EntityStore> attackerRef, Ref<EntityStore> targetRef, float statValue, ComponentAccessor<EntityStore> accessor);
}
