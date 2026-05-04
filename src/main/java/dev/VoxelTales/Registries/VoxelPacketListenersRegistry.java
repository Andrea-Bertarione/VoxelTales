package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import dev.VoxelTales.Core.AVoxelRegistry;
import dev.VoxelTales.PacketListeners.WeaponActivationListener;
import dev.VoxelTales.PacketListeners.WeaponMoveListener;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelPacketListenersRegistry extends AVoxelRegistry<VoxelPacketListenersRegistry> {
    public void init(VoxelTalesPlugin plugin) {
        registerInbound(new WeaponMoveListener());
        registerInbound(new WeaponActivationListener());

        LoggerUtil.getLogger().info("[VoxelPacketListenersRegistry] Registered " + super.getRegistryCount() + " packet listeners.");
    }

    private void registerInbound(PlayerPacketFilter filter) {
        PacketAdapters.registerInbound(filter);
        super.incrementRegistryCount();
    }
}
