package dev.VoxelTales.PacketListeners;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.UI.HUD.WeaponHUD;
import dev.VoxelTales.VoxelTalesPlugin;

import javax.annotation.Nonnull;

public class WeaponActivationListener implements PlayerPacketFilter {

    @Override
    public boolean test(@Nonnull PlayerRef playerRef, @Nonnull Packet packet) {
        // Step 1: Only intercept the workhorse interaction packet
        if (!(packet instanceof SyncInteractionChains syncPacket)) {
            return false;
        }

        Short lockedSlot = VoxelCacheRegistry.staticGetSynced(CacheEnum.SLOT_CACHE, playerRef.getUuid(), Short.class);
        if (lockedSlot == null) { return false; }

        // Step 2: Search the interaction updates for slot swaps
        for (SyncInteractionChain chain : syncPacket.updates) {
            if (chain.interactionType == InteractionType.SwapFrom
                    && chain.data != null
                    && chain.initial) {

                if (chain.data.targetSlot == lockedSlot) {
                    handleWeaponDrawn(playerRef);
                }
                else {
                    handleWeaponSheath(playerRef);
                }
            }
        }
        return false;
    }

    private void handleWeaponDrawn(PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) return;

        WeaponHUD weaponHUD = VoxelCacheRegistry.staticGet(CacheEnum.HUD_CACHE, playerRef, WeaponHUD.class);
        World world = ref.getStore().getExternalData().getWorld();

        world.execute(weaponHUD::show);

    }

    private void handleWeaponSheath(PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) return;

        WeaponHUD weaponHUD = VoxelCacheRegistry.staticGet(CacheEnum.HUD_CACHE, playerRef, WeaponHUD.class);
        weaponHUD.hide();
    }
}