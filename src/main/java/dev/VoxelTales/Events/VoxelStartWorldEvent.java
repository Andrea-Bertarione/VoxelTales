package dev.VoxelTales.Events;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.config.WorldWorldMapConfig;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.events.StartWorldEvent;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import  com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import it.unimi.dsi.fastutil.Pair;

import java.util.Objects;

public class VoxelStartWorldEvent {
    private static final Vector3f SWORD_SAGE_ROTATION = new Vector3f(0.0F, 0.0F, 0.0F);

    public static void onStartWorld(StartWorldEvent event) {
        World world = event.getWorld();

        ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
        if (spawnProvider == null) {
            LoggerUtil.getLogger().warning("WorldWorldMapConfig is null!");
            return;
        }

        Vector3d SWORD_SAGE_POSITION = spawnProvider.getSpawnPoint(world, world.getWorldConfig().getUuid()).getPosition();

        Store<EntityStore> store = world.getEntityStore().getStore();

        Pair<Ref<EntityStore>, INonPlayerCharacter> result =
                NPCPlugin.get().spawnNPC(store, "Sword_Sage", null, new Vector3d(SWORD_SAGE_POSITION), SWORD_SAGE_ROTATION);

        if (result == null) {
            throw new IllegalStateException("Failed to spawn Sword_Sage NPC");
        }

        Ref<EntityStore> npcRef = result.first();
        TransformComponent transform = store.getComponent(npcRef, TransformComponent.getComponentType());
        if (transform != null) {
            transform.getPosition().assign(SWORD_SAGE_POSITION);
        }

        LoggerUtil.getLogger().info("Sword_Sage NPC spawned at " + SWORD_SAGE_POSITION);
    }
}

