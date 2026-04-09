package dev.VoxelTales.Utils;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelXPConfigsHelper {
    public static int getMobXP(NPCEntity npcEntity, EntityStatMap stats) {
        var wrapper = VoxelTalesPlugin.get().getEntityXpConfigs();
        return wrapper.get().getOrGenerateXP(npcEntity, stats, wrapper::save);
    }
}
