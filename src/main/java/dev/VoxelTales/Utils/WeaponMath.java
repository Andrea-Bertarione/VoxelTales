package dev.VoxelTales.Utils;

import com.hypixel.hytale.assetstore.map.LookupTableAssetMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WeaponMath {
    public static int getRequiredXP(int level) {
        VoxelTalesConfigs configs = VoxelTalesPlugin.get().getVoxelTalesConfigs().get();
        return (int) (configs.getXpBaseValue() * Math.pow(level, configs.getXpExponent()));
    }

    public static float getXPProgress(int currentXP, int level) {
        return ((float) currentXP / getRequiredXP(level));
    }

    public static Map<String, Float> getFinalDamageMap(int level, EntityStatMap statMap, Map<String, Float> scalingMap, Map<String, Float> baseDamageMap) {
        float levelMultiplier = 1.0f + (level * 0.0125f);

        float scalingSum = 0f;
        for (Map.Entry<String, Float> entry : scalingMap.entrySet()) {
            float scalingValue = entry.getValue();

            int statType = EntityStatType.getAssetMap().getIndexOrDefault("Boost_" + entry.getKey(), -1);

            if (statType != -1 && statMap.get(statType) != null) {
                scalingSum += (scalingValue * Objects.requireNonNull(statMap.get(statType)).get());
            }
        }

        float statMultiplier = 1.0f + scalingSum;
        float totalMultiplier = levelMultiplier * statMultiplier;

        Map<String, Float> finalMap = new HashMap<>();
        for (Map.Entry<String, Float> entry : baseDamageMap.entrySet()) {
            finalMap.put(entry.getKey(), entry.getValue() * totalMultiplier);
        }

        return finalMap;
    }
}
