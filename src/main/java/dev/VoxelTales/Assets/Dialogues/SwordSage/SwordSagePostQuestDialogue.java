package dev.VoxelTales.Assets.Dialogues.SwordSage;

import au.ellie.hyui.events.UIContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Assets.Dialogues.DialogKey;
import dev.VoxelTales.Assets.Dialogues.Flags.SwordSageFlags;
import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Registries.VoxelDialogueRegistry;
import dev.VoxelTales.UI.Pages.DialoguePage;
import dev.VoxelTales.Utils.VoxelFlagHelper;
import dev.VoxelTales.Utils.VoxelWeaponProgressionHelper;

import java.util.List;
import java.util.Objects;

import static dev.VoxelTales.Assets.Dialogues.DefaultDialogues.defaultCloseResponse;
import static dev.VoxelTales.Assets.Dialogues.DefaultDialogues.defaultOpenForgeResponse;

public class SwordSagePostQuestDialogue {
    private static final String CHECK_QUEST_TEXT = "I've done it, i think?";
    private static final String UNLOCKED_MEMORIES_TEXT_SUCCESS = "Oh i see you did start remembering, then i'll have to congratulate you, here's a couple new blades and handles to aid you in your journey.";
    private static final String UNLOCKED_MEMORIES_TEXT_FAILURE = "I'm sorry, but i need you to actually find yourself first.";

    public static void register() {
        // Root dialogue line shown when the player first opens the conversation.
        DialogueController.DialogueNode introNode = DialogueController.DialogueNode.root(SwordSageRepeatedDialogue.INTRO_TEXT);
        DialogueController.DialogueNode successNode = DialogueController.DialogueNode.node(UNLOCKED_MEMORIES_TEXT_SUCCESS);
        DialogueController.DialogueNode failureNode = DialogueController.DialogueNode.node(UNLOCKED_MEMORIES_TEXT_FAILURE);

        DialogueController.DialogueResponse.BranchedNode successBranch = new DialogueController.DialogueResponse.BranchedNode(successNode, SwordSageFlags.COMPLETED_SWORD_SAGE_QUEST, true);
        DialogueController.DialogueResponse.BranchedNode failureBranch = new DialogueController.DialogueResponse.BranchedNode(failureNode, SwordSageFlags.COMPLETED_SWORD_SAGE_QUEST, false);

        successNode.addResponse(defaultCloseResponse())
                .addResponse(defaultOpenForgeResponse());

        failureNode.addResponse(defaultCloseResponse());

        introNode.addResponse(defaultCloseResponse())
                .addResponse(defaultOpenForgeResponse())
                .addResponse(DialogueController.DialogueResponse.branchNode(CHECK_QUEST_TEXT, List.of(
                    successBranch, failureBranch
                )).withCallback(SwordSagePostQuestDialogue::unlockNewWeapon));

        VoxelDialogueRegistry.register(DialogKey.SWORD_SAGE_POST_QUEST, introNode);
    }

    private static void unlockNewWeapon(UIContext ctx, DialoguePage page) {
        if (Objects.equals(page.getCurrentNode().getText(), UNLOCKED_MEMORIES_TEXT_FAILURE)) { return; }

        PlayerRef playerRef = page.getPlayerRef();

        List.of("Sharp", "Quick").forEach(id -> {
            VoxelWeaponProgressionHelper.unlockBlade(playerRef, id);
            VoxelWeaponProgressionHelper.unlockHandle(playerRef, id);
        });

        VoxelFlagHelper.setFlag(page.getPlayerRef(), SwordSageFlags.SWORD_SAGE_REWARDS_CLAIMED, true);
    }
}
