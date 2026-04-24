package dev.VoxelTales.Assets.Dialogues;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import dev.VoxelTales.Assets.Dialogues.Flags.SwordSageFlags;
import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Registries.VoxelDialogueRegistry;

import static dev.VoxelTales.Assets.Dialogues.DefaultDialogues.*;

public class SwordSagePreQuestDialogue {
    private static final String NEXT_QUEST_TEXT = "What should i do next?";
    private static final String UNLOCK_MEMORIES_TEXT = "Well, i do have something that might help you, go to the temple of Gaia, i marked it on your map, come back to me after you've started remembering...";
    private static final String ACCEPTED_QUEST_TEXT = "Good, go for it!";

    public static void register() {
        // Root dialogue line shown when the player first opens the conversation.
        DialogueController.DialogueNode introNode = DialogueController.DialogueNode.root(SwordSageRepeatedDialogue.INTRO_TEXT);
        DialogueController.DialogueNode unlockMemoriesNode = DialogueController.DialogueNode.node(UNLOCK_MEMORIES_TEXT);
        DialogueController.DialogueNode acceptedQuestNode = DialogueController.DialogueNode.node(ACCEPTED_QUEST_TEXT);

        introNode.addResponse(DEFAULT_CLOSE_RESPONSE);
        introNode.addResponse(DEFAULT_OPEN_FORGE_RESPONSE);
        introNode.addResponse(DialogueController.DialogueResponse.node(NEXT_QUEST_TEXT, unlockMemoriesNode));

        unlockMemoriesNode.addResponse(
                DialogueController.DialogueResponse.flagNode(ACCEPT_QUEST_TEXT, acceptedQuestNode, SwordSageFlags.ACCEPTED_SWORD_SAGE_QUEST)
        );

        acceptedQuestNode.addResponse(DEFAULT_CLOSE_RESPONSE);

        VoxelDialogueRegistry.register("sword-sage-pre-quest", introNode);
    }
}
