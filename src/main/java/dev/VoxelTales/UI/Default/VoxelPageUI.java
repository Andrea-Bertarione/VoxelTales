package dev.VoxelTales.UI.Default;

import au.ellie.hyui.builders.PageBuilder;
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

public abstract class VoxelPageUI {
    protected final PlayerRef playerRef;
    protected Store<EntityStore> store = null;
    protected PageBuilder builder = null;

    public VoxelPageUI(PlayerRef playerRef) {
        this.playerRef = playerRef;
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref != null && ref.isValid()) {
            this.store = ref.getStore();
        }
    }

    //custom update logic, remember to call the default implementation!
    public abstract void update();

    //Default implementation
    public void update(String pathHTML) {
        this.builder = PageBuilder.pageForPlayer(this.playerRef)
                .loadHtml(pathHTML);
    }

    public void open() {
        this.update();
        if (store != null && builder != null) {
            builder.open(store);
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
}
