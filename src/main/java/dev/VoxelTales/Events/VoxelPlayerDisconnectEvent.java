package dev.VoxelTales.Events;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelPlayerDisconnectEvent {
    public static void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();

        VoxelTalesPlugin.get().removeLockedSlot(playerRef.getUuid());
        VoxelTalesPlugin.get().removeWeaponHud(playerRef.getUuid());

        VoxelCacheRegistry.cleanup(playerRef.getUuid());
    }
}
