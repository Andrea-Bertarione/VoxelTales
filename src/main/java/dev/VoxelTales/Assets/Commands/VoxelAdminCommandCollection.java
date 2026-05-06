package dev.VoxelTales.Assets.Commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import dev.VoxelTales.Assets.Commands.AdminCommands.*;
import dev.VoxelTales.Assets.Commands.Presets.OpenUICommand;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.UI.Pages.DialoguePage;
import dev.VoxelTales.UI.Pages.WeaponConfigurationPage;
import dev.VoxelTales.UI.Pages.WeaponForgerPage;

public class VoxelAdminCommandCollection extends AbstractCommandCollection {
    public VoxelAdminCommandCollection() {
        super("voxel", "Collection of admin commands");

        registerAdminCommands();
        registerUICommands();
    }

    private void registerAdminCommands() {
        addSubCommand(new GiveXPCommand());
        addSubCommand(new SetBladeCommand());
        addSubCommand(new SetHandleCommand());
        addSubCommand(new SetSkillCommand());
        addSubCommand(new SetUltimateCommand());
        addSubCommand(new SpawnSwordSage());
    }

    private void registerUICommands() {
        addSubCommand(new OpenUICommand<>("config", "Open mod configuration", CacheEnum.WEAPON_CONFIGURATION_PAGE, WeaponConfigurationPage.class));
        addSubCommand(new OpenUICommand<>("forger", "Open weapon forger", CacheEnum.WEAPON_FORGER_PAGE, WeaponForgerPage.class));
        addSubCommand(new OpenUICommand<>("dialogue", "Open dialogue menu", CacheEnum.DIALOGUE_PAGE, DialoguePage.class));
    }
}