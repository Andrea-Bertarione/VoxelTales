package dev.VoxelTales.Assets.Commands;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Utils.VoxelStatsHelper;
import kotlin.Pair;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoxelShowCurrentStats extends AbstractPlayerCommand {
    public VoxelShowCurrentStats() {
        super("ShowStats", "Command to show the current stats of the player");
    }

    @Override
    protected void execute(
            @NotNull CommandContext context,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef,
            @NonNullDecl World world
    ) {
        EntityStatMap statMap = store.ensureAndGetComponent(ref, EntityStatMap.getComponentType());
        List<Pair<String, Float>> stats = VoxelStatsHelper.getAllStats(statMap);

        Message msg = Message.empty();
        stats.forEach(val -> {
            if (val.getSecond() == 0f) return;

            msg.insert(val + "\n");
        });

        playerRef.sendMessage(msg);
    }
}
