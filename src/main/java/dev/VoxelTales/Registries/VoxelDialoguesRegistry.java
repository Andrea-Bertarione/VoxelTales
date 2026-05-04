package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import dev.VoxelTales.Assets.Dialogues.DialogKey;
import dev.VoxelTales.Assets.Dialogues.SwordSage.SwordSageIntroDialogue;
import dev.VoxelTales.Assets.Dialogues.SwordSage.SwordSagePostQuestDialogue;
import dev.VoxelTales.Assets.Dialogues.SwordSage.SwordSagePreQuestDialogue;
import dev.VoxelTales.Assets.Dialogues.SwordSage.SwordSageRepeatedDialogue;
import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Core.AVoxelRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.concurrent.ConcurrentHashMap;

public class VoxelDialoguesRegistry extends AVoxelRegistry<VoxelDialoguesRegistry> {
    private final ConcurrentHashMap<String, DialogueController.DialogueNode> dialogueRegistry = new ConcurrentHashMap<>();

    private static VoxelDialoguesRegistry INSTANCE;

    public void register(DialogKey key, DialogueController.DialogueNode dialogueNode) {
        dialogueRegistry.put(String.valueOf(key), dialogueNode);
        super.incrementRegistryCount();
    }

    public DialogueController.DialogueNode get(String key) {
        return dialogueRegistry.get(key);
    }

    @Deprecated
    public void register(String key, DialogueController.DialogueNode dialogueNode) {
        dialogueRegistry.put(key, dialogueNode);
        super.incrementRegistryCount();
    }

    @Deprecated
    public DialogueController.DialogueNode get(DialogKey key) { return dialogueRegistry.get(String.valueOf(key)); }

    public void clear() {
        dialogueRegistry.clear();
    }

    public void init(VoxelTalesPlugin plugin) {
        INSTANCE = this;

        SwordSageIntroDialogue.register();
        SwordSagePreQuestDialogue.register();
        SwordSageRepeatedDialogue.register();
        SwordSagePostQuestDialogue.register();

        LoggerUtil.getLogger().info("[VoxelDialogueRegistry] Registered " + super.getRegistryCount() + " dialogues.");
    }

    // Static direct access methods
    public static DialogueController.DialogueNode staticGet(DialogKey key) {
        return INSTANCE.get(key);
    }

    public static void staticRegister(DialogKey key, DialogueController.DialogueNode dialogueNode) {
        INSTANCE.register(key, dialogueNode);
    }
}
