package dev.VoxelTales.Utils;

import com.hypixel.hytale.server.core.meta.IMetaRegistry;
import com.hypixel.hytale.server.core.meta.MetaKey;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCalculatorSystems;

public class VoxelMetadata {
    // This key will store a Boolean to tell our system "Don't process this hit again"
    public static MetaKey<Boolean> PROCESSED_KEY;

    public static void register(IMetaRegistry<Damage> registry) {
        PROCESSED_KEY = registry.registerMetaObject(_ -> false);
    }
}