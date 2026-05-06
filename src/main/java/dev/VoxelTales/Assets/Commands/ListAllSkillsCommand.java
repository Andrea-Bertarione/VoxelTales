package dev.VoxelTales.Assets.Commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Configs.VoxelSkillConfigs;
import dev.VoxelTales.Registries.VoxelSkillsRegistry;
import org.jetbrains.annotations.NotNull;

public class ListAllSkillsCommand extends AbstractPlayerCommand {

    public ListAllSkillsCommand() {
        super("listAllSkills", "List all available skills");
    }

    @Override
    protected void execute(@NotNull CommandContext commandContext, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        playerRef.sendMessage(Message.raw("Available skills:"));
        for (VoxelSkillConfigs.SkillDefinition skill : VoxelSkillsRegistry.staticGetSkills()) {
            playerRef.sendMessage(Message.raw("- " + skill.getDisplayName() + "(" + skill.getName() + ")"));
        }
    }
}