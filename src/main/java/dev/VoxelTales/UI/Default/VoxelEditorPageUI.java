package dev.VoxelTales.UI.Default;

import au.ellie.hyui.builders.GroupBuilder;
import au.ellie.hyui.builders.PageOverlayBuilder;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import au.ellie.hyui.events.UIContext;
import dev.VoxelTales.UI.Components.ModalUI;
import java.util.LinkedHashMap;

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

        this.builder.getById("page-root", GroupBuilder.class).ifPresent(root -> {
            this.builder.getById("main-overlay", PageOverlayBuilder.class).ifPresent(ov -> ov.withVisible(false));

            modal.onFinally(() -> this.builder.getById("main-overlay", PageOverlayBuilder.class).ifPresent(ov -> ov.withVisible(true)));

            modal.onConfirm(_ -> {
                this.isDirty = false;
                onSafe.run();
                context.updatePage(true);
            });
            modal.open(this.builder, root);
        });
    }
}
