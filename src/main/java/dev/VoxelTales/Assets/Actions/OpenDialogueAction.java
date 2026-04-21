package dev.VoxelTales.Assets.Actions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import dev.VoxelTales.Assets.Actions.Builders.BuilderOpenDialogueAction;
import dev.VoxelTales.Assets.Dialogues.Flags.SwordSageFlags;
import dev.VoxelTales.Components.DialogueStateComponent;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.Registries.VoxelDialogueRegistry;
import dev.VoxelTales.UI.Pages.DialoguePage;
import dev.VoxelTales.VoxelTalesPlugin;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

/**
 * <p>TODO: Add JavaDoc documentation here.</p>
 */
public class OpenDialogueAction extends ActionBase {
    /* TODO: Add private data variables to store for OpenDialogueAction. */
    public OpenDialogueAction(@NonNullDecl BuilderOpenDialogueAction builder, BuilderSupport builderSupport) {
        super(builder);
    }

    // Returns false if this Action is blocking execution.
    @Override
    public boolean canExecute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        return super.canExecute(ref, role, sensorInfo, dt, store);
    }

    // Returns false if this Action will block the next Action.
    @Override
    public boolean execute(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Role role, InfoProvider sensorInfo, double dt, @NonNullDecl Store<EntityStore> store) {
        if (!super.execute(ref, role, sensorInfo, dt, store))
            return false;

        Ref<EntityStore> playerReference = role.getStateSupport().getInteractionIterationTarget();
        if (playerReference == null) {
            return false;
        } else {
            PlayerRef playerRefComponent = (PlayerRef)store.getComponent(playerReference, PlayerRef.getComponentType());
            if (playerRefComponent == null) {
                return false;
            } else {
                Player playerComponent = (Player)store.getComponent(playerReference, Player.getComponentType());
                if (playerComponent == null) {
                    return false;
                } else {
                    DialoguePage page = VoxelCacheRegistry.get("DialoguePage", playerRefComponent, DialoguePage.class);
                    DialogueStateComponent dialogueStateComponent = store.ensureAndGetComponent(playerReference, VoxelTalesPlugin.get().getDialogueStateComponent());

                    if (page != null) {
                        page.openWith(VoxelDialogueRegistry.get("sword-sage" +
                                (dialogueStateComponent.hasFlag(SwordSageFlags.EXHAUSTED_SWORD_SAGE)
                                        ? "" : "-intro")));
                    }
                    else {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}