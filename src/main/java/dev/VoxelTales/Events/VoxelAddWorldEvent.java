package dev.VoxelTales.Events;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.Controllers.SwordSageController;
import dev.VoxelTales.Registries.VoxelConfigsRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.concurrent.TimeUnit;

public class VoxelAddWorldEvent {

    //Delay to avoid interaction bugs
    private static final int SWORD_SAGE_SPAWN_DELAY = 10;
    private static final Vector3d SWORD_SAGE_POSITION_OFFSET = new Vector3d(11, 1, 2);

    public static void onAddWorldEvent(AddWorldEvent event) {
        LoggerUtil.getLogger().info("[Sword_Sage] AddWorldEvent triggered for world " + event.getWorld().getWorldConfig().getUuid());
        World world = event.getWorld();

        VoxelTalesConfigs configs = VoxelTalesConfigs.get();

        if (configs.isServerSetUP()) {
            LoggerUtil.getLogger().warning("[Sword_Sage] World already set up!");
            return;
        }

        HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> world.execute(() -> {
            ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
            if (spawnProvider == null) {
                LoggerUtil.getLogger().warning("[Sword_Sage] World spawn provider is null!");
                return;
            }

            Vector3d basePosition = spawnProvider.getSpawnPoint(world, world.getWorldConfig().getUuid()).getPosition();
            SwordSageController.spawnSwordSage(world, basePosition.add(SWORD_SAGE_POSITION_OFFSET));

            configs.setServerSetUP(true);
            VoxelConfigsRegistry.save(VoxelTalesConfigs.class);

        }), SWORD_SAGE_SPAWN_DELAY, TimeUnit.SECONDS);
    }
}

