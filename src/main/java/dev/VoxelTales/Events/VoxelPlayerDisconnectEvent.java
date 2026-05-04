package dev.VoxelTales.Events;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelPlayerDisconnectEvent {
    public static void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();

        VoxelCacheRegistry.staticCleanup(playerRef.getUuid());
    }
}
