package dev.VoxelTales.Utils;

import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import dev.VoxelTales.VoxelTalesPlugin;

public class EntityXPConfigsHelper {
    public static int getMobXP(String npcId, EntityStatMap stats) {
        var wrapper = VoxelTalesPlugin.get().getEntityXpConfigs();
        return wrapper.get().getOrGenerateXP(npcId, stats, wrapper::save);
    }
}
