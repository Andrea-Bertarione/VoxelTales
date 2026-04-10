package dev.VoxelTales.UI.Pages.Default;

import au.ellie.hyui.builders.GroupBuilder;
import au.ellie.hyui.builders.PageOverlayBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import au.ellie.hyui.events.UIContext;
import dev.VoxelTales.UI.Components.ModalUI;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class VoxelEditorPageUI extends VoxelPageUI {
    protected boolean isDirty = false;

    public VoxelEditorPageUI(PlayerRef playerRef) {
        super(playerRef);
    }

    protected void withDiscardConfirmation(UIContext context, Runnable onSafe) {
        if (!this.isDirty) {
            onSafe.run();
            return;
        }

        ModalUI modal = new ModalUI("Unsaved Changes", "Discard Changes", new LinkedHashMap<>());
        modal.setDescription("You have unsaved changes! Are you sure you want to leave?");
        modal.setHeight(250);

        modalConfirmHelper(modal, (_, _) -> {
            this.isDirty = false;
            onSafe.run();
            context.updatePage(true);
        });
    }

    protected void modalConfirmHelper(ModalUI modal, BiConsumer<GroupBuilder, Map<String, Object>> callback) {
        inPageRoot(root -> {
            inMainOverlay(overlay -> {
                overlay.withVisible(false);

                modal.onFinally(() -> overlay.withVisible(true));
            });

            modal.onConfirm(dataMap -> callback.accept(root, dataMap));
            modal.open(this.builder, root);
        });
    }
}
