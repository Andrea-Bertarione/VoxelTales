package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import dev.VoxelTales.Interfaces.IVoxelPassiveEffect;
import dev.VoxelTales.Utils.VoxelStatsHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VoxelPassivesRegistry{
    private static final Map<String, IVoxelPassiveEffect> PASSIVES = new HashMap<>();
    private static final EntityEffect BURN_EFFECT = EntityEffect.getAssetMap().getAsset("Burn");

    static {
        PASSIVES.put("Passive_Burn", (attacker, targetRef, chance, accessor) -> {
            if (passedChance(chance)) {
                if (targetRef == null || !targetRef.isValid()) return;
                if (BURN_EFFECT == null) return;

                //LoggerUtil.getLogger().info("Passive_Burn activated!");

                EffectControllerComponent effectControllerComponent = accessor.ensureAndGetComponent(targetRef, EffectControllerComponent.getComponentType());

                float duration = accessor.ensureAndGetComponent(attacker, EntityStatMap.getComponentType()).get(VoxelStatsHelper.getStatIndex("Passive_Burn_Duration")).get();
                effectControllerComponent.addEffect(targetRef, BURN_EFFECT, duration, OverlapBehavior.EXTEND, accessor);
            }
        });
    }

    public static IVoxelPassiveEffect get(String key) { return PASSIVES.get(key); }
    public static Set<String> getRegisteredKeys() { return PASSIVES.keySet(); }

    private static boolean passedChance(float chance) {
        return Math.random() < chance;
    }
}