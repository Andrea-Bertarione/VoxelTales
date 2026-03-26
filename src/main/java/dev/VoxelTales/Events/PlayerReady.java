package dev.VoxelTales.Events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.VoxelPlayerComponent;
import dev.VoxelTales.Components.WeaponHandlerComponent;
import dev.VoxelTales.Controllers.CharacterStatsController;
import dev.VoxelTales.UI.WeaponHUD;
import dev.VoxelTales.Utils.SwordFactory;
import dev.VoxelTales.Utils.VoxelCacheRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.HashMap;

public class PlayerReady {
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

        SwordFactory.setVoxelWeaponStack(store, ref, playerComponent, weaponHandlerComponent);
        CharacterStatsController.setLevelHealthBoost(store, ref, weaponHandlerComponent.getSwordInternalLevel());
    }
}
