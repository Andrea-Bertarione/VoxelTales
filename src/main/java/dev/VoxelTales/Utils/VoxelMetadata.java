package dev.VoxelTales.Utils;

import com.hypixel.hytale.server.core.meta.IMetaRegistry;
import com.hypixel.hytale.server.core.meta.MetaKey;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;

public class VoxelMetadata {
    // This key will store a Boolean to tell our system "Don't process this hit again"
    public static MetaKey<Boolean> PROCESSED_KEY;

    // Key to handle the interaction - damage handshake
    public static MetaKey<String> INTERACTION_SOURCE_KEY;
    public static MetaKey<String> DAMAGE_SOURCE_KEY;

    public static void registerDamage(IMetaRegistry<Damage> registry) {
        PROCESSED_KEY = registry.registerMetaObject(_ -> false);
        DAMAGE_SOURCE_KEY = registry.registerMetaObject(_ -> "chain");
    }

    public static void registerInteraction(IMetaRegistry<Interaction> registry) {
        INTERACTION_SOURCE_KEY = registry.registerMetaObject(_ -> "chain");
    }
}