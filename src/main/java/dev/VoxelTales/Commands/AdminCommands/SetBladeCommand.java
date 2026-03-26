package dev.VoxelTales.Commands.AdminCommands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.VoxelPlayerComponent;
import dev.VoxelTales.Components.WeaponHandlerComponent;
import dev.VoxelTales.Utils.SwordFactory;
import dev.VoxelTales.VoxelTalesPlugin;
import org.jetbrains.annotations.NotNull;

public class SetBladeCommand extends AbstractPlayerCommand {

    public SetBladeCommand() {
        super("setBlade", "Set the current blade to the specified one by name");
    }

    private final RequiredArg<String> name =
            withRequiredArg("name", "name of the blade", ArgTypes.STRING);

    @Override
    protected void execute(@NotNull CommandContext commandContext, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        VoxelPlayerComponent playerComponent = store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getVoxelPlayerComponent());
        WeaponHandlerComponent weaponHandlerComponent = store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getWeaponHandlerComponent());

        weaponHandlerComponent.setCurrentBlade(name.get(commandContext));
        SwordFactory.setVoxelWeaponStack(store, ref, playerComponent, weaponHandlerComponent);
    }
}
