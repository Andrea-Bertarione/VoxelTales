package dev.VoxelTales.Utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.DialogueStateComponent;
import dev.VoxelTales.VoxelTalesPlugin;

public class VoxelFlagHelper {
    public static boolean hasFlag(PlayerRef playerRef, String flag) {
        if (playerRef == null || flag == null || flag.isBlank()) {
            return false;
        }

        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return false;
        }

        Store<EntityStore> store = ref.getStore();
        DialogueStateComponent dialogueStateComponent =
                store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getDialogueStateComponent());

        return dialogueStateComponent.hasFlag(flag);
    }

    public static void setFlag(PlayerRef playerRef, String flag, boolean value) {
        if (playerRef == null || flag == null || flag.isBlank()) {
            return;
        }

        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }

        Store<EntityStore> store = ref.getStore();
        DialogueStateComponent dialogueStateComponent =
                store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getDialogueStateComponent());

        dialogueStateComponent.setFlag(flag, value);
    }

    public static void clearFlag(PlayerRef playerRef, String flag) {
        if (playerRef == null || flag == null || flag.isBlank()) {
            return;
        }

        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }

        Store<EntityStore> store = ref.getStore();
        DialogueStateComponent dialogueStateComponent =
                store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getDialogueStateComponent());

        dialogueStateComponent.clearFlag(flag);
    }

    public static void toggleFlag(PlayerRef playerRef, String flag) {
        if (playerRef == null || flag == null || flag.isBlank()) {
            return;
        }

        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            return;
        }

        Store<EntityStore> store = ref.getStore();
        DialogueStateComponent dialogueStateComponent =
                store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getDialogueStateComponent());

        dialogueStateComponent.toggleFlag(flag);
    }
}