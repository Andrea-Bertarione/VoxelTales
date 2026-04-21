package dev.VoxelTales.Assets.Commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import dev.VoxelTales.Assets.Commands.AdminCommands.*;
import dev.VoxelTales.Assets.Commands.Presets.OpenUICommand;
import dev.VoxelTales.UI.Pages.DialoguePage;
import dev.VoxelTales.UI.Pages.WeaponConfigurationPage;
import dev.VoxelTales.UI.Pages.WeaponForgerPage;

public class VoxelAdminCommandCollection extends AbstractCommandCollection {
    public VoxelAdminCommandCollection() {
        super("voxel", "Collection of admin commands");

        addSubCommand(new GiveXPCommand());
        addSubCommand(new SetBladeCommand());
        addSubCommand(new SetHandleCommand());
        addSubCommand(new SetSkillCommand());
        addSubCommand(new SpawnSwordSage());

        addSubCommand(new OpenUICommand<>(
                "config",
                "Command to open the mod configuration menu",
                "WeaponConfigurationPage",
                WeaponConfigurationPage.class
        ));

        addSubCommand(new OpenUICommand<>(
                "forger",
                "Command to open the weapon forger menu",
                "WeaponForgerPage",
                WeaponForgerPage.class
        ));

        addSubCommand(new OpenUICommand<>(
                "dialogue",
                "Command to open the dialogue menu",
                "DialoguePage",
                DialoguePage.class
        ));
    }
}
