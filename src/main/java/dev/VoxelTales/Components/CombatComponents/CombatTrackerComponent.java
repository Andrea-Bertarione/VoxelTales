package dev.VoxelTales.Components.CombatComponents;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTrackerComponent implements Component<EntityStore> {
    private final Map<UUID, Float> damageMap;

    public static final BuilderCodec<CombatTrackerComponent> CODEC = BuilderCodec.builder(CombatTrackerComponent.class, CombatTrackerComponent::new)
            .build();

    public CombatTrackerComponent() {
        this.damageMap = new HashMap<>();
    }

    public void addDamage(UUID playerUuid, float amount) {
        this.damageMap.merge(playerUuid, amount, Float::sum);
    }

    public Map<UUID, Float> getDamageMap() {
        return damageMap;
    }

    @Override
    public Component<EntityStore> clone() {
        CombatTrackerComponent clone = new CombatTrackerComponent();
        clone.damageMap.putAll(this.damageMap);
        return clone;
    }
}