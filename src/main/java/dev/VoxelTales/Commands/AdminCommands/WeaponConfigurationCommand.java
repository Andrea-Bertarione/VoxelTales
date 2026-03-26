package dev.VoxelTales.Commands.AdminCommands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.UI.WeaponConfigurationPage;
import dev.VoxelTales.Utils.VoxelCacheRegistry;
import org.jetbrains.annotations.NotNull;

public class WeaponConfigurationCommand extends AbstractPlayerCommand {
    public WeaponConfigurationCommand() {
        super("config", "Command to open the mod configuration menu");
    }

    @Override
    protected void execute(@NotNull CommandContext commandContext, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        WeaponConfigurationPage weaponConfigurationPage = VoxelCacheRegistry.get("WeaponConfigurationPage", playerRef, WeaponConfigurationPage.class);

        weaponConfigurationPage.open();
    }
}
