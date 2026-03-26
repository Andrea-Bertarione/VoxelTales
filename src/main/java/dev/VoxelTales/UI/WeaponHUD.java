package dev.VoxelTales.UI;

import au.ellie.hyui.builders.*;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Utils.WeaponMath;
import dev.VoxelTales.VoxelTalesPlugin;
import dev.VoxelTales.Components.WeaponHandlerComponent;

import java.util.Objects;

public class WeaponHUD {
    private final PlayerRef playerRef;
    private HyUIHud hudResult;

    private float progressPercent = 0;
    private int currentXP = 0;
    private int requiredXP = 0;
    private int swordPoints = 0;

    private boolean isActive = false; // Internal state: Should this be visible?

    public WeaponHUD(PlayerRef playerRef) {
        this.playerRef = playerRef;
    }

    /**
     * Logic to determine what data goes into the HTML
     */
    private void prepareValues() {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) return;

        WeaponHandlerComponent handler = ref.getStore().getComponent(ref, VoxelTalesPlugin.get().getWeaponHandlerComponent());
        if (handler == null) return;

        this.swordPoints = handler.getSwordPoints();
        this.currentXP = handler.getSwordXP();
        this.requiredXP = WeaponMath.getRequiredXP(handler.getSwordInternalLevel());
        this.progressPercent = WeaponMath.getXPProgress(handler.getSwordXP(), handler.getSwordInternalLevel());
    }

    /**
     * The "Redraw" function. Wipes the old HUD and builds a fresh one.
     */
    public void update() {
        if (!isActive) return;

        World world = Objects.requireNonNull(playerRef.getReference()).getStore().getExternalData().getWorld();

        world.execute(() -> {
            // 1. Clean up the old instance if it exists
            if (this.hudResult != null) {
                this.hudResult.remove();
            }

            this.prepareValues();

            HudBuilder builder = HudBuilder.hudForPlayer(playerRef)
                    .enableRuntimeTemplateUpdates(true)
                    .loadHtml("HUD/WeaponHUD.html");

            builder.getById("vtales-bar-frame", GroupBuilder.class)
                    .ifPresentOrElse(elem ->
                    elem.addChild(new ProgressBarBuilder()
                            //.withCircular(true)
                            .withId("vtales-bar-fill")
                            .withBarTexturePath("Assets/ProgressBarInner.png")
                            .withEffectTexturePath("Assets/ProgressBarEffect.png")
                            .withEffectWidth(123)
                            .withEffectHeight(82)
                            .withEffectOffset(81)
                                            .withBackground(new HyUIPatchStyle().setColor("#00000000"))
                            //.withAlignment(ProgressBarAlignment.Horizontal)
                            //.withDirection(ProgressBarDirection.Start)
                            .withAnchor(new HyUIAnchor()
                                    .setWidth(664)
                                    .setHeight(33)
                            )
                            .withValue(this.progressPercent)
                    ), () -> {
                       LoggerUtil.getLogger().severe("vtales-bar-frame not found");
                    });

            builder.getById("vtales-hud-text-root-shadow", GroupBuilder.class).ifPresentOrElse(elem -> {
                elem.addChild(new LabelBuilder()
                        .withId("vtales-xp-label-shadow")
                        .withText(this.currentXP + "/" + this.requiredXP)
                        .withStyle(new HyUIStyle()
                                .setHorizontalAlignment(Alignment.Center)
                                .setFontSize(16)
                                .setRenderBold(true)
                                .setTextColor("#000000")
                        )
                        .withAnchor(new HyUIAnchor()
                                .setBottom(21)
                                .setRight(2)
                        )
                );
            }, () -> {
                LoggerUtil.getLogger().severe("vtales-hud-text-root-shadow not found");
            });

            builder.getById("vtales-hud-text-root", GroupBuilder.class).ifPresentOrElse(elem -> {
                elem.addChild(new LabelBuilder()
                        .withId("vtales-xp-label")
                        .withText(this.currentXP + "/" + this.requiredXP)
                                .withStyle(new HyUIStyle()
                                        .setHorizontalAlignment(Alignment.Center)
                                        .setFontSize(16)
                                        .setRenderBold(true)
                                        .setTextColor("#fcffef")
                                )
                        .withAnchor(new HyUIAnchor()
                                .setBottom(22)
                                .setLeft(0)
                                .setRight(0)
                        )
                );
            }, () -> {
                LoggerUtil.getLogger().severe("vtales-hud-text-root not found");
            });

            builder.getById("vtales-hud-sp-root", ContainerBuilder.class).ifPresentOrElse(elem -> {
                elem.addContentChild(new LabelBuilder()
                        .withId("vtales-sp-label")
                        .withText(String.valueOf(this.swordPoints))
                        .withFlexWeight(1)
                        .withStyle(new HyUIStyle()
                                .setAlignment(Alignment.Center)
                                .setFontSize(22)
                                .setRenderBold(true)
                                .setTextColor("#fcffef")
                        )
                );
            }, () -> {
                LoggerUtil.getLogger().severe("vtales-hud-sp-root not found");
            });

            this.hudResult = builder.show();
        });
    }

    /**
     * Turns the HUD logic on and triggers an initial draw
     */
    public void show() {
        if (this.isActive) return;
        this.isActive = true;
        this.update();
    }

    /**
     * Turns the HUD logic off and removes it from the screen
     */
    public void hide() {
        this.isActive = false;
        if (this.hudResult != null) {
            World world = Objects.requireNonNull(playerRef.getReference()).getStore().getExternalData().getWorld();
            world.execute(() -> {
                this.hudResult.remove();
                this.hudResult = null;
            });
        }
    }
}