package dev.VoxelTales.Controllers;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.PlayerComponents.VoxelPlayerComponent;
import dev.VoxelTales.Utils.VoxelStatsHelper;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.HashMap;
import java.util.Set;

public class CharacterStatsController {
    public static final String VOXEL_TALES_HEALTH_MODIFIER = "VoxelTales:HealthModifier";

    public static final Set<String> STATS_SET = Set.of(
            //Scaling stat boosts
            "Boost_Physical",
            "Boost_Dexterity",
            "Boost_Fire",
            "Boost_Magic",

            //Passives abilities stats
            "Passive_Burn",
            "Passive_Burn_Duration"
    );


    public static void setLevelHealthBoost(Store<EntityStore> store, Ref<EntityStore> ref, int level) {
        EntityStatMap statMap = getEntityStatMap(store, ref);

        StaticModifier healthMod = new StaticModifier(Modifier.ModifierTarget.MAX, StaticModifier.CalculationType.MULTIPLICATIVE, 1.0f + (level * 0.0125f));
        statMap.putModifier(DefaultEntityStatTypes.getHealth(), VOXEL_TALES_HEALTH_MODIFIER, healthMod);
    }

    public static void setWeaponModifiers(Store<EntityStore> store, Ref<EntityStore> ref, HashMap<String, Float> newModifiers) {
        EntityStatMap statMap = getEntityStatMap(store, ref);
        VoxelPlayerComponent voxelPlayerComponent = getVoxelPlayerComponent(store, ref);

        VoxelStatsHelper.updateWeaponStats(voxelPlayerComponent, statMap, newModifiers);
    }

    private static EntityStatMap getEntityStatMap(Store<EntityStore> store, Ref<EntityStore> ref) {
        return store.ensureAndGetComponent(ref, EntityStatMap.getComponentType());
    }

    private static VoxelPlayerComponent getVoxelPlayerComponent(Store<EntityStore> store, Ref<EntityStore> ref) {
        return store.ensureAndGetComponent(ref, VoxelPlayerComponent.getComponentType());
    }
}
