package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.event.IBaseEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import dev.VoxelTales.Events.VoxelAddWorldEvent;
import dev.VoxelTales.Events.VoxelPlayerDisconnectEvent;
import dev.VoxelTales.Events.VoxelPlayerReadyEvent;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.function.Consumer;

public class VoxelEventsRegistry implements IVoxelRegistry {
    private static short eventCount = 0;

    public static void init(VoxelTalesPlugin plugin) {
        EventRegistry eventRegistry = plugin.getEventRegistry();

        register(eventRegistry, PlayerReadyEvent.class, VoxelPlayerReadyEvent::onPlayerReady);
        register(eventRegistry, PlayerDisconnectEvent.class, VoxelPlayerDisconnectEvent::onPlayerDisconnect);
        register(eventRegistry, AddWorldEvent.class, VoxelAddWorldEvent::onAddWorldEvent);

        LoggerUtil.getLogger().info("[VoxelEventsRegistry] Registered " + eventCount + " events.");
    }

    private static <K, E extends IBaseEvent<K>> void register(
            EventRegistry eventRegistry,
            Class<E> eventClass,
            Consumer<E> handler) {

        eventRegistry.registerGlobal(eventClass, handler);
        eventCount++;
    }
}
