package dev.VoxelTales.UI.HUD;

import au.ellie.hyui.builders.*;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.UI.HUD.Default.VoxelHudUI;
import dev.VoxelTales.Utils.VoxelWeaponMathHelper;
import dev.VoxelTales.VoxelTalesPlugin;
import dev.VoxelTales.Components.WeaponHandlerComponent;

import java.util.Objects;

public class WeaponHUD extends VoxelHudUI {
    private float progressPercent = 0;
    private int currentXP = 0;
    private int requiredXP = 0;
    private int swordPoints = 0;

    public WeaponHUD(PlayerRef playerRef) {
        super(playerRef);
    }

    public void fetchValues() {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) return;

        WeaponHandlerComponent handler = ref.getStore().getComponent(ref, VoxelTalesPlugin.get().getWeaponHandlerComponent());
        if (handler == null) return;

        this.swordPoints = handler.getSwordPoints();
        this.currentXP = handler.getSwordXP();
        this.requiredXP = VoxelWeaponMathHelper.getRequiredXP(handler.getSwordInternalLevel());
        this.progressPercent = VoxelWeaponMathHelper.getXPProgress(handler.getSwordXP(), handler.getSwordInternalLevel());
    }

    public void update() {
        super.update(() -> {
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

    public void show() {
        super.show(this::update);
    }
}