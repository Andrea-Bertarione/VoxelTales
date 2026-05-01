package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import dev.VoxelTales.Assets.Dialogues.DialogKey;
import dev.VoxelTales.Assets.Dialogues.SwordSage.SwordSageIntroDialogue;
import dev.VoxelTales.Assets.Dialogues.SwordSage.SwordSagePostQuestDialogue;
import dev.VoxelTales.Assets.Dialogues.SwordSage.SwordSagePreQuestDialogue;
import dev.VoxelTales.Assets.Dialogues.SwordSage.SwordSageRepeatedDialogue;
import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.concurrent.ConcurrentHashMap;

public class VoxelDialoguesRegistry implements IVoxelRegistry {
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

    public static void init(VoxelTalesPlugin plugin) {
        SwordSageIntroDialogue.register();
        SwordSagePreQuestDialogue.register();
        SwordSageRepeatedDialogue.register();
        SwordSagePostQuestDialogue.register();

        LoggerUtil.getLogger().info("[VoxelDialogueRegistry] Registered " + dialogueRegistry.size() + " dialogues.");
    }
}
