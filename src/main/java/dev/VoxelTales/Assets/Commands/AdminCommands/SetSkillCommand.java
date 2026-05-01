package dev.VoxelTales.Assets.Commands.AdminCommands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.PlayerComponents.WeaponHandlerComponent;
import dev.VoxelTales.VoxelTalesPlugin;
import org.jetbrains.annotations.NotNull;

public class SetSkillCommand extends AbstractPlayerCommand {

    public SetSkillCommand() {
        super("setSkill", "Set the current skill to the specified one by name");
    }

    private final RequiredArg<String> name =
            withRequiredArg("name", "name of the skill", ArgTypes.STRING);

    @Override
    protected void execute(@NotNull CommandContext commandContext, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        WeaponHandlerComponent weaponHandlerComponent = store.ensureAndGetComponent(ref, WeaponHandlerComponent.getComponentType());

        weaponHandlerComponent.setSelectedSkill(name.get(commandContext));
    }
}
