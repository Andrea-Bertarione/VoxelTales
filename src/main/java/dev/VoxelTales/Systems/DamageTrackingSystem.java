package dev.VoxelTales.Systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import dev.VoxelTales.Components.CombatComponents.CombatTrackerComponent;
import dev.VoxelTales.VoxelTalesPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class DamageTrackingSystem extends DamageEventSystem {

    @Override
    public void handle(int index, ArchetypeChunk<EntityStore> archetypeChunk, @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer, @NotNull Damage damage) {
        Ref<EntityStore> targetRef = archetypeChunk.getReferenceTo(index);
        if (!targetRef.isValid()) {
            return;
        }

        Damage.Source source = damage.getSource();
        if (!(source instanceof Damage.EntitySource entitySource)) {
            return;
        }

        Ref<EntityStore> attackerRef = entitySource.getRef();
        if (!attackerRef.isValid()) {
            return;
        }

        Player attackerPlayer = store.getComponent(attackerRef, Player.getComponentType());
        if (attackerPlayer == null) {
            return;
        }

        UUIDComponent attackerUuidComponent = store.getComponent(attackerRef, UUIDComponent.getComponentType());
        if (attackerUuidComponent == null) {
            return;
        }
        UUID attackerUuid = attackerUuidComponent.getUuid();

        CombatTrackerComponent tracker = commandBuffer.getComponent(targetRef, VoxelTalesPlugin.get().getCombatTrackerComponent());

        float damageAmount = damage.getAmount();

        if (tracker != null) {
            tracker.addDamage(attackerUuid, damageAmount);
        }
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(NPCEntity.getComponentType());
    }

    @Nullable
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getInspectDamageGroup();
    }
}
