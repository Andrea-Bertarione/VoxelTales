package dev.VoxelTales.Systems;

import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Assets.Dialogues.Flags.SwordSageFlags;
import dev.VoxelTales.Components.PlayerComponents.DialogueStateComponent;
import dev.VoxelTales.VoxelTalesPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class MemoriesUnlockedSystem extends RefChangeSystem<EntityStore, PlayerMemories> {
    @NotNull
    @Override
    public ComponentType<EntityStore, PlayerMemories> componentType() {
        return PlayerMemories.getComponentType();
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull PlayerMemories playerMemories, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        DialogueStateComponent stateComponent = commandBuffer.ensureAndGetComponent(ref, DialogueStateComponent.getComponentType());

        //LoggerUtil.getLogger().info("Sword Sage quest completed");

        stateComponent.setFlag(SwordSageFlags.COMPLETED_SWORD_SAGE_QUEST, true);
    }

    @Override
    public void onComponentSet(@NotNull Ref<EntityStore> ref, @Nullable PlayerMemories playerMemories, @NotNull PlayerMemories t1, @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {
        DialogueStateComponent stateComponent = commandBuffer.ensureAndGetComponent(ref, DialogueStateComponent.getComponentType());

        //LoggerUtil.getLogger().info("Sword Sage quest completed");

        stateComponent.setFlag(SwordSageFlags.COMPLETED_SWORD_SAGE_QUEST, true);
    }

    @Override
    public void onComponentRemoved(@NotNull Ref<EntityStore> ref, @NotNull PlayerMemories playerMemories, @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {}

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}
