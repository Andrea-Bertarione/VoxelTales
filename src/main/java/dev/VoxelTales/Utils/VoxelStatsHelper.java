package dev.VoxelTales.Utils;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import dev.VoxelTales.Components.PlayerComponents.VoxelPlayerComponent;
import kotlin.Pair;
import dev.VoxelTales.Controllers.CharacterStatsController;

import java.util.*;

public class VoxelStatsHelper {
    public static void updateWeaponStats(VoxelPlayerComponent playerComp, EntityStatMap statMap, HashMap<String, Float> newModifiers) {
        Map<String, Float> weaponStatModifiers = playerComp.getWeaponStatModifiers();

        if (weaponStatModifiers != null && !weaponStatModifiers.isEmpty()) {
            for (Map.Entry<String, Float> oldEntry : weaponStatModifiers.entrySet()) {
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

        // Update the state
        playerComp.setWeaponStatModifiers(newModifiers != null ? new HashMap<>(newModifiers) : new HashMap<>());
        playerComp.setAreStatsUpdated(true);
    }

    public static int getStatIndex(String stat) {
        if (!CharacterStatsController.STATS_SET.contains(stat)) return -1;
        return EntityStatType.getAssetMap().getIndexOrDefault(stat, -1);
    }

    public static List<Pair<String, Float>> getAllStats(EntityStatMap statMap) {
        List<Pair<String, Float>> stats = new ArrayList<>();

        CharacterStatsController.STATS_SET.forEach((statName) -> {
            int index = getStatIndex(statName);
            if (index != -1) {
                Pair<String, Float> val = new Pair<>(statName, Objects.requireNonNull(statMap.get(index)).get());
                stats.add(val);
            }
        });

        return stats;
    }
}