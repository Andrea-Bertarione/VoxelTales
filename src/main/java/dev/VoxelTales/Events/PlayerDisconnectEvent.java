package dev.VoxelTales.Events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

public class PlayerDisconnectEvent {
    public static void onPlayerDisconnect(com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();

        VoxelTalesPlugin.get().removeLockedSlot(playerRef.getUuid());
        VoxelTalesPlugin.get().removeWeaponHud(playerRef.getUuid());

        VoxelCacheRegistry.cleanup(playerRef.getUuid());
    }
}
