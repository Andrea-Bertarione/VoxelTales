package dev.VoxelTales.Registries;

import dev.VoxelTales.Controllers.DialogueController;

import java.util.concurrent.ConcurrentHashMap;

public class VoxelDialogueRegistry {
    private static final ConcurrentHashMap<String, DialogueController.DialogueNode> dialogueRegistry = new ConcurrentHashMap<>();

    public static void register(String key, DialogueController.DialogueNode dialogueNode) {
        dialogueRegistry.put(key, dialogueNode);
    }

    public static DialogueController.DialogueNode get(String key) {
        return dialogueRegistry.get(key);
    }

    public static void clear() {
        dialogueRegistry.clear();
    }
}
