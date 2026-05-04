package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry;
import dev.VoxelTales.Assets.Interactions.RouterSignatureInteraction;
import dev.VoxelTales.Assets.Interactions.RouterSkillInteraction;
import dev.VoxelTales.Assets.Interactions.VoxelDamageEntityInteraction;
import dev.VoxelTales.Core.AVoxelRegistry;
import dev.VoxelTales.Registries.RegistryEnums.InteractionEnum;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelInteractionRegistry extends AVoxelRegistry<VoxelInteractionRegistry> {
    public void init(VoxelTalesPlugin plugin) {
        CodecMapRegistry.Assets<Interaction, ?> interactionCodedRegistry = plugin.getCodecRegistry(Interaction.CODEC);

        registerInteraction(interactionCodedRegistry, InteractionEnum.ROUTER_SIGNATURE_INTERACTION, RouterSignatureInteraction.class, RouterSignatureInteraction.CODEC);
        registerInteraction(interactionCodedRegistry, InteractionEnum.ROUTER_SKILL_INTERACTION, RouterSkillInteraction.class, RouterSkillInteraction.CODEC);
        registerInteraction(interactionCodedRegistry, InteractionEnum.DAMAGE_ENTITY, VoxelDamageEntityInteraction.class, VoxelDamageEntityInteraction.CODEC);

        LoggerUtil.getLogger().info("[VoxelInteractionRegistry] Registered " + super.getRegistryCount() + " interactions.");
    }

    private void registerInteraction(CodecMapRegistry.Assets<Interaction, ?> interactionCodedRegistry, InteractionEnum interactionEnum, Class<? extends Interaction> clazz, BuilderCodec<? extends Interaction> codec) {
        interactionCodedRegistry.register(interactionEnum.getName(), clazz, codec);
        super.incrementRegistryCount();
    }
}
