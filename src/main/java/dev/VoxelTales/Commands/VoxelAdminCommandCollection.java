package dev.VoxelTales.Commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import dev.VoxelTales.Commands.AdminCommands.*;

public class VoxelAdminCommandCollection extends AbstractCommandCollection {
    public VoxelAdminCommandCollection() {
        super("voxel", "Collection of admin commands");

        addSubCommand(new GiveXPCommand());
        addSubCommand(new SetBladeCommand());
        addSubCommand(new SetHandleCommand());
        addSubCommand(new SetSkillCommand());
        addSubCommand(new WeaponConfigurationCommand());
    }
}
