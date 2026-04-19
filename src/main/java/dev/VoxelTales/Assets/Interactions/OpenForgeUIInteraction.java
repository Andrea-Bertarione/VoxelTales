package dev.VoxelTales.Assets.Interactions;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.Registries.VoxelDialogueRegistry;
import dev.VoxelTales.UI.Pages.DialoguePage;

import javax.annotation.Nonnull;

public class OpenForgeUIInteraction extends SimpleInteraction {
    public static final BuilderCodec<OpenForgeUIInteraction> CODEC = BuilderCodec.builder(OpenForgeUIInteraction.class, OpenForgeUIInteraction::new, SimpleInteraction.CODEC).build();

    @Override
    protected final void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        if (firstRun) {
            this.firstRun(type, context, cooldownHandler);
            super.tick0(firstRun, time, type, context, cooldownHandler);
        }
    }

    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        LoggerUtil.getLogger().info("Opening DialoguePage!");

        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        Player playerComponent = (Player)commandBuffer.getComponent(ref, Player.getComponentType());
        if (playerComponent != null) {
                PlayerRef playerRef = (PlayerRef)commandBuffer.getComponent(ref, PlayerRef.getComponentType());

                assert playerRef != null;

                DialoguePage page = VoxelCacheRegistry.get("DialoguePage", playerRef, DialoguePage.class);

                //LoggerUtil.getLogger().info("Opening ForgingPage for player!");

                if (page != null) {
                    page.openWith(VoxelDialogueRegistry.get("sword-sage"));
                }
            }

    }
}
