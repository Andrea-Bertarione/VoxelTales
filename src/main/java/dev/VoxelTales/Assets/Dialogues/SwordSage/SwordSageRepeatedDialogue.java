package dev.VoxelTales.Assets.Dialogues.SwordSage;

import dev.VoxelTales.Assets.Dialogues.DialogKey;
import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Registries.VoxelDialogueRegistry;

import static dev.VoxelTales.Assets.Dialogues.DefaultDialogues.defaultCloseResponse;
import static dev.VoxelTales.Assets.Dialogues.DefaultDialogues.defaultOpenForgeResponse;

public class SwordSageRepeatedDialogue {
    public static final String INTRO_TEXT = "Greetings, dear soul, i'm at your service.";

    public static void register() {
        DialogueController.DialogueNode introNode = DialogueController.DialogueNode.root(SwordSageRepeatedDialogue.INTRO_TEXT);

        introNode.addResponse(defaultCloseResponse());
        introNode.addResponse(defaultOpenForgeResponse());

        VoxelDialogueRegistry.register(DialogKey.SWORD_SAGE_REPEATED, introNode);
    }

}
