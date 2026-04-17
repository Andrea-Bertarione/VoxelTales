package dev.VoxelTales.Events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.VoxelPlayerComponent;
import dev.VoxelTales.Components.WeaponHandlerComponent;
import dev.VoxelTales.Controllers.CharacterStatsController;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.Utils.VoxelSwordHelper;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.HashMap;

public class VoxelPlayerReadyEvent {
    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();

        Ref<EntityStore> ref = player.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }
        Store<EntityStore> store = ref.getStore();
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        assert playerRef != null;

        VoxelPlayerComponent playerComponent = store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getVoxelPlayerComponent());
        WeaponHandlerComponent weaponHandlerComponent = store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getWeaponHandlerComponent());
        short itemSlot = playerComponent.getWeaponSlot();

        VoxelTalesPlugin.get().getSlotCache().put(playerRef.getUuid(), itemSlot);

        VoxelSwordHelper.setVoxelWeaponStack(store, ref, playerComponent, weaponHandlerComponent);
        CharacterStatsController.setLevelHealthBoost(store, ref, weaponHandlerComponent.getSwordInternalLevel());

        VoxelCacheRegistry.get("VoxelPlayerWeaponProgressCache", playerRef, HashMap.class);
    }
}
