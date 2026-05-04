package dev.VoxelTales.Controllers;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;

import dev.VoxelTales.Components.PlayerComponents.WeaponHandlerComponent;
import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.UI.HUD.WeaponHUD;
import dev.VoxelTales.Utils.VoxelMathHelper;

public class LevelingController {

    public static void incrementXP(Store<EntityStore> store, Ref<EntityStore> ref, int amount) {
        WeaponHandlerComponent weaponHandlerComponent = store.ensureAndGetComponent(ref, WeaponHandlerComponent.getComponentType());

        VoxelTalesConfigs configs = VoxelTalesConfigs.get();

        int currentXP = weaponHandlerComponent.getSwordXP();
        weaponHandlerComponent.setSwordXP((int) (currentXP + (amount * configs.getGlobalXpMultiplier())));

        int levelsGained = checkLevelUP(weaponHandlerComponent);

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) { return; }

        // Redraw HUD
        WeaponHUD weaponHUD = VoxelCacheRegistry.staticGet(CacheEnum.HUD_CACHE, playerRef, WeaponHUD.class);
        weaponHUD.update();

        if (levelsGained > 0) {
            levelUP(levelsGained, playerRef, store, ref);
            CharacterStatsController.setLevelHealthBoost(store, ref, weaponHandlerComponent.getSwordInternalLevel());
        }
    }

    private static int checkLevelUP(WeaponHandlerComponent weaponHandlerComponent) {
        int startingLevel = weaponHandlerComponent.getSwordInternalLevel();
        int requiredXP = VoxelMathHelper.getRequiredXP(startingLevel);

        VoxelTalesConfigs configs = VoxelTalesConfigs.get();
        if (startingLevel >= configs.getMaxLevel()) { return 0; }
        while (weaponHandlerComponent.getSwordXP() >= requiredXP) {
            weaponHandlerComponent.setSwordXP(weaponHandlerComponent.getSwordXP() - requiredXP);
            weaponHandlerComponent.incrementLevel();
            weaponHandlerComponent.addSP(configs.getSpPerLevel());

            requiredXP = VoxelMathHelper.getRequiredXP(weaponHandlerComponent.getSwordInternalLevel());
        }

        return weaponHandlerComponent.getSwordInternalLevel() - startingLevel;
    }

    private static void levelUP(int levels, PlayerRef playerRef, Store<EntityStore> store, Ref<EntityStore> ref) {
        VoxelTalesConfigs configs = VoxelTalesConfigs.get();

        String secondary = levels == 1 ?
                "Congratulations you leveled up and gained " + levels * configs.getSpPerLevel() + " SP!"
                : "Congratulations you leveled up " + levels + " times and gained " + levels * configs.getSpPerLevel() + " SP!";

        var primaryMessage = Message.raw("Level up!").color("#00FF00");
        var secondaryMessage = Message.raw(secondary).color("#FFFFFF");
        var icon = new ItemStack("Weapon_Sword_Steel", 1).toPacket();

        NotificationUtil.sendNotification(playerRef.getPacketHandler(), primaryMessage, secondaryMessage, icon);
        int soundIndex = SoundEvent.getAssetMap().getIndex("SFX_Level_Up_Generic");

        World world = store.getExternalData().getWorld();

        world.execute(() -> {
            TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());

            if (transform != null) {
                SoundUtil.playSoundEvent3dToPlayer(
                        ref,
                        soundIndex,
                        SoundCategory.SFX,
                        transform.getPosition(),
                        store
                );
            }
        });
    }
}