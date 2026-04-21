package dev.VoxelTales.PacketListeners;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.inventory.DropItemStack;
import com.hypixel.hytale.protocol.packets.inventory.MoveItemStack;
import com.hypixel.hytale.protocol.packets.inventory.SmartMoveItemStack;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import dev.VoxelTales.Utils.VoxelInventoryHelper;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.Objects;

public class WeaponMoveListener {

    public static PlayerPacketFilter weaponFilter() {
        return (playerRef, packet) -> {
            short lockedSlot = VoxelTalesPlugin.get().getLockedSlot(playerRef.getUuid());
            boolean shouldBlock = isShouldBlock(packet, lockedSlot);

            if (shouldBlock) {
                VoxelInventoryHelper.syncHotbar(playerRef, playerRef.getReference(),
                        Objects.requireNonNull(playerRef.getReference()).getStore());
                return true; // Cancel the restricted move
            }

            return false; // Let all other inventory interactions pass
        };
    }

    private static boolean isShouldBlock(Packet packet, short lockedSlot) {
        boolean shouldBlock = false;
        int sectionId = -1;

        if (packet instanceof DropItemStack p && p.inventorySectionId == sectionId) {
            /*
            LoggerUtil.getLogger().info(String.format(
                    "[PACKET DEBUG] Section %d, Slot %d",
                    p.inventorySectionId, p.slotId
            ));
             */
            shouldBlock = (p.slotId == lockedSlot);
        }
        else if (packet instanceof MoveItemStack p && (p.fromSectionId == sectionId || p.toSectionId == sectionId)) {
            // ONLY block if the 'from' or 'to' matches the restricted slot
            shouldBlock = (p.fromSlotId == lockedSlot || p.toSlotId == lockedSlot);
        }
        else if (packet instanceof SmartMoveItemStack p && p.fromSectionId == sectionId) {
            // Shift-clicking out of the restricted slot
            shouldBlock = (p.fromSlotId == lockedSlot);
        }
        return shouldBlock;
    }
}