package dev.VoxelTales.Controllers;

import au.ellie.hyui.events.UIContext;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.DialogueStateComponent;
import dev.VoxelTales.UI.Pages.DialoguePage;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class DialogueController {
    public enum DialogueType {
        NODE,
        CLOSE,
        FLAG_NODE,
        BRANCHED,
        CUSTOM
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
        public record BranchedNode(DialogueNode node, String flag, boolean expectedValue) {}
        private final String text;
        private final String id;
        private final DialogueType type;
        private final DialogueNode node;
        private List<BranchedNode> branches = new ArrayList<>();
        private BiConsumer<UIContext, DialoguePage> callback = null;

        private String flagBuffer;

        private DialogueResponse(String text, DialogueType type, DialogueNode node, BiConsumer<UIContext, DialoguePage> callback) {
            this.text = text;
            this.id = UUID.randomUUID().toString();
            this.type = type;
            this.node = node;
            this.callback = callback;
        }

        private DialogueResponse(String text, DialogueType type, DialogueNode node, List<BranchedNode> branches) {
            this.text = text;
            this.id = UUID.randomUUID().toString();
            this.type = type;
            this.node = node;
            this.branches = branches;
        }

        private DialogueResponse(String text, DialogueType type, DialogueNode node, String flag) {
            this.text = text;
            this.id = UUID.randomUUID().toString();
            this.type = type;
            this.node = node;
            this.flagBuffer = flag;
        }

        public static DialogueResponse node(String text, DialogueNode node) {
            return new DialogueResponse(text, DialogueType.NODE, node, (BiConsumer<UIContext, DialoguePage>) null);
        }

        public static DialogueResponse close(String text) {
            return new DialogueResponse(text, DialogueType.CLOSE, (DialogueNode) null, (BiConsumer<UIContext, DialoguePage>) null);
        }

        public static DialogueResponse flagNode(String text, DialogueNode node, String flag) {
            return new DialogueResponse(text, DialogueType.FLAG_NODE, node, flag);
        }

        public static DialogueResponse branchNode(String text, List<BranchedNode> branches) {
            return new DialogueResponse(text, DialogueType.BRANCHED, null, branches);
        }

        public static DialogueResponse custom(String text) {
            return new DialogueResponse(text, DialogueType.CUSTOM, (DialogueNode) null, (BiConsumer<UIContext, DialoguePage>) null);
        }

        public DialogueResponse withCallback(BiConsumer<UIContext, DialoguePage> callback) {
            this.callback = callback;
            return this;
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
                case NODE:
                    if (this.node == null) { return; }
                    if (ctx == null) { return; }

                    page.setNode(this.node);
                    ctx.updatePage(true);

                    //LoggerUtil.getLogger().info("Dialogue node set to " + this.node.getText());
                    break;
                case CLOSE:
                    page.close();
                    //LoggerUtil.getLogger().info("Dialogue page closed");
                    break;

                case FLAG_NODE:
                    if (this.node == null) { return; }
                    if (ctx == null) { return; }

                    page.setNode(this.node);
                    ctx.updatePage(true);

                    PlayerRef playerRef = page.getPlayerRef();
                    Ref<EntityStore> ref = playerRef.getReference();
                    assert ref != null;

                    Store<EntityStore> store = ref.getStore();

                    store.getExternalData().getWorld().execute(() -> {
                        DialogueStateComponent state = store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getDialogueStateComponent());
                        state.setFlag(this.flagBuffer, true);
                    });

                    break;
                case BRANCHED:
                    if (this.branches == null) { return; }
                    if (ctx == null) { return; }

                    DialogueNode resolved = resolveBranch(page);
                    if (resolved == null) { return; }

                    page.setNode(resolved);
                    ctx.updatePage(true);
                    break;

                case CUSTOM:
                    break;
            }

            if (this.callback != null) {
                this.callback.accept(ctx, page);
            }
        }

        private DialogueNode resolveBranch(DialoguePage page) {
            PlayerRef playerRef = page.getPlayerRef();
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null) { return null; }

            Store<EntityStore> store = ref.getStore();
            DialogueStateComponent state = store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getDialogueStateComponent());

            for (BranchedNode branch : branches) {
                boolean actual = state.hasFlag(branch.flag());
                if (actual == branch.expectedValue()) {
                    return branch.node();
                }
            }

            return null;
        }
    }
}
