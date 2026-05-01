package dev.VoxelTales.Utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import dev.VoxelTales.Components.PlayerComponents.PlayerWeaponProgressComponent;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelWeaponProgressionHelper {
    private static final String SECONDARY_LINE_NOTIFICATION = "Talk with the Sword Sage to check them out!";
    private static final String BLADE_UNLOCK_NOTIFICATION = "Blade unlocked: %s";
    private static final String HANDLE_UNLOCK_NOTIFICATION = "Handle unlocked: %s";
    private static final ItemStack BLADE_ICON = new ItemStack("Weapon_Sword_Wood", 1);
    private static final ItemStack HANDLE_ICON = new ItemStack("Weapon_Club_Scrap", 1);


    public static void unlockBlade(PlayerRef playerRef, String bladeId) {
        if (playerRef == null || bladeId == null || bladeId.isBlank()) {
            return;
        }

        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }

        Store<EntityStore> store = ref.getStore();
        PlayerWeaponProgressComponent progressComponent =
                store.ensureAndGetComponent(ref, PlayerWeaponProgressComponent.getComponentType());

        progressComponent.unlockBlade(bladeId);
        runNotification(playerRef, "blade", bladeId);
    }

    public static void unlockHandle(PlayerRef playerRef, String handleId) {
        if (playerRef == null || handleId == null || handleId.isBlank()) {
            return;
        }

        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }

        Store<EntityStore> store = ref.getStore();
        PlayerWeaponProgressComponent progressComponent =
                store.ensureAndGetComponent(ref, PlayerWeaponProgressComponent.getComponentType());

        progressComponent.unlockHandle(handleId);
        runNotification(playerRef, "handle", handleId);
    }

    private static void runNotification(PlayerRef playerRef, String type, String id) {
        String message = type.equals("blade") ? String.format(BLADE_UNLOCK_NOTIFICATION, id) : String.format(HANDLE_UNLOCK_NOTIFICATION, id);
        var primary = Message.raw(message).color("#00FF00");
        var secondary = Message.raw(SECONDARY_LINE_NOTIFICATION).color("#FFFFFF");
        var icon = (type.equals("blade") ? BLADE_ICON : HANDLE_ICON).toPacket();
        NotificationUtil.sendNotification(playerRef.getPacketHandler(), primary, secondary, icon);
    }
}