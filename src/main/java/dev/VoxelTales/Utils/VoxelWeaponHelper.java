package dev.VoxelTales.Utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.inventory.UpdatePlayerInventory;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.PlayerComponents.VoxelPlayerComponent;
import dev.VoxelTales.Components.PlayerComponents.WeaponHandlerComponent;
import dev.VoxelTales.Configs.VoxelSkillConfigs;
import dev.VoxelTales.Configs.VoxelWeaponConfigs;
import dev.VoxelTales.Controllers.CharacterStatsController;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.Registries.VoxelSkillsRegistry;
import dev.VoxelTales.UI.HUD.WeaponHUD;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VoxelWeaponHelper {
    private static final String DEFAULT_WEAPON_ID = "Weapon_Heirloom_Old_Old";

    public static void equipNewWeapon(PlayerRef playerRef, String blade, String handle) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) return;

        Store<EntityStore> store = ref.getStore();

        store.getExternalData().getWorld().execute(() -> {
            WeaponHandlerComponent weaponHandlerComponent = store.ensureAndGetComponent(ref, WeaponHandlerComponent.getComponentType());
            weaponHandlerComponent.setCurrentBlade(blade);
            weaponHandlerComponent.setCurrentHandle(handle);

            VoxelWeaponHelper.setVoxelWeaponStack(playerRef);

            VoxelCacheRegistry.staticInvalidate(CacheEnum.VOXEL_PLAYER_DAMAGE_CACHE, playerRef.getUuid());
            VoxelCacheRegistry.staticInvalidate(CacheEnum.VOXEL_PLAYER_SCALING_CACHE, playerRef.getUuid());
            VoxelCacheRegistry.staticInvalidate(CacheEnum.VOXEL_PLAYER_ATKSPEED_CACHE, playerRef.getUuid());
        });
    }

    public static void selectSkill(PlayerRef playerRef, String skillId) {
        selectSkillType(playerRef, skillId, WeaponHandlerComponent::setSelectedSkill);
    }

    public static void selectUltimate(PlayerRef playerRef, String ultimateId) {
        selectSkillType(playerRef, ultimateId, WeaponHandlerComponent::setSelectedUltimate);
    }

    private static void selectSkillType(PlayerRef playerRef, String skillId,
                                        BiConsumer<WeaponHandlerComponent, VoxelSkillConfigs.SkillDefinition> setter) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) return;

        Store<EntityStore> store = ref.getStore();
        store.getExternalData().getWorld().execute(() -> {
            WeaponHandlerComponent weaponHandler = store.ensureAndGetComponent(ref, WeaponHandlerComponent.getComponentType());
            VoxelSkillConfigs.SkillDefinition def = VoxelSkillsRegistry.staticGetSkill(skillId);
            setter.accept(weaponHandler, def);
        });
    }

    public static void syncHotbar(PlayerRef playerRef, Ref<EntityStore> ref, Store<EntityStore> store) {
        World world = store.getExternalData().getWorld();

        world.execute(() -> {
            InventoryComponent hotbar = store.getComponent(ref, Objects.requireNonNull(InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID)));
            if (hotbar == null) return;

            UpdatePlayerInventory clientPacket = new UpdatePlayerInventory();
            clientPacket.hotbar = hotbar.getInventory().toPacket();

            playerRef.getPacketHandler().writeNoCache(clientPacket);
        });
    }

    public static void changeVoxelWeaponSlot(PlayerRef playerRef, Store<EntityStore> store, Ref<EntityStore> ref, short oldSlot, short slot) {
        WeaponHandlerComponent weaponHandlerComponent = store.ensureAndGetComponent(ref, WeaponHandlerComponent.getComponentType());
        VoxelPlayerComponent playerComponent = store.ensureAndGetComponent(ref, VoxelPlayerComponent.getComponentType());
        InventoryComponent.Hotbar hotbar = store.ensureAndGetComponent(ref, InventoryComponent.Hotbar.getComponentType());

        hotbar.getInventory().setItemStackForSlot(oldSlot, null);
        setVoxelWeaponStack(store, ref, hotbar, weaponHandlerComponent, slot);

        playerComponent.setWeaponSlot(slot);
        VoxelCacheRegistry.staticUpdate(CacheEnum.SLOT_CACHE, playerRef, slot);
    }

    public static void setVoxelWeaponStack(PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) return;

        Store<EntityStore> store = ref.getStore();

        WeaponHandlerComponent weaponHandlerComponent = store.ensureAndGetComponent(ref, WeaponHandlerComponent.getComponentType());
        InventoryComponent.Hotbar hotbar = store.ensureAndGetComponent(ref, InventoryComponent.Hotbar.getComponentType());

        setVoxelWeaponStack(store, ref, hotbar, weaponHandlerComponent, VoxelCacheRegistry.staticGet(CacheEnum.SLOT_CACHE, playerRef, Short.class));
    }

    public static void setVoxelWeaponStack(@NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, InventoryComponent.Hotbar hotbar, WeaponHandlerComponent weaponHandlerComponent, short slot) {
        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;

        VoxelWeaponConfigs.WeaponStatSnapshot statSnapshot = weaponHandlerComponent.getStatSnapshot();

        hotbar.getInventory().setItemStackForSlot(slot,
                generateItemStack(weaponHandlerComponent));

        CharacterStatsController.setWeaponModifiers(store, ref, (HashMap<String, Float>) statSnapshot.passivesMap());

        WeaponHUD weaponHUD = VoxelCacheRegistry.staticGet(CacheEnum.HUD_CACHE, store.ensureAndGetComponent(ref, PlayerRef.getComponentType()), WeaponHUD.class);
        if (hotbar.getActiveSlot() == slot) {
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
            return new ItemStack(DEFAULT_WEAPON_ID);
        }

        return new ItemStack(finalId);
    }
}