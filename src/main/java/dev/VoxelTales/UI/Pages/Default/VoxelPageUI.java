package dev.VoxelTales.UI.Pages.Default;

import au.ellie.hyui.builders.*;
import au.ellie.hyui.events.UIContext;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class VoxelPageUI {
    protected final PlayerRef playerRef;

    @Nullable
    protected Store<EntityStore> store = null;
    protected PageBuilder builder = null;
    protected HyUIPage currentPage = null;

    private long openDebounceMillis = 150L;
    private long lastDismissAtMillis = 0L;

    public VoxelPageUI(PlayerRef playerRef) {
        this.playerRef = playerRef;
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref != null && ref.isValid()) {
            this.store = ref.getStore();
        }
    }

    public void setOpenDebounceMillis(long openDebounceMillis) {
        this.openDebounceMillis = Math.max(0L, openDebounceMillis);
    }

    public long getOpenDebounceMillis() {
        return this.openDebounceMillis;
    }

    //custom update logic, remember to call the default implementation!
    public abstract void update();

    //Default implementation
    public void update(String pathHTML) {
        if (pathHTML == null) return;

        this.builder = PageBuilder.pageForPlayer(this.playerRef)
                .loadHtml(pathHTML);
    }

    public void open() {
        long now = System.currentTimeMillis();
        long elapsedSinceDismiss = now - this.lastDismissAtMillis;

        if (elapsedSinceDismiss < this.openDebounceMillis) {
            return;
        }

        this.update();

        if (store != null && builder != null) {
            this.builder.onDismiss((page, _) -> {
                this.currentPage = null;
                this.lastDismissAtMillis = System.currentTimeMillis();
            });

            this.currentPage = builder.open(store);
        }
    }

    public void close() {
        if (this.currentPage != null) {
            this.currentPage.close();
            this.currentPage = null;
            this.lastDismissAtMillis = System.currentTimeMillis();
        }
    }

    protected void notifySuccess(String title, String message, String iconItem, String soundName) {
        var primary = Message.raw(title).color("#00FF00");
        var secondary = Message.raw(message).color("#FFFFFF");
        var icon = new ItemStack(iconItem, 1).toPacket();
        NotificationUtil.sendNotification(this.playerRef.getPacketHandler(), primary, secondary, icon);

        // Play the "ding" sound automatically
        if (soundName != null) {
            playGlobalSound(soundName);
        }
    }

    protected void playGlobalSound(String soundName) {
        if (this.store == null) return;
        int index = SoundEvent.getAssetMap().getIndex(soundName);
        this.store.getExternalData().getWorld().execute(() -> {
            Ref<EntityStore> ref = this.playerRef.getReference();
            if (ref != null && ref.isValid()) {
                SoundUtil.playSoundEvent3dToPlayer(ref, index, SoundCategory.SFX, Vector3d.ZERO, this.store);
            }
        });
    }

    protected void inPageRoot(Consumer<GroupBuilder> callback) {
        if (this.builder == null) return;

        this.builder.getById("page-root", GroupBuilder.class).ifPresent(callback);
    }

    protected void inMainOverlay(Consumer<PageOverlayBuilder> callback) {
        if (this.builder == null) return;

        this.builder.getById("main-overlay", PageOverlayBuilder.class).ifPresent(callback);
    }

    protected void bindButtonClick(String elementId, BiConsumer<ButtonBuilder, UIContext> callback) {
        if (this.builder == null) return;

        this.builder.getById(elementId, ButtonBuilder.class).ifPresent(btn ->
                btn.onClick((_, context) -> callback.accept(btn, context))
        );
    }

    public PlayerRef getPlayerRef() {
        return this.playerRef;
    }
}
