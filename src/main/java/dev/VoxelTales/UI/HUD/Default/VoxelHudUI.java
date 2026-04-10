package dev.VoxelTales.UI.HUD.Default;

import au.ellie.hyui.builders.HyUIHud;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;

public abstract class VoxelHudUI {
    protected PlayerRef playerRef;
    protected HyUIHud hudResult;
    protected boolean isActive = false;

    public VoxelHudUI(PlayerRef playerRef) {
        this.playerRef = playerRef;
    }

    /**
     * Function that triggers the values fetch for the HUD
     */
    protected abstract void fetchValues();

    /**
     * The "Redraw" function. Wipes the old HUD and builds a fresh one.
     */
    protected void update(Runnable task) {
        if (!isActive) return;
        if (playerRef.getReference() == null || !playerRef.getReference().isValid()) return;

        World world = playerRef.getReference().getStore().getExternalData().getWorld();

        if (this.hudResult != null) {
            this.hudResult.remove();
        }

        this.fetchValues();

        world.execute(task);
    }

    /**
     * Turns the HUD logic on and triggers an initial draw
     */
    public void show(Runnable onDraw) {
        if (this.isActive) return;
        this.isActive = true;

        onDraw.run();
    }

    /**
     * Turns the HUD logic off and removes it from the screen
     */
    public void hide() {
        this.isActive = false;

        if (this.hudResult == null) return;
        if (playerRef.getReference() == null || !playerRef.getReference().isValid()) return;

        World world = playerRef.getReference().getStore().getExternalData().getWorld();
        world.execute(() -> {
            this.hudResult.remove();
            this.hudResult = null;
        });
    }
}
