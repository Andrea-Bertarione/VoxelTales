package dev.VoxelTales.Utils;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.VoxelTales.Configs.EntityXPConfigs;
import dev.VoxelTales.Registries.VoxelConfigsRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelXPConfigsHelper {
    public static int getMobXP(NPCEntity npcEntity, EntityStatMap stats) {
        return EntityXPConfigs.get().getOrGenerateXP(npcEntity, stats, () -> VoxelConfigsRegistry.save(EntityXPConfigs.class));
    }
}
