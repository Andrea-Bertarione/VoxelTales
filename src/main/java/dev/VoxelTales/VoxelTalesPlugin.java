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
    private final RegistryManager registryManager = new RegistryManager();

    public VoxelTalesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;

        //Init the ComponentRegistry at plugin creation to make sure that the static behavior of components is respected
        registryManager.initRegistry(VoxelComponentsRegistry.class, this);
    }

    @Nullable
    @Override
    public CompletableFuture<Void> preLoad() {
        //Init the ConfigRegistry during preLoad to allow the copying of the configuration files before loading them
        registryManager.initRegistry(VoxelConfigsRegistry.class, this);

        return super.preLoad();
    }

    @Override
    protected void setup() {
        //Register custom Metadata
        VoxelDamageMetadata.registerDamage(Damage.META_REGISTRY);
        VoxelDamageMetadata.registerInteraction(Interaction.META_REGISTRY);

        registryManager.initRegistries(List.of(
                VoxelSystemsRegistry.class,
                VoxelDamageKindsRegistry.class,
                VoxelEventsRegistry.class,
                VoxelPacketListenersRegistry.class,
                VoxelCommandsRegistry.class,
                VoxelInteractionRegistry.class,
                VoxelNPCActionsRegistry.class,
                VoxelDialoguesRegistry.class,
                VoxelCacheRegistry.class,
                VoxelSkillsRegistry.class
        ), this);
    }

    @Override
    protected void start() {
        //Run asset patching AFTER everything loaded
        VoxelAssetReflection.patch();
        VoxelDamageUIReflection.disableBuiltinCombatText(this);

        //Save the configs after everything is loaded
        registryManager.getRegistry(VoxelConfigsRegistry.class).saveAll();
    }

    public Set<Dependency<EntityStore>> dependencies = Set.of(
            new SystemDependency<>(Order.AFTER, RoleSystems.RoleActivateSystem.class)
    );

    public static VoxelTalesPlugin get() {
        return instance;
    }
    public static RegistryManager getRegistryManager() { return instance.registryManager; }

    public Config<?> registerConfig(String name, BuilderCodec<?> codec) {
        return this.withConfig(name, codec);
    }
}