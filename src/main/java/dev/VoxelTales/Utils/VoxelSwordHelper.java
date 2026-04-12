package dev.VoxelTales.Utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.VoxelPlayerComponent;
import dev.VoxelTales.Components.WeaponHandlerComponent;
import dev.VoxelTales.UI.HUD.WeaponHUD;
import dev.VoxelTales.VoxelTalesPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class VoxelSwordHelper {
    public static void equipNewWeapon(PlayerRef playerRef, String blade, String handle) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) return;

        Store<EntityStore> store = ref.getStore();

        store.getExternalData().getWorld().execute(() -> {
            WeaponHandlerComponent weaponHandlerComponent = store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getWeaponHandlerComponent());
            VoxelPlayerComponent playerComponent = store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getVoxelPlayerComponent());
            weaponHandlerComponent.setCurrentBlade(blade);
            weaponHandlerComponent.setCurrentHandle(handle);

            VoxelSwordHelper.setVoxelWeaponStack(store, ref, playerComponent, weaponHandlerComponent);
        });
    }

    public static void setVoxelWeaponStack(@NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, VoxelPlayerComponent playerComponent, WeaponHandlerComponent weaponHandlerComponent) {
        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;

        InventoryComponent.Hotbar hotbar = store.getComponent(ref, InventoryComponent.Hotbar.getComponentType());

        assert hotbar != null;
        ItemContainer hotbarInventory = hotbar.getInventory();

        hotbarInventory.setItemStackForSlot(playerComponent.getWeaponSlot(),
                generateItemStack(weaponHandlerComponent));

        HashMap<String, Float> passiveMap = (HashMap<String, Float>) weaponHandlerComponent.getCalculatedPassivesMap();

        playerComponent.areStatsUpdated = false;
        VoxelStatsHelper.updateWeaponStats(playerComponent, store.ensureAndGetComponent(ref, EntityStatMap.getComponentType()), passiveMap);

        WeaponHUD weaponHUD = VoxelTalesPlugin.get().getWeaponHud(store.ensureAndGetComponent(ref, PlayerRef.getComponentType()));
        if (hotbar.getActiveSlot() == playerComponent.getWeaponSlot()) {
            weaponHUD.show();
        }
    }

    public static ItemStack generateItemStack(WeaponHandlerComponent handler) {
        String blade = handler.getCurrentBlade();
        String handle = handler.getCurrentHandle();

        return generateItemStack(blade, handle);
    }

    public static ItemStack generateItemStack(String blade, String handle) {
        String finalId = "Weapon_Heirloom_" + blade + "_" + handle;
        Item item = Item.getAssetMap().getAsset(finalId);

        if (item == null || item == Item.UNKNOWN) {
            return new ItemStack("Weapon_Heirloom_Old_Old");
        }

        return new ItemStack(finalId);
    }
}
