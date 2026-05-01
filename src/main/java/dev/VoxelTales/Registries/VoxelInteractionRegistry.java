package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry;
import dev.VoxelTales.Assets.Interactions.RouterSignatureInteraction;
import dev.VoxelTales.Assets.Interactions.RouterSkillInteraction;
import dev.VoxelTales.Assets.Interactions.VoxelDamageEntityInteraction;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.Registries.RegistryEnums.InteractionEnum;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelInteractionRegistry implements IVoxelRegistry {
    private static short interactionCount = 0;

    public static void init(VoxelTalesPlugin plugin) {
        CodecMapRegistry.Assets<Interaction, ?> interactionCodedRegistry = plugin.getCodecRegistry(Interaction.CODEC);

        registerInteraction(interactionCodedRegistry, InteractionEnum.ROUTER_SIGNATURE_INTERACTION, RouterSignatureInteraction.class, RouterSignatureInteraction.CODEC);
        registerInteraction(interactionCodedRegistry, InteractionEnum.ROUTER_SKILL_INTERACTION, RouterSkillInteraction.class, RouterSkillInteraction.CODEC);
        registerInteraction(interactionCodedRegistry, InteractionEnum.DAMAGE_ENTITY, VoxelDamageEntityInteraction.class, VoxelDamageEntityInteraction.CODEC);

        LoggerUtil.getLogger().info("[VoxelInteractionRegistry] Registered " + interactionCount + " interactions.");
    }

    private static void registerInteraction(CodecMapRegistry.Assets<Interaction, ?> interactionCodedRegistry, InteractionEnum interactionEnum, Class<? extends Interaction> clazz, BuilderCodec<? extends Interaction> codec) {
        interactionCodedRegistry.register(interactionEnum.getName(), clazz, codec);
        interactionCount++;
    }
}
