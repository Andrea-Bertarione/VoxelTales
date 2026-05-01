package dev.VoxelTales.Configs;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.VoxelTales.Registries.VoxelConfigsRegistry;

import java.util.HashMap;

public class EntityXPConfigs {
    private HashMap<String, Integer> mobXPLookUpTable;

    public static EntityXPConfigs get() {
        return VoxelConfigsRegistry.getConfig(EntityXPConfigs.class);
    }

    public static final BuilderCodec<EntityXPConfigs> CODEC = BuilderCodec.builder(EntityXPConfigs.class, EntityXPConfigs::new)
            .append(
                    new KeyedCodec<>("MobXPLookUpTable", new MapCodec<>(Codec.INTEGER, HashMap::new)),
                    (config, value) -> config.mobXPLookUpTable = new HashMap<>(value),
                    (config) -> config.mobXPLookUpTable
            )
            .add()
            .build();

    public EntityXPConfigs() {
        this.mobXPLookUpTable = new HashMap<>();
        this.mobXPLookUpTable.put("default", 10);
    }

    public HashMap<String, Integer> getMobXPLookUpTable() {
        return this.mobXPLookUpTable;
    }

    //Helper methods
    public Integer getXP(String name) {
        return this.mobXPLookUpTable.get(name);
    }

    public int getOrGenerateXP(NPCEntity entity, EntityStatMap statMap, Runnable onNewEntry) {
        String npcName = entity.getNPCTypeId();

        if (mobXPLookUpTable.containsKey(npcName)) {
            return mobXPLookUpTable.get(npcName);
        }

        EntityStatValue health = statMap.get(DefaultEntityStatTypes.getHealth());
        int defaultValue = (health != null) ? (int) health.getMax() : 10;

        assert entity.getRole() != null;
        boolean isHostile = entity.getRole().getWorldSupport().getDefaultPlayerAttitude() == Attitude.HOSTILE;

        double xpMulti = (isHostile ? 1.3 : 0.8);

        mobXPLookUpTable.put(npcName, (int) (defaultValue * xpMulti));
        onNewEntry.run();
        return defaultValue;
    }

    public void setXP(String name, int value) {
        this.mobXPLookUpTable.put(name, value);
    }
}