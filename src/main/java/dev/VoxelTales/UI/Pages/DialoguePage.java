package dev.VoxelTales.UI.Pages;

import au.ellie.hyui.builders.*;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Controllers.DialogueController;
import dev.VoxelTales.UI.Pages.Default.VoxelPageUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DialoguePage extends VoxelPageUI {

    private static final String HTML_PATH = "Pages/Dialogue.html";

    private static final String TEXT_CONTENT_ID = "dialogue-text-content";
    private static final String OPTIONS_CONTAINER_ID = "dialogue-options";

    private DialogueController.DialogueNode currentNode;

    private final Map<String, UIElementBuilder<?>> responseButtons = new HashMap<>();

    public DialoguePage(PlayerRef playerRef) {
        super(playerRef);
    }

    public void update() {
        super.update(HTML_PATH);

        this.setTextContent();
        this.buildOptions();
    }

    public void openWith(DialogueController.DialogueNode node) {
        //LoggerUtil.getLogger().info("Opening dialogue with node: " + node.getText());

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
        this.redrawCurrentNode();
    }

    public void setText(String text) {
        if (this.currentNode == null) {
            this.currentNode = DialogueController.DialogueNode.root(text);
        } else {
            this.currentNode.withText(text);
        }

        this.redrawCurrentNode();
    }

    public void setResponses(List<DialogueController.DialogueResponse> responses) {
        if (this.currentNode == null) {
            this.currentNode = DialogueController.DialogueNode.root(null);
        }

        this.currentNode.getResponses().clear();

        if (responses != null) {
            this.currentNode.getResponses().addAll(responses);
        }

        this.redrawCurrentNode();
    }

    private void redrawCurrentNode() {
        if (this.currentNode == null) {
            return;
        }

        this.setTextContent();
        this.buildOptions();

        this.currentPage.updatePage(true);
    }

    private void setTextContent() {
        this.withDialogueGroup(TEXT_CONTENT_ID, LabelBuilder.class,
                label -> label.withText(this.currentNode.getText())
        );
    }

    private void buildOptions() {
        this.withDialogueGroup(OPTIONS_CONTAINER_ID, GroupBuilder.class, container -> {
            List<DialogueController.DialogueResponse> responses =
                    List.copyOf(this.currentNode.getResponses());

            this.syncOptions(container, responses);
        });
    }

    private void syncOptions(
            GroupBuilder container,
            List<DialogueController.DialogueResponse> responses
    ) {
        Map<String, DialogueController.DialogueResponse> desired = new HashMap<>();

        for (DialogueController.DialogueResponse response : responses) {
            String id = buildOptionId(response);
            desired.put(id, response);
        }

        this.responseButtons.entrySet().stream()
                .filter(entry -> !desired.containsKey(entry.getKey()))
                .map(Map.Entry::getValue)
                .forEach(this.builder::removeElement);

        for (Map.Entry<String, DialogueController.DialogueResponse> entry : desired.entrySet()) {
            String id = entry.getKey();
            DialogueController.DialogueResponse response = entry.getValue();

            UIElementBuilder<?> existing = this.responseButtons.get(id);

            if (existing != null) {
                container.addChild(existing);
                continue;
            }

            ButtonBuilder button = buildResponseButton(id, response);

            this.responseButtons.put(id, button);
            container.addChild(button);
        }

        this.responseButtons.keySet().retainAll(desired.keySet());
    }

    private ButtonBuilder buildResponseButton(
            String id,
            DialogueController.DialogueResponse response
    ) {
        //LoggerUtil.getLogger().info("Creating dialogue option: " + response.getText());

        return ButtonBuilder.secondaryTextButton()
                .withId(id)
                .withText(response.getText())
                .withAnchor(new HyUIAnchor().setTop(5).setHeight(40))
                .onClick((__, ctx) -> response.processPress(this, ctx));
    }

    private String buildOptionId(DialogueController.DialogueResponse response) {
        return ("dialogue-option-" + response.getId())
                .toLowerCase()
                .replaceAll("[^a-z0-9-]", "");
    }

    private <E extends UIElementBuilder<E>> void withDialogueGroup(
            String elementId,
            Class<E> type,
            Consumer<E> consumer
    ) {
        if (this.currentPage != null) {
            this.currentPage.getById(elementId, type).ifPresent(consumer);
            return;
        }

        if (this.builder != null) {
            this.builder.getById(elementId, type).ifPresent(consumer);
        }
    }
}