package dev.VoxelTales.Assets.Dialogues;

import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Registries.VoxelDialogueRegistry;

import static dev.VoxelTales.Assets.Dialogues.RepeatedDialogues.DEFAULT_CLOSE_RESPONSE;
import static dev.VoxelTales.Assets.Dialogues.RepeatedDialogues.DEFAULT_OPEN_FORGE_RESPONSE;

public class SwordSageDialogue {
    private static final String INTRO_TEXT = "Greetings, dear soul, i'm at your service.";

    public static void register() {
        // Root dialogue line shown when the player first opens the conversation.
        DialogueController.DialogueNode introNode = DialogueController.DialogueNode.root(INTRO_TEXT);

        introNode.addResponse(DEFAULT_CLOSE_RESPONSE);
        introNode.addResponse(DEFAULT_OPEN_FORGE_RESPONSE);

        VoxelDialogueRegistry.register("sword-sage", introNode);
    }
}
