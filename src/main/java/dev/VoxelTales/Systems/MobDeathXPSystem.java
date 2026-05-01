package dev.VoxelTales.Systems;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.VoxelTales.Components.CombatComponents.CombatTrackerComponent;
import dev.VoxelTales.Controllers.LevelingController;
import dev.VoxelTales.Utils.VoxelXPConfigsHelper;
import dev.VoxelTales.VoxelTalesPlugin;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MobDeathXPSystem extends DeathSystems.OnDeathSystem {

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(
                CombatTrackerComponent.getComponentType(),
                NPCEntity.getComponentType(),
                EntityStatMap.getComponentType(),
                Query.not(Player.getComponentType())
        );
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref , @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store , @Nonnull CommandBuffer<EntityStore>  commandBuffer) {
        CombatTrackerComponent tracker = store.getComponent(ref, CombatTrackerComponent.getComponentType());
        NPCEntity npcEntity = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        EntityStatMap entityStatMap = store.getComponent(ref, EntityStatMap.getComponentType());
        if (tracker == null) return;
        if (npcEntity == null) return;
        if (entityStatMap == null) return;

        Map<UUID, Float> damageMap = tracker.getDamageMap();
        float totalDamage = 0;
        for (float d : damageMap.values()) totalDamage += d;

        int totalXp = VoxelXPConfigsHelper.getMobXP(npcEntity, entityStatMap);

        for (Map.Entry<UUID, Float> entry : damageMap.entrySet()) {
            float percentage = entry.getValue() / totalDamage;
            int sharedXp = (int) (totalXp * percentage);

            PlayerRef playerRef = Universe.get().getPlayer(entry.getKey());
            if (playerRef != null && sharedXp > 0) {
                store.getExternalData().getWorld().execute(() -> {
                    LevelingController.incrementXP(store, playerRef.getReference(), sharedXp);
                });

            }
        }
    }
}