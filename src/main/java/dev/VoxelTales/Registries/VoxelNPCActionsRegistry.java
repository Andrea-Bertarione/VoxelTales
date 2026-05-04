package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import dev.VoxelTales.Assets.Actions.Builders.BuilderOpenDialogueAction;
import dev.VoxelTales.Core.AVoxelRegistry;
import dev.VoxelTales.Registries.RegistryEnums.ActionEnum;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.function.Supplier;

public class VoxelNPCActionsRegistry extends AVoxelRegistry<VoxelNPCActionsRegistry> {
    public void init(VoxelTalesPlugin plugin) {
        NPCPlugin npcPlugin = NPCPlugin.get();

        newAction(npcPlugin, ActionEnum.OPEN_DIALOGUE_ACTION, BuilderOpenDialogueAction::new);

        LoggerUtil.getLogger().info("[VoxelNPCActionsRegistry] Registered " + super.getRegistryCount() + " actions.");
    }

    private <T> void newAction(NPCPlugin npcPlugin, ActionEnum actionEnum, Supplier<Builder<T>> clazz) {
        npcPlugin.registerCoreComponentType(actionEnum.getName(), clazz);
        super.incrementRegistryCount();
    }
}
