package dev.VoxelTales.Configs;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;

import java.util.HashMap;
import java.util.OptionalInt;

public class EntityXPConfigs {
    private HashMap<String, Integer> mobXPLookUpTable;

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

    public int getOrGenerateXP(String name, EntityStatMap statMap, Runnable onNewEntry) {
        if (mobXPLookUpTable.containsKey(name)) {
            return mobXPLookUpTable.get(name);
        }

        EntityStatValue health = statMap.get(DefaultEntityStatTypes.getHealth());
        int defaultValue = (health != null) ? (int) health.getMax() : 10;

        mobXPLookUpTable.put(name, defaultValue);
        onNewEntry.run();
        return defaultValue;
    }

    public void setXP(String name, int value) {
        this.mobXPLookUpTable.put(name, value);
    }
}