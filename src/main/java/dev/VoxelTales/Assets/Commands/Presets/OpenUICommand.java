package dev.VoxelTales.Assets.Commands.Presets;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.UI.Default.VoxelPageUI;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import org.jetbrains.annotations.NotNull;

public class OpenUICommand<T extends VoxelPageUI> extends AbstractPlayerCommand {
    private final String cacheKey;
    private final Class<T> uiClass;

    public OpenUICommand(String commandName, String description, String cacheKey, Class<T> uiClass) {
        super(commandName, description);
        this.cacheKey = cacheKey;
        this.uiClass = uiClass;
    }

    @Override
    protected void execute(@NotNull CommandContext commandContext, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        // Fetch from your registry using the class type
        T page = VoxelCacheRegistry.get(this.cacheKey, playerRef, this.uiClass);

        if (page != null) {
            page.open();
        } else {
            // Optional: Log a warning if the UI failed to initialize
            commandContext.sendMessage(Message.parse("Failed to open UI: " + cacheKey));
        }
    }
}