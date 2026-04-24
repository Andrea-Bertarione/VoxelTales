package dev.VoxelTales.Assets.Dialogues;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import dev.VoxelTales.Assets.Dialogues.Flags.SwordSageFlags;
import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Registries.VoxelDialogueRegistry;

import java.util.ArrayList;
import java.util.List;

import static dev.VoxelTales.Assets.Dialogues.DefaultDialogues.*;

public class SwordSagePostQuestDialogue {
    private static final String CHECK_QUEST_TEXT = "I've done it, i think?";
    private static final String UNLOCKED_MEMORIES_TEXT_SUCCESS = "Oh i see you did start remembering, then i'll have to congratulate you, here's a couple new blades and handles to aid you in your journey.";
    private static final String UNLOCKED_MEMORIES_TEXT_FAILURE = "I'm sorry, but i need you to actually find yourself first.";

    public static void register() {
        // Root dialogue line shown when the player first opens the conversation.
        DialogueController.DialogueNode introNode = DialogueController.DialogueNode.root(SwordSageRepeatedDialogue.INTRO_TEXT);
        DialogueController.DialogueNode successNode = DialogueController.DialogueNode.node(UNLOCKED_MEMORIES_TEXT_SUCCESS);
        DialogueController.DialogueNode failureNode = DialogueController.DialogueNode.node(UNLOCKED_MEMORIES_TEXT_FAILURE);

        introNode.addResponse(DEFAULT_CLOSE_RESPONSE);
        introNode.addResponse(DEFAULT_OPEN_FORGE_RESPONSE);
        introNode.addResponse(DialogueController.DialogueResponse.branchNode(CHECK_QUEST_TEXT, (List.of(
                new DialogueController.DialogueResponse.BranchedNode(successNode, SwordSageFlags.COMPLETED_SWORD_SAGE_QUEST, true),
                new DialogueController.DialogueResponse.BranchedNode(failureNode, SwordSageFlags.COMPLETED_SWORD_SAGE_QUEST, false)
        ))));

        successNode.addResponse(DEFAULT_CLOSE_RESPONSE);
        successNode.addResponse(DEFAULT_OPEN_FORGE_RESPONSE);

        failureNode.addResponse(DEFAULT_CLOSE_RESPONSE);

        VoxelDialogueRegistry.register("sword-sage-post-quest", introNode);
    }
}
