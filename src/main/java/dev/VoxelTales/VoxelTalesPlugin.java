package dev.VoxelTales;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.npc.systems.RoleSystems;
import dev.VoxelTales.Registries.*;
import dev.VoxelTales.Registries.MetaData.VoxelDamageMetadata;
import dev.VoxelTales.Utils.Reflections.VoxelAssetReflection;
import dev.VoxelTales.Utils.Reflections.VoxelDamageUIReflection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class VoxelTalesPlugin extends JavaPlugin {
    private static VoxelTalesPlugin instance;

    public VoxelTalesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;

        VoxelConfigsRegistry.saveAll();
    }

    @Nullable
    @Override
    public CompletableFuture<Void> preLoad() {
        VoxelConfigsRegistry.init(this);

        return super.preLoad();
    }

    @Override
    protected void setup() {
        //Register custom Metadata
        VoxelDamageMetadata.registerDamage(Damage.META_REGISTRY);
        VoxelDamageMetadata.registerInteraction(Interaction.META_REGISTRY);

        //Register Damage Kinds
        VoxelDamageKindsRegistry.init(this);

        //Register components
        VoxelComponentsRegistry.init(this);

        //Register events
        VoxelEventsRegistry.init(this);

        //Register systems
        VoxelSystemsRegistry.init(this);

        //Register packet listeners
        VoxelPacketListenersRegistry.init(this);

        //Register Commands
        VoxelCommandsRegistry.init(this);

        //Register Interactions
        VoxelInteractionRegistry.init(this);

        //Register Actions
        VoxelNPCActionsRegistry.init(this);

        //Register Caches
        VoxelCacheRegistry.init(this);

        //Register Dialogues
        VoxelDialoguesRegistry.init(this);
    }

    @Override
    protected void start() {
        //Run asset patching AFTER everything loaded
        VoxelAssetReflection.patch();
        VoxelDamageUIReflection.disableBuiltinCombatText(this);
    }

    public Set<Dependency<EntityStore>> dependencies = Set.of(
            new SystemDependency<>(Order.AFTER, RoleSystems.RoleActivateSystem.class)
    );

    public static VoxelTalesPlugin get() {
        return instance;
    }

    public Config<?> registerConfig(String name, BuilderCodec<?> codec) {
        return this.withConfig(name, codec);
    }
}