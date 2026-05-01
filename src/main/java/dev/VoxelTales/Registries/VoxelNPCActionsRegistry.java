package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import dev.VoxelTales.Assets.Actions.Builders.BuilderOpenDialogueAction;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.Registries.RegistryEnums.ActionEnum;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.function.Supplier;

public class VoxelNPCActionsRegistry implements IVoxelRegistry {
    private static short actionCount = 0;

    public static void init(VoxelTalesPlugin plugin) {
        NPCPlugin npcPlugin = NPCPlugin.get();

        newAction(npcPlugin, ActionEnum.OPEN_DIALOGUE_ACTION, BuilderOpenDialogueAction::new);

        LoggerUtil.getLogger().info("[VoxelNPCActionsRegistry] Registered " + actionCount + " actions.");
    }

    private static <T> void newAction(NPCPlugin npcPlugin, ActionEnum actionEnum, Supplier<Builder<T>> clazz) {
        npcPlugin.registerCoreComponentType(actionEnum.getName(), clazz);
        actionCount++;
    }
}
