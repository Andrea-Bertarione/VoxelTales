package dev.VoxelTales.Assets.Commands.Presets;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.UI.Pages.Default.VoxelPageUI;
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
        T page = VoxelCacheRegistry.get(this.cacheKey, playerRef, this.uiClass);

        if (page != null) {
            page.open();
        } else {
            commandContext.sendMessage(Message.parse("Failed to open UI: " + cacheKey));
        }
    }
}