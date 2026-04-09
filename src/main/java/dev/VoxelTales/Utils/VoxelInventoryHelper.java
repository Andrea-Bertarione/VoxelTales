package dev.VoxelTales.Utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.inventory.UpdatePlayerInventory;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.Objects;

public class VoxelInventoryHelper {

    /**
     * Forces a full hotbar synchronization from Server to Client.
     * Safely handles thread-bridging to the World Thread.
     */
    public static void syncHotbar(PlayerRef playerRef, Ref<EntityStore> ref, Store<EntityStore> store) {
        World world = store.getExternalData().getWorld();

        world.execute(() -> {
            InventoryComponent hotbar = store.getComponent(ref, Objects.requireNonNull(InventoryComponent.getComponentTypeById(InventoryComponent.HOTBAR_SECTION_ID)));
            if (hotbar == null) return;

            // Generate the packet based on current Server-side state
            UpdatePlayerInventory clientPacket = new UpdatePlayerInventory();
            clientPacket.hotbar = hotbar.getInventory().toPacket();

            // Write directly to the player's connection
            playerRef.getPacketHandler().writeNoCache(clientPacket);
        });
    }
}