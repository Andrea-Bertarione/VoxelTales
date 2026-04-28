package dev.VoxelTales.Assets.Dialogues.SwordSage;

import dev.VoxelTales.Assets.Dialogues.DialogKey;
import dev.VoxelTales.Assets.Dialogues.Flags.SwordSageFlags;
import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.Registries.VoxelDialogueRegistry;

import static dev.VoxelTales.Assets.Dialogues.DefaultDialogues.*;

public class SwordSageIntroDialogue {
    private static final String INTRO_TEXT =
            "Welcome to the world of Orbis young soul, im sorry i had to evoke you in such a place...";
    private static final String VOID_TEXT =
            "I would've preferred to use the temple but the void already has taken over it.";
    private static final String INTRODUCE_SELF_TEXT =
            "That being said, i still have to introduce myself. I am the Sword Sage.";
    private static final String WHO_ARE_YOU_TEXT =
            "Yeah that's a difficult answer to give, you're a lost soul";
    private static final String CREATED_YOU_TEXT =
            "I used a lot of my strength to generate a new body for a sword wielder's soul, you!";
    private static final String SERVICES_TEXT =
            "Yeah, everything will be revealed at the right time, but for now i can only provide my services and aid you in your quest.";
    private static final String FINAL_TEXT =
            "I'm sorry, i know it's a lot to take in but you'll understand soon, for now forge your first sword and find your way, im at your service if you have more questions.";


    private static final String WHOAMI_RESPONSE_TEXT = "Who am I?";
    private static final String UH_RESPONSE_TEXT = "Huh??";
    private static final String WHAT_RESPONSE_TEXT = "A sword what? a sword wielder's soul?";
    private static final String QUEST_RESPONSE_TEXT = "A quest too??";

    public static void register() {
        // Root dialogue line shown when the player first opens the conversation.
        DialogueController.DialogueNode introNode = DialogueController.DialogueNode.root(INTRO_TEXT);

        // Follow-up lines in order.
        DialogueController.DialogueNode voidNode =
                DialogueController.DialogueNode.node(VOID_TEXT);

        DialogueController.DialogueNode introduceSelfNode =
                DialogueController.DialogueNode.node(INTRODUCE_SELF_TEXT);

        DialogueController.DialogueNode whoAreYouNode =
                DialogueController.DialogueNode.node(WHO_ARE_YOU_TEXT);

        DialogueController.DialogueNode createdYouNode =
                DialogueController.DialogueNode.node(CREATED_YOU_TEXT);

        DialogueController.DialogueNode servicesNode =
                DialogueController.DialogueNode.node(SERVICES_TEXT);

        DialogueController.DialogueNode finalNode =
                DialogueController.DialogueNode.node(FINAL_TEXT);

        // Player choice that advances the dialogue to the follow-up node.
        introNode.addResponse(DialogueController.DialogueResponse.node(CONTINUE_TEXT, voidNode));
        voidNode.addResponse(DialogueController.DialogueResponse.node(CONTINUE_TEXT, introduceSelfNode));
        introduceSelfNode.addResponse(DialogueController.DialogueResponse.node(WHOAMI_RESPONSE_TEXT, whoAreYouNode));
        whoAreYouNode.addResponse(DialogueController.DialogueResponse.node(UH_RESPONSE_TEXT, createdYouNode));
        createdYouNode.addResponse(DialogueController.DialogueResponse.node(WHAT_RESPONSE_TEXT, servicesNode));
        servicesNode.addResponse(DialogueController.DialogueResponse.flagNode(QUEST_RESPONSE_TEXT, finalNode, SwordSageFlags.EXHAUSTED_SWORD_SAGE));
        finalNode.addResponse(defaultCloseResponse());
        finalNode.addResponse(defaultOpenForgeResponse());

        VoxelDialogueRegistry.register(DialogKey.SWORD_SAGE_INTRO, introNode);
    }
}
