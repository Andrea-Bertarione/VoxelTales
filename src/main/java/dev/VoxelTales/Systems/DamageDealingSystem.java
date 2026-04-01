package dev.VoxelTales.Systems;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.common.util.RandomUtil;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.DamageEntityInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.VoxelTales.Components.WeaponHandlerComponent;
import dev.VoxelTales.Utils.VoxelMetadata;
import dev.VoxelTales.Utils.VoxelStatsHelper;
import dev.VoxelTales.VoxelTalesPlugin;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;

public class DamageDealingSystem extends DamageEventSystem {
    @Override
    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store,
                       @Nonnull CommandBuffer<EntityStore> buffer, @Nonnull Damage damage) {

        if (Boolean.TRUE.equals(damage.getIfPresentMetaObject(VoxelMetadata.PROCESSED_KEY))) return;
        if (!(damage.getSource() instanceof Damage.EntitySource source)) return;

        Ref<EntityStore> attackerRef = source.getRef();
        Ref<EntityStore> targetRef = chunk.getReferenceTo(index);

        WeaponHandlerComponent weapon = store.getComponent(attackerRef, VoxelTalesPlugin.get().getWeaponHandlerComponent());
        EntityStatMap attackerStats = store.getComponent(attackerRef, EntityStatMap.getComponentType());

        if (weapon == null || attackerStats == null) return;

        float totalEffectiveBoost = 0.0f;
        Map<String, Float> scalings = weapon.getCalculatedScalingMap(); // e.g., {"Air": 1.1, "Magic": 0.5}
        for (Map.Entry<String, Float> entry : scalings.entrySet()) {
            float scalingValue = entry.getValue();
            float statBoost = 0f;

            int statIndex = VoxelStatsHelper.getStatIndex("Boost_" + entry.getKey());
            if (statIndex != -1) {
                EntityStatValue attackStatValue = attackerStats.get(statIndex);
                if (attackStatValue != null) {
                    statBoost = attackStatValue.get();
                }
            }

            totalEffectiveBoost += (scalingValue * statBoost);
        }

        Float finalScaledDamage = calculateDamage(weapon, attackerStats, totalEffectiveBoost);
        String interactionSource = damage.getMetaObject(VoxelMetadata.DAMAGE_SOURCE_KEY);

        if (interactionSource != null) {
            LoggerUtil.getLogger().info("The interactionSource is: " + interactionSource);
        }
        else {
            LoggerUtil.getLogger().warning("No interactionSource found!");
        }

        Map<String, Float> typeMap = weapon.getCalculatedDamageMap(); // e.g., {"Fire": 0.7, "Magic": 0.3}
        if (typeMap.isEmpty()) return;

        boolean isFirst = true;
        for (Map.Entry<String, Float> entry : typeMap.entrySet()) {
            String typeName = entry.getKey();
            float typeMultiplier = entry.getValue();

            float calculatedAmount = finalScaledDamage * typeMultiplier;

            DamageCause cause = DamageCause.getAssetMap().getAsset(typeName);
            if (cause == null) continue;

            if (isFirst) {
                damage.setAmount(calculatedAmount);
                damage.setDamageCauseIndex(DamageCause.getAssetMap().getIndex(typeName));
                damage.putMetaObject(VoxelMetadata.PROCESSED_KEY, true);
                damage.setCancelled(false);

                buffer.ensureAndGetComponent(targetRef, VoxelTalesPlugin.get().getCombatTrackerComponent());
                isFirst = false;
            } else {
                Damage extraHit = new Damage(damage.getSource(), cause, calculatedAmount);
                copyDamageMeta(damage, extraHit);
                extraHit.putMetaObject(VoxelMetadata.PROCESSED_KEY, true);

                DamageSystems.executeDamage(targetRef, buffer, extraHit);
            }
        }
    }

    @Override
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.or(NPCEntity.getComponentType(), Player.getComponentType());
    }

    private void copyDamageMeta(Damage original, Damage next) {
        if (original.getIfPresentMetaObject(Damage.HIT_LOCATION) != null) {
            next.putMetaObject(Damage.HIT_LOCATION, original.getMetaObject(Damage.HIT_LOCATION));
        }
        if (original.getIfPresentMetaObject(Damage.HIT_ANGLE) != null) {
            next.putMetaObject(Damage.HIT_ANGLE, original.getMetaObject(Damage.HIT_ANGLE));
        }

        // Copy the DamageSequence so the extra hits know they are part of the same 'swing'
        DamageCalculatorSystems.DamageSequence seq = original.getIfPresentMetaObject(DamageCalculatorSystems.DAMAGE_SEQUENCE);
        if (seq != null) {
            next.putMetaObject(DamageCalculatorSystems.DAMAGE_SEQUENCE, seq);
        }
    }

    private Float calculateDamage(WeaponHandlerComponent weaponHandlerComponent, EntityStatMap attackerStats, Float totalEffectiveBoost) {
        float baseDamage = 1.5f;

        int weaponLevel = weaponHandlerComponent.getSwordInternalLevel();
        float levelMultiplier = 1.0f + (weaponLevel * 0.0125f);

        float atkSpeed = weaponHandlerComponent.getCalculatedAttackSpeed();
        float speedMultiplier = 1.0f + ((1.0f - atkSpeed) * 1.5f);
        speedMultiplier = Math.max(0.1f, speedMultiplier);

        float finalScaledDamage = (baseDamage * speedMultiplier) * levelMultiplier * (1.0f + totalEffectiveBoost);
        return tryCrit(finalScaledDamage, attackerStats);
    }

    private Float tryCrit(Float currentDamage, EntityStatMap statMap) {
        int statIndex = VoxelStatsHelper.getStatIndex("Boost_Dexterity");
        if (statIndex == -1) return currentDamage;

        EntityStatValue dexStat = statMap.get(statIndex);
        if (dexStat == null) return currentDamage;

        float dexBoostPercent = dexStat.get() * 100f;
        float critChance = dexBoostPercent / 10f;
        float critMultiplier = critChance / 100f;

        if (critChance < 0.1f) return currentDamage;

        if (RandomUtil.getSecureRandom().nextFloat(100f) <= critChance) {
            return currentDamage * (1f + critMultiplier);
        }

        return currentDamage;
    }
}