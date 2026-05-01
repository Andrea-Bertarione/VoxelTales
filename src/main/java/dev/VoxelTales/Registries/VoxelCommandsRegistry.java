package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import dev.VoxelTales.Assets.Commands.ChangeSlotCommand;
import dev.VoxelTales.Assets.Commands.VoxelAdminCommandCollection;
import dev.VoxelTales.Assets.Commands.VoxelShowCurrentStats;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelCommandsRegistry implements IVoxelRegistry {
    private static Short commandCount = 0;

    public static void init(VoxelTalesPlugin plugin) {
        CommandRegistry commandRegistry = plugin.getCommandRegistry();

        registerCommand(commandRegistry, new ChangeSlotCommand());
        registerCommand(commandRegistry, new VoxelShowCurrentStats());
        registerCommand(commandRegistry, new VoxelAdminCommandCollection());

        LoggerUtil.getLogger().info("[VoxelCommandsRegistry] Registered " + commandCount + " commands.");
    }

    private static void registerCommand(CommandRegistry commandRegistry, AbstractCommand command) {
        commandRegistry.registerCommand(command);
        commandCount++;
    }
}
