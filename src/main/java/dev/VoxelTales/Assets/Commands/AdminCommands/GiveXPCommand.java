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
import dev.VoxelTales.Controllers.LevelingController;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;

public class GiveXPCommand extends AbstractPlayerCommand {
    public GiveXPCommand() {
        super("giveXP", "Command to to set sword XP");
    }

    private final RequiredArg<Integer> xp =
            withRequiredArg("xp", "amount of xp", ArgTypes.INTEGER);

    @Override
    protected void execute(
            @NotNull CommandContext context,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef,
            @NonNullDecl World world
    ) {

        LevelingController.incrementXP(store, ref, xp.get(context));
        context.sendMessage(Message.raw("Given " + xp.get(context) + " sword xp"));
    }
}
