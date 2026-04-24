package dev.VoxelTales.Assets.Dialogues;

import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Registries.VoxelDialogueRegistry;

import static dev.VoxelTales.Assets.Dialogues.DefaultDialogues.DEFAULT_CLOSE_RESPONSE;
import static dev.VoxelTales.Assets.Dialogues.DefaultDialogues.DEFAULT_OPEN_FORGE_RESPONSE;

public class SwordSageRepeatedDialogue {
    public static final String INTRO_TEXT = "Greetings, dear soul, i'm at your service.";

    public static void register() {
        DialogueController.DialogueNode introNode = DialogueController.DialogueNode.root(SwordSageRepeatedDialogue.INTRO_TEXT);

        introNode.addResponse(DEFAULT_CLOSE_RESPONSE);
        introNode.addResponse(DEFAULT_OPEN_FORGE_RESPONSE);

        VoxelDialogueRegistry.register("sword-sage-repeated", introNode);
    }

}
