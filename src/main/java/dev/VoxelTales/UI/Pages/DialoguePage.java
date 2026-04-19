package dev.VoxelTales.UI.Pages;

import au.ellie.hyui.builders.ButtonBuilder;
import au.ellie.hyui.builders.GroupBuilder;
import au.ellie.hyui.builders.HyUIAnchor;
import au.ellie.hyui.builders.LabelBuilder;
import au.ellie.hyui.events.UIContext;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.UI.Pages.Default.VoxelPageUI;

import java.util.List;

public class DialoguePage extends VoxelPageUI {
    private static final String HTML_PATH = "Pages/Dialogue.html";

    private static final String TEXT_CONTENT_ID = "dialogue-text-content";
    private static final String OPTIONS_CONTAINER_ID = "dialogue-options";

    private DialogueController.DialogueNode currentNode;
    private List<DialogueController.DialogueResponse> renderedResponses;

    public DialoguePage(PlayerRef playerRef) {
        super(playerRef);
    }

    public void update() {
        super.update(HTML_PATH);

        if (this.currentNode == null) {
            return;
        }

        this.setTextContent();
        this.buildOptions();
    }

    public void openWith(DialogueController.DialogueNode node) {
        this.currentNode = node;
        this.open();
    }

    public void openWith(String initialText, DialogueController.DialogueResponse[] responses) {
        DialogueController.DialogueNode node = DialogueController.DialogueNode.root(initialText);
        if (responses != null) {
            for (DialogueController.DialogueResponse response : responses) {
                node.addResponse(response);
            }
        }

        this.openWith(node);
    }

    public void setNode(DialogueController.DialogueNode node) {
        this.currentNode = node;
        this.update();
    }

    public void setText(String text) {
        if (this.currentNode == null) {
            this.currentNode = DialogueController.DialogueNode.root(text);
        } else {
            this.currentNode.withText(text);
        }

        this.update();
    }

    public void setResponses(List<DialogueController.DialogueResponse> responses) {
        if (this.currentNode == null) {
            this.currentNode = DialogueController.DialogueNode.root(null);
        }

        this.currentNode.getResponses().clear();
        if (responses != null) {
            this.currentNode.getResponses().addAll(responses);
        }

        this.update();
    }

    @Override
    public void open() {
        super.open();
    }

    private void setTextContent() {
        this.builder.getById(TEXT_CONTENT_ID, LabelBuilder.class).ifPresent(labelBuilder -> {
            labelBuilder.withText(this.currentNode.getText());
        });
    }

    private void buildOptions() {
        this.builder.getById(OPTIONS_CONTAINER_ID, GroupBuilder.class).ifPresent(container -> {
            this.clearOptions();

            List<DialogueController.DialogueResponse> responses = this.currentNode.getResponses();
            for (DialogueController.DialogueResponse response : responses) {
                LoggerUtil.getLogger().info("Adding dialogue option: " + response.getText());

                container.addChild(ButtonBuilder.secondaryTextButton()
                        .withId("dialogue-option-" + response.getId())
                        .withText(response.getText())
                        .withAnchor(new HyUIAnchor().setTop(5).setHeight(40))
                        .onClick((__, ctx) -> {
                            response.processPress(this, ctx);
                        })
                );
            }

            this.renderedResponses = List.copyOf(responses);
        });
    }

    private void clearOptions() {
        if (this.renderedResponses == null) {
            return;
        }

        for (DialogueController.DialogueResponse previousResponse : this.renderedResponses) {
            this.builder.removeElement("dialogue-option-" + previousResponse.getId());
        }

        this.renderedResponses = null;
    }
}
