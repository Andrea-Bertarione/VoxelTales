package dev.VoxelTales.Events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.PlayerComponents.PlayerWeaponProgressComponent;
import dev.VoxelTales.Components.PlayerComponents.WeaponHandlerComponent;
import dev.VoxelTales.Controllers.CharacterStatsController;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.Utils.VoxelWeaponHelper;

import java.util.Map;

public class VoxelPlayerReadyEvent {
    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();

        Ref<EntityStore> ref = player.getReference();
        if (ref == null || !ref.isValid()) return;

        Store<EntityStore> store = ref.getStore();
        PlayerRef playerRef = store.ensureAndGetComponent(ref, PlayerRef.getComponentType());
        WeaponHandlerComponent weaponHandlerComponent = store.ensureAndGetComponent(ref, WeaponHandlerComponent.getComponentType());

        VoxelWeaponHelper.setVoxelWeaponStack(playerRef);
        CharacterStatsController.setLevelHealthBoost(store, ref, weaponHandlerComponent.getSwordInternalLevel());

        VoxelCacheRegistry.staticGet(CacheEnum.VOXEL_PLAYER_WEAPON_PROGRESS_CACHE, playerRef, PlayerWeaponProgressComponent.PlayerWeaponProgressData.class);
        VoxelCacheRegistry.staticGet(CacheEnum.VOXEL_PLAYER_DAMAGE_CACHE, playerRef, Map.class);
        VoxelCacheRegistry.staticGet(CacheEnum.VOXEL_PLAYER_SCALING_CACHE, playerRef, Map.class);
        VoxelCacheRegistry.staticGet(CacheEnum.VOXEL_PLAYER_ATKSPEED_CACHE, playerRef, Float.class);
    }
}
