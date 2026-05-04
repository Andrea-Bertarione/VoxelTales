package dev.VoxelTales.Utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.PlayerComponents.WeaponHandlerComponent;
import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class VoxelMathHelper {
    public static int getRequiredXP(int level) {
        VoxelTalesConfigs configs = VoxelTalesConfigs.get();
        return (int) (30 + (configs.getXpBaseValue() * Math.pow(level, configs.getXpExponent())));
    }

    public static float getXPProgress(int currentXP, int level) {
        return ((float) currentXP / getRequiredXP(level));
    }

    public static Map<String, Float> getCachedDamageMap(PlayerRef playerRef) {
        return Objects.requireNonNull(getWeaponHandlerComponent(playerRef)).getCalculatedDamageMap();
    }

    public static Map<String, Float> getCachedScalingMap(PlayerRef playerRef) {
        return Objects.requireNonNull(getWeaponHandlerComponent(playerRef)).getCalculatedScalingMap();
    }

    public static float getCachedAttackSpeedMap(PlayerRef playerRef) {
        return Objects.requireNonNull(getWeaponHandlerComponent(playerRef)).getCalculatedAttackSpeed();
    }

    private static WeaponHandlerComponent getWeaponHandlerComponent(PlayerRef playerRef) {
        Pair<Ref<EntityStore>, Store<EntityStore>> pair = getPlayerRefAndStore(playerRef);
        if (pair == null) {
            return null;
        }

        Ref<EntityStore> ref = pair.getFirst();
        Store<EntityStore> store = pair.getSecond();

        return store.ensureAndGetComponent(ref, WeaponHandlerComponent.getComponentType());
    }

    private static Pair<Ref<EntityStore>, Store<EntityStore>> getPlayerRefAndStore(PlayerRef playerRef) {
        if (playerRef == null) { return null; }
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) { return null; }
        Store<EntityStore> store = ref.getStore();

        return new Pair<>(ref, store);
    }
}
