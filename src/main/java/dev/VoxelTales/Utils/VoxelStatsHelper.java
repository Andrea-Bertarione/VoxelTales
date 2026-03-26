package dev.VoxelTales.Utils;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import dev.VoxelTales.Components.VoxelPlayerComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class VoxelStatsHelper {
    public static final Set<String> STATS_SET = Set.of(
            "Boost_Physical",
            "Boost_Dexterity"
    );

    public static void updateWeaponStats(VoxelPlayerComponent playerComp, EntityStatMap statMap, HashMap<String, Float> newModifiers) {
        if (playerComp.areStatsUpdated && playerComp.weaponStatModifiers != null) {
            for (Map.Entry<String, Float> oldEntry : playerComp.weaponStatModifiers.entrySet()) {
                int index = getStatIndex(oldEntry.getKey());
                if (index != -1 && oldEntry.getValue() != 0f) {
                    float currentValue = Objects.requireNonNull(statMap.get(index)).get();
                    statMap.setStatValue(index, currentValue - oldEntry.getValue());
                }
            }
        }

        if (newModifiers != null) {
            for (Map.Entry<String, Float> newEntry : newModifiers.entrySet()) {
                int index = getStatIndex(newEntry.getKey());
                if (index != -1 && newEntry.getValue() != 0f) {
                    float currentValue = Objects.requireNonNull(statMap.get(index)).get();
                    statMap.setStatValue(index, currentValue + newEntry.getValue());
                }
            }
        }

        playerComp.weaponStatModifiers = newModifiers != null ? new HashMap<>(newModifiers) : new HashMap<>();
        playerComp.areStatsUpdated = true;
    }

    public static int getStatIndex(String stat) {
        if (!STATS_SET.contains(stat)) return -1;
        return EntityStatType.getAssetMap().getIndexOrDefault(stat, -1);
    }
}