package dev.VoxelTales.Assets.Interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.RunRootInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.PlayerComponents.WeaponHandlerComponent;
import dev.VoxelTales.Registries.MetaData.VoxelDamageMetadata;
import dev.VoxelTales.VoxelTalesPlugin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class RouterSkillInteraction extends RunRootInteraction {
    public static final BuilderCodec<RouterSkillInteraction> CODEC = BuilderCodec.builder(
            RouterSkillInteraction.class, RouterSkillInteraction::new, RunRootInteraction.CODEC
    ).build();

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        context.getState().state = InteractionState.Finished;

        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();

        WeaponHandlerComponent weaponHandlerComponent = store.getComponent(ref, WeaponHandlerComponent.getComponentType());
        if (weaponHandlerComponent == null) {
            context.execute(Objects.requireNonNull(RootInteraction.getAssetMap().getAsset(this.rootInteraction)));
            return;
        }

        context.getMetaStore().putMetaObject(VoxelDamageMetadata.INTERACTION_SOURCE_KEY, "Skill");

        context.execute(Objects.requireNonNull(RootInteraction.getAssetMap().getAsset(weaponHandlerComponent.getSelectedSkill())));
    }
}
