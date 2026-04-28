package dev.VoxelTales.Registries;

import dev.VoxelTales.Assets.Dialogues.DialogKey;
import dev.VoxelTales.Controllers.DialogueController;

import java.util.concurrent.ConcurrentHashMap;

public class VoxelDialogueRegistry {
    private static final ConcurrentHashMap<String, DialogueController.DialogueNode> dialogueRegistry = new ConcurrentHashMap<>();

    public static void register(DialogKey key, DialogueController.DialogueNode dialogueNode) {
        dialogueRegistry.put(String.valueOf(key), dialogueNode);
    }

    public static DialogueController.DialogueNode get(String key) {
        return dialogueRegistry.get(key);
    }

    @Deprecated
    public static void register(String key, DialogueController.DialogueNode dialogueNode) {
        dialogueRegistry.put(key, dialogueNode);
    }

    @Deprecated
    public static DialogueController.DialogueNode get(DialogKey key) { return dialogueRegistry.get(String.valueOf(key)); }

    public static void clear() {
        dialogueRegistry.clear();
    }
}
