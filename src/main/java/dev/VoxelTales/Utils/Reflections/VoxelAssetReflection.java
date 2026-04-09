package dev.VoxelTales.Utils.Reflections;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.DamageEntityInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageCalculator;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class VoxelAssetReflection {

    public static void patch() {
        LoggerUtil.getLogger().info("[VoxelPatcher] Global Interaction Patching Started.");

        try {
            Field calcField = DamageEntityInteraction.class.getDeclaredField("damageCalculator");
            Field rawMapField = DamageCalculator.class.getDeclaredField("baseDamageRaw");
            Field indexMapField = DamageCalculator.class.getDeclaredField("baseDamage");

            calcField.setAccessible(true);
            rawMapField.setAccessible(true);
            indexMapField.setAccessible(true);

            Constructor<DamageCalculator> calcConstructor = DamageCalculator.class.getDeclaredConstructor();
            calcConstructor.setAccessible(true);

            int totalInteractions = 0;
            int patchedCount = 0;

            for (Interaction interaction : Interaction.getAssetMap().getAssetMap().values()) {
                if (interaction == null) continue;
                totalInteractions++;

                if (interaction.getClass().getName().contains("DamageEntityInteraction")) {
                    DamageEntityInteraction dmgInteraction = (DamageEntityInteraction) interaction;

                    if (applyPatch(dmgInteraction, calcField, rawMapField, indexMapField, calcConstructor)) {
                        patchedCount++;
                    }
                }
            }

            LoggerUtil.getLogger().info("[VoxelPatcher] Scanned " + totalInteractions + " interactions. Patched " + patchedCount + " damage triggers.");

        } catch (Exception e) {
            LoggerUtil.getLogger().info("[VoxelPatcher] FATAL ERROR: " + e.toString());
            e.printStackTrace();
        }
    }

    private static boolean applyPatch(DamageEntityInteraction interaction, Field calcField, Field rawMapField, Field indexMapField, Constructor<DamageCalculator> constructor) throws Exception {
        DamageCalculator calc = (DamageCalculator) calcField.get(interaction);

        if (calc == null) {
            calc = constructor.newInstance();
            calcField.set(interaction, calc);
        }

        Object2FloatMap<String> rawMap = (Object2FloatMap<String>) rawMapField.get(calc);

        if (rawMap == null || rawMap.isEmpty()) {
            Object2FloatOpenHashMap<String> newRaw = new Object2FloatOpenHashMap<>();
            newRaw.put("Physical", 0.0001f);

            rawMapField.set(calc, newRaw);

            Int2FloatOpenHashMap newIndex = new Int2FloatOpenHashMap();
            int physIdx = DamageCause.getAssetMap().getIndex("Physical");
            newIndex.put(physIdx != -1 ? physIdx : 0, 0.0001f);

            indexMapField.set(calc, newIndex);

            return true;
        }
        return false;
    }
}