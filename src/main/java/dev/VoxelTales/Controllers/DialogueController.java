package dev.VoxelTales.Controllers;

import au.ellie.hyui.events.UIContext;
import dev.VoxelTales.UI.Pages.DialoguePage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class DialogueController {
    public enum DialogueType {
        CALLBACK,
        NODE,
        CLOSE
    }

    public static class DialogueNode {
        private final boolean isRoot;
        private String text;
        private final List<DialogueResponse> responses;

        private DialogueNode(boolean isRoot, String text, List<DialogueResponse> responses) {
            this.isRoot = isRoot;
            this.text = text;
            this.responses = responses == null ? new ArrayList<>() : responses;
        }

        public static DialogueNode root(String text) {
            return new DialogueNode(true, text, new ArrayList<>());
        }

        public static DialogueNode node(String text) {
            return new DialogueNode(false, text, new ArrayList<>());
        }

        public DialogueNode withText(String text) {
            this.text = text;
            return this;
        }

        public DialogueNode addResponse(DialogueResponse response) {
            this.responses.add(response);
            return this;
        }

        public boolean isRoot() {
            return isRoot;
        }

        public String getText() {
            return text;
        }

        public List<DialogueResponse> getResponses() {
            return responses;
        }
    }

    public static class DialogueResponse {
        private final String text;
        private final String id;
        private final DialogueType type;
        private final DialogueNode node;
        private final BiConsumer<UIContext, DialoguePage> callback;

        private DialogueResponse(String text, DialogueType type, DialogueNode node, BiConsumer<UIContext, DialoguePage> callback) {
            this.text = text;
            this.id = UUID.randomUUID().toString();
            this.type = type;
            this.node = node;
            this.callback = callback;
        }

        public static DialogueResponse callback(String text, BiConsumer<UIContext, DialoguePage> callback) {
            return new DialogueResponse(text, DialogueType.CALLBACK, null, callback);
        }

        public static DialogueResponse node(String text, DialogueNode node) {
            return new DialogueResponse(text, DialogueType.NODE, node, null);
        }

        public static DialogueResponse close(String text) {
            return new DialogueResponse(text, DialogueType.CLOSE, null, null);
        }

        public String getText() {
            return text;
        }

        public String getId() {
            return id;
        }

        public DialogueType getType() {
            return type;
        }

        public DialogueNode getNode() {
            return node;
        }

        public BiConsumer<UIContext, DialoguePage> getCallback() {
            return callback;
        }

        public void processPress(DialoguePage page, UIContext ctx) {
            if (page == null) { return; }

            switch (this.type) {
                case CALLBACK:
                    if (this.callback == null) { return; }
                    if (ctx == null) { return; }

                    this.callback.accept(ctx, page);
                    break;
                case NODE:
                    if (this.node == null) { return; }
                    if (ctx == null) { return; }

                    page.setNode(this.node);
                    ctx.updatePage(true);
                    break;
                case CLOSE:
                    page.close();
                    break;
            }
        }
    }
}
