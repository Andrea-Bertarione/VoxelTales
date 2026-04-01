package dev.VoxelTales.Utils;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.meta.MetaKey;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCalculatorSystems;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.DamageEntityInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageCalculator;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageClass;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageEffects;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VoxelDamageEntityReflection {
    // Cached MetaKeys
    public static MetaKey<Integer> NEXT_INDEX;
    public static MetaKey<DamageCalculatorSystems.Sequence> SEQUENTIAL_HITS;
    public static MetaKey<Damage[]> QUEUED_DAMAGE;

    // Cached Methods
    private static Method calculateKnockbackMethod;
    private static Method processDamageMethod;

    // Nested fields
    private static Field TD_INDEX;
    private static Field TD_DAMAGE_CALCULATOR;
    private static Field TD_DAMAGE_EFFECTS;
    private static Field AD_ANGLE_RAD;
    private static Field AD_ANGLE_DISTANCE_RAD;

    static {
        try {
            // Extract private static MetaKeys
            Field nextIndexField = DamageEntityInteraction.class.getDeclaredField("NEXT_INDEX");
            nextIndexField.setAccessible(true);
            NEXT_INDEX = (MetaKey<Integer>) nextIndexField.get(null);

            Field seqHitsField = DamageEntityInteraction.class.getDeclaredField("SEQUENTIAL_HITS");
            seqHitsField.setAccessible(true);
            SEQUENTIAL_HITS = (MetaKey<DamageCalculatorSystems.Sequence>) seqHitsField.get(null);

            Field queuedDamageField = DamageEntityInteraction.class.getDeclaredField("QUEUED_DAMAGE");
            queuedDamageField.setAccessible(true);
            QUEUED_DAMAGE = (MetaKey<Damage[]>) queuedDamageField.get(null);

            // Extract private static calculateKnockbackAndArmorModifiers method [cite: 235]
            calculateKnockbackMethod = DamageEntityInteraction.class.getDeclaredMethod(
                    "calculateKnockbackAndArmorModifiers",
                    DamageClass.class, Object2FloatMap.class, Ref.class, Ref.class, float[].class, double[].class, ComponentAccessor.class
            );
            calculateKnockbackMethod.setAccessible(true);

            // Extract private processDamage method [cite: 165]
            processDamageMethod = DamageEntityInteraction.class.getDeclaredMethod(
                    "processDamage",
                    InteractionContext.class, Damage[].class
            );
            processDamageMethod.setAccessible(true);

            TD_INDEX = DamageEntityInteraction.TargetedDamage.class.getDeclaredField("index");
            TD_INDEX.setAccessible(true);

            TD_DAMAGE_CALCULATOR = DamageEntityInteraction.TargetedDamage.class.getDeclaredField("damageCalculator");
            TD_DAMAGE_CALCULATOR.setAccessible(true);

            TD_DAMAGE_EFFECTS = DamageEntityInteraction.TargetedDamage.class.getDeclaredField("damageEffects");
            TD_DAMAGE_EFFECTS.setAccessible(true);

            // AngledDamage Fields
            AD_ANGLE_RAD = DamageEntityInteraction.AngledDamage.class.getDeclaredField("angleRad");
            AD_ANGLE_RAD.setAccessible(true);

            AD_ANGLE_DISTANCE_RAD = DamageEntityInteraction.AngledDamage.class.getDeclaredField("angleDistanceRad");
            AD_ANGLE_DISTANCE_RAD.setAccessible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Helper Invokers ---

    public static void invokeKnockback(DamageClass damageClass, Object2FloatMap<DamageCause> damage, Ref<EntityStore> targetRef, Ref<EntityStore> attackerRef, float[] armorDamageModifiers, double[] knockbackMultiplier, ComponentAccessor<EntityStore> componentAccessor) {
        try {
            calculateKnockbackMethod.invoke(null, damageClass, damage, targetRef, attackerRef, armorDamageModifiers, knockbackMultiplier, componentAccessor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean invokeProcessDamage(DamageEntityInteraction instance, InteractionContext context, Damage[] queuedDamage) {
        try {
            return (boolean) processDamageMethod.invoke(instance, context, queuedDamage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getIndex(DamageEntityInteraction.TargetedDamage td) {
        try { return TD_INDEX.getInt(td); } catch (Exception e) { return 0; }
    }

    public static DamageCalculator getDamageCalculator(DamageEntityInteraction.TargetedDamage td) {
        try { return (DamageCalculator) TD_DAMAGE_CALCULATOR.get(td); } catch (Exception e) { return null; }
    }

    public static DamageEffects getDamageEffects(DamageEntityInteraction.TargetedDamage td) {
        try { return (DamageEffects) TD_DAMAGE_EFFECTS.get(td); } catch (Exception e) { return null; }
    }

    public static float getAngleRad(DamageEntityInteraction.AngledDamage ad) {
        try { return AD_ANGLE_RAD.getFloat(ad); } catch (Exception e) { return 0f; }
    }

    public static float getAngleDistanceRad(DamageEntityInteraction.AngledDamage ad) {
        try { return AD_ANGLE_DISTANCE_RAD.getFloat(ad); } catch (Exception e) { return 0f; }
    }
}