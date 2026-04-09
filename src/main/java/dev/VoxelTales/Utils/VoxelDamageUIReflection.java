package dev.VoxelTales.Utils;

import dev.VoxelTales.VoxelTalesPlugin;
import java.util.logging.Level;
import static com.hypixel.hytale.logger.HytaleLogger.getLogger;

public final class VoxelDamageUIReflection {
    public static void disableBuiltinCombatText(VoxelTalesPlugin plugin) {
        try {
            Object proxy = plugin.getEntityStoreRegistry();
            java.lang.reflect.Field registryField = proxy.getClass().getDeclaredField("registry");
            registryField.setAccessible(true);
            Object registryObj = registryField.get(proxy);
            if (registryObj instanceof com.hypixel.hytale.component.ComponentRegistry<?> registry) {
                @SuppressWarnings({"rawtypes"})
                Class systemClass = Class.forName(
                        "com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems$EntityUIEvents");

                registry.unregisterSystem(systemClass);
                getLogger().at(Level.INFO).log("Successfully disabled built-in combat text system.");
            }
        } catch (Throwable t) {
            getLogger().at(Level.SEVERE).withCause(t).log("Unable to disable built-in combat text system.");
        }
    }
}
