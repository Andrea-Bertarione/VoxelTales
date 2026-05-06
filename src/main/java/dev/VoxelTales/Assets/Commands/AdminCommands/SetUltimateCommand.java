package dev.VoxelTales.Assets.Commands.AdminCommands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Registries.VoxelSkillsRegistry;
import dev.VoxelTales.Utils.VoxelWeaponHelper;
import org.jetbrains.annotations.NotNull;

public class SetUltimateCommand extends AbstractPlayerCommand {

    public SetUltimateCommand() {
        super("setUltimate", "Set the current ultimate to the specified one by name");
    }

    private final RequiredArg<String> name =
            withRequiredArg("name", "name of the ultimate", ArgTypes.STRING);

    @Override
    protected void execute(@NotNull CommandContext commandContext, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        try {
            VoxelWeaponHelper.selectUltimate(playerRef, name.get(commandContext));
            playerRef.sendMessage(Message.raw("Ultimate set successfully to: " + VoxelSkillsRegistry.staticGetSkill(name.get(commandContext)).getDisplayName()));
        }
        catch (Exception err) {
            playerRef.sendMessage(Message.raw("Failed to set ultimate: " + err.getMessage()));
        }
    }
}
