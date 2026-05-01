package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.PacketListeners.WeaponActivationListener;
import dev.VoxelTales.PacketListeners.WeaponMoveListener;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelPacketListenersRegistry implements IVoxelRegistry {
    private static Short PacketAdaptersCount = 0;

    public static void init(VoxelTalesPlugin plugin) {
        registerInbound(new WeaponMoveListener());
        registerInbound(new WeaponActivationListener());

        LoggerUtil.getLogger().info("[VoxelPacketListenersRegistry] Registered " + PacketAdaptersCount + " packet listeners.");
    }

    private static void registerInbound(PlayerPacketFilter filter) {
        PacketAdapters.registerInbound(filter);
        PacketAdaptersCount++;
    }
}
