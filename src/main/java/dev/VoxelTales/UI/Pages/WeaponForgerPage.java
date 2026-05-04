package dev.VoxelTales.UI.Pages;

import au.ellie.hyui.builders.*;
import au.ellie.hyui.elements.LayoutModeSupported;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Components.PlayerComponents.PlayerWeaponProgressComponent;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.UI.Pages.Default.VoxelPageUI;
import dev.VoxelTales.Utils.VoxelInventoryHelper;
import dev.VoxelTales.Utils.VoxelWeaponConfigsHelper;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WeaponForgerPage extends VoxelPageUI {
    private static final String HTML_PATH = "Pages/WeaponForger.html";

    private static final String CONFIRM_FORGE_BUTTON_ID = "confirm-forge-button";

    private static final String BLADE_LIST_SELECTOR_ID = "blades-item-list";
    private static final String HANDLE_LIST_SELECTOR_ID = "handles-item-list";

    private static final String BLADE_STATS_BUTTON = "blades-show-stats";
    private static final String HANDLE_STATS_BUTTON = "handles-show-stats";

    private static final String BLADE_BASE_LIST_ID = "blade-base-damage-list";
    private static final String BLADE_SCALING_LIST_ID = "blade-scaling-list";
    private static final String BLADE_PASSIVE_LIST_ID = "blade-passives-list";

    private static final String HANDLE_BASE_LIST_ID = "handle-base-list";
    private static final String HANDLE_SCALING_LIST_ID = "handle-scaling-list";
    private static final String HANDLE_PASSIVE_LIST_ID = "handle-passives-list";

    private static final String TOTAL_BASE_LIST_ID = "total-base-damage-list";
    private static final String TOTAL_SCALING_LIST_ID = "total-scaling-list";
    private static final String TOTAL_PASSIVE_LIST_ID = "total-passives-list";

    private static final String FORGED_STATS_ID = "forged-stats";
    private static final String BLADE_SLOT_ID = "preview-slot-blade";
    private static final String HANDLE_SLOT_ID = "preview-slot-handle";
    private static final String BLADE_SLOT_LABEL_ID = "blade-slot-placeholder";
    private static final String HANDLE_SLOT_LABEL_ID = "handle-slot-placeholder";
    private static final String BLADE_STATS_PANEL_ID = "blades-stats-panel";
    private static final String HANDLE_STATS_PANEL_ID = "handles-stats-panel";

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    private String selectedBlade = null;
    private String selectedHandle = null;
    private boolean isBladeStatsShown = false;
    private boolean isHandleStatsShown = false;

    private final Map<String, UIElementBuilder<?>> bladesList = new HashMap<>();
    private final Map<String, UIElementBuilder<?>> handlesList = new HashMap<>();

    private final Map<String, UIElementBuilder<?>> bladeStatElements = new HashMap<>();
    private final Map<String, UIElementBuilder<?>> handleStatElements = new HashMap<>();
    private final Map<String, UIElementBuilder<?>> forgedStatElements = new HashMap<>();

    private Set<String> availableBlades = java.util.Collections.emptySet();
    private Set<String> availableHandles = java.util.Collections.emptySet();

    public WeaponForgerPage(PlayerRef playerRef) {
        super(playerRef);

        this.loadWeaponLists();
    }

    public void update() {
        super.update(HTML_PATH);

        //LoggerUtil.getLogger().info("WeaponForgerPage.update()");

        this.loadWeaponLists();

        this.bindShowStatsButtons();
        this.refreshStatsPanelsVisibility();

        this.drawBladeList();
        this.drawHandleList();

        if (this.isBladeStatsShown) this.drawBladeStats();
        if (this.isHandleStatsShown) this.drawHandleStats();
        this.drawForgedStats();
        this.drawPreviewEquation();

        this.drawForgeButton();
        this.bindConfirmForgeButton();
    }

    public void open() {
        super.open();
    }

    private void loadWeaponLists() {
        PlayerWeaponProgressComponent.PlayerWeaponProgressData weaponProgressCache = VoxelCacheRegistry.staticGetSynced(CacheEnum.VOXEL_PLAYER_WEAPON_PROGRESS_CACHE, this.playerRef.getUuid(), PlayerWeaponProgressComponent.PlayerWeaponProgressData.class);
        if (weaponProgressCache == null) return;

        this.availableBlades = weaponProgressCache.unlockedBlades();
        this.availableHandles = weaponProgressCache.unlockedHandles();
    }


    private void bindConfirmForgeButton() {
        this.builder.getById(CONFIRM_FORGE_BUTTON_ID, ButtonBuilder.class).ifPresent(button ->
                button.onClick((_, context) -> {
                    VoxelInventoryHelper.equipNewWeapon(this.playerRef, this.selectedBlade, this.selectedHandle);

                    super.notifySuccess("Success!", "Successfully forged " + this.selectedBlade + " - " + this.selectedHandle, "Weapon_Heirloom_" + this.selectedBlade + "_" + this.selectedHandle, "SFX_Level_Up_Generic");

                    context.getPage().ifPresent(page -> {
                        page.close();
                        context.updatePage(true);
                    });
                })
        );
    }

    private void drawForgeButton() {
        this.builder.getById(CONFIRM_FORGE_BUTTON_ID, ButtonBuilder.class).ifPresent(button ->
                button.withDisabled( this.selectedBlade == null || this.selectedHandle == null)
        );
    }

    private void drawPreviewEquation() {
        this.updatePreviewSlot(BLADE_SLOT_ID, BLADE_SLOT_LABEL_ID, this.selectedBlade, "Blade");
        this.updatePreviewSlot(HANDLE_SLOT_ID, HANDLE_SLOT_LABEL_ID, this.selectedHandle, "Handle");
    }

    private void updatePreviewSlot(String slotId, String labelId, String value, String fallback) {
        this.builder.getById(slotId, GroupBuilder.class).flatMap(_ -> this.builder.getById(labelId, LabelBuilder.class)).ifPresent(label ->
                label.withText(value != null ? value : fallback).withStyle(new HyUIStyle().setFontSize(value != null ? 20 : 14).setTextColor(value != null ? "#ffffff" : "#444444")));
    }


    private void drawBladeList() {
        this.builder.getById(BLADE_LIST_SELECTOR_ID, GroupBuilder.class).ifPresent(group ->
                this.syncSidebar(group, this.bladesList, this.availableBlades, this.selectedBlade, this::buildBladeButton)
        );
    }

    private void drawHandleList() {
        this.builder.getById(HANDLE_LIST_SELECTOR_ID, GroupBuilder.class).ifPresent(group ->
                this.syncSidebar(group, this.handlesList, this.availableHandles, this.selectedHandle, this::buildHandleButton)
        );
    }

    private void drawBladeStats() {
        this.bladeStatElements.values().forEach(this.builder::removeElement);
        this.bladeStatElements.clear();

        if (this.selectedBlade == null) return;

        this.syncStatPanel(
                BLADE_BASE_LIST_ID,
                BLADE_SCALING_LIST_ID,
                BLADE_PASSIVE_LIST_ID,
                this.bladeStatElements,
                this.getBladeBaseStats(),
                this.getBladeScalingStats(),
                this.getBladePassiveStats()
        );
    }

    private void drawHandleStats() {
        this.handleStatElements.values().forEach(this.builder::removeElement);
        this.handleStatElements.clear();

        if (this.selectedHandle == null) return;

        this.syncStatPanel(
                HANDLE_BASE_LIST_ID,
                HANDLE_SCALING_LIST_ID,
                HANDLE_PASSIVE_LIST_ID,
                this.handleStatElements,
                this.getHandleBaseStats(),
                this.getHandleScalingStats(),
                this.getHandlePassiveStats()
        );
    }

    private void drawForgedStats() {
        this.forgedStatElements.values().forEach(this.builder::removeElement);
        this.forgedStatElements.clear();

        if (this.selectedBlade == null || this.selectedHandle == null) {
            this.builder.getById(FORGED_STATS_ID, GroupBuilder.class).ifPresent(el -> el.withVisible(false));
            return;
        }

        this.builder.getById(FORGED_STATS_ID, GroupBuilder.class).ifPresent(container -> {
            container.withVisible(true);

            this.syncStatPanel(
                    TOTAL_BASE_LIST_ID,
                    TOTAL_SCALING_LIST_ID,
                    TOTAL_PASSIVE_LIST_ID,
                    this.forgedStatElements,
                    this.buildForgedBaseStats(),
                    this.buildForgedScalingStats(),
                    this.buildForgedPassiveStats()
            );
        });
    }

    private void syncStatPanel(
            String baseListId,
            String scalingListId,
            String passiveListId,
            Map<String, UIElementBuilder<?>> cache,
            Map<String, Float> baseStats,
            Map<String, Float> scalingStats,
            Map<String, Float> passiveStats
    ) {
        this.syncStatList(baseListId, cache, "base", baseStats);
        this.syncStatList(scalingListId, cache, "scaling", scalingStats);
        this.syncStatList(passiveListId, cache, "passive", passiveStats);
    }

    private void syncStatList(
            String containerId,
            Map<String, UIElementBuilder<?>> cache,
            String categoryPrefix,
            Map<String, Float> values
    ) {
        this.builder.getById(containerId, GroupBuilder.class).ifPresent(container -> {
            Set<String> nextKeys = values.keySet().stream()
                    .map(key -> categoryPrefix + ":" + key)
                    .collect(Collectors.toSet());

            cache.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(categoryPrefix + ":"))
                    .filter(entry -> !nextKeys.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .forEach(this.builder::removeElement);

            values.forEach((key, value) -> {
                String cacheKey = categoryPrefix + ":" + key;
                UIElementBuilder<?> existing = cache.get(cacheKey);

                if (existing != null) {
                    container.addChild(existing);
                    return;
                }

                GroupBuilder row = this.buildStatRow(key, value);
                cache.put(cacheKey, row);
                container.addChild(row);
            });
        });
    }

    private GroupBuilder buildStatRow(String key, Float value) {
        return new GroupBuilder()
                .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                .withAnchor(new HyUIAnchor().setHeight(26))
                .addChild(
                        new LabelBuilder()
                                .withFlexWeight(2)
                                .withText(key)
                )
                .addChild(
                        new LabelBuilder()
                                .withFlexWeight(1)
                                .withText(DECIMAL_FORMAT.format(value))
                );
    }

    private void setSelectedBlade(String blade) {
        this.selectedBlade = blade;
        this.drawForgedStats();
        this.drawBladeStats();
        this.drawPreviewEquation();
        this.drawForgeButton();
    }

    private void setSelectedHandle(String handle) {
        this.selectedHandle = handle;
        this.drawForgedStats();
        this.drawHandleStats();
        this.drawPreviewEquation();
        this.drawForgeButton();
    }

    private void syncSidebar(
            GroupBuilder group,
            Map<String, UIElementBuilder<?>> cache,
            Set<String> desiredItems,
            String selectedKey,
            SidebarButtonFactory factory
    ) {
        Set<String> nextKeys = new HashSet<>();

        for (String name : desiredItems) {
            UIElementBuilder<?> existing = cache.get(name);

            if (existing != null) {
                nextKeys.add(name);
                this.applySelectedStyle(existing, name, selectedKey);
                group.addChild(existing);
                continue;
            }

            UIElementBuilder<?> created = factory.create(name);
            cache.put(name, created);
            nextKeys.add(name);
            group.addChild(created);
        }

        this.removeStaleEntries(cache, nextKeys);
    }

    private void removeStaleEntries(Map<String, UIElementBuilder<?>> cache, Set<String> nextKeys) {
        cache.entrySet().stream()
                .filter(entry -> !nextKeys.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .forEach(this.builder::removeElement);

        cache.keySet().retainAll(nextKeys);
    }

    private void applySelectedStyle(UIElementBuilder<?> element, String name, String selectedKey) {
        if (!(element instanceof ButtonBuilder button)) return;

        if (name.equals(selectedKey)) {
            button.withStyle(ButtonBuilder.secondaryTextButton().getHyUIStyle());
        } else {
            button.withStyle(ButtonBuilder.tertiaryTextButton().getHyUIStyle());
        }
    }

    private void refreshStatsPanelsVisibility() {
        this.builder.getById(BLADE_STATS_PANEL_ID, ContainerBuilder.class)
                .ifPresent(el -> el.withVisible(this.isBladeStatsShown));

        this.builder.getById(HANDLE_STATS_PANEL_ID, ContainerBuilder.class)
                .ifPresent(el -> el.withVisible(this.isHandleStatsShown));
    }

    private void bindShowStatsButtons() {
        this.bindToggleStatsButton(BLADE_STATS_BUTTON, () -> this.isBladeStatsShown = !this.isBladeStatsShown, this.isBladeStatsShown);
        this.bindToggleStatsButton(HANDLE_STATS_BUTTON, () -> this.isHandleStatsShown = !this.isHandleStatsShown, this.isHandleStatsShown);
    }

    private void bindToggleStatsButton(String buttonId, Runnable toggleAction, boolean currentlyShown) {
        this.builder.getById(buttonId, ButtonBuilder.class).ifPresent(btn ->
                btn.onClick((_, ctx) -> {
                    toggleAction.run();
                    btn.withText(currentlyShown ? "+" : "-");
                    this.refreshStatsPanelsVisibility();
                    ctx.updatePage(true);
                })
        );
    }

    private ButtonBuilder buildBladeButton(String bladeName) {
        return ButtonBuilder.tertiaryTextButton()
                .withId(this.buildSafeId("blade-", bladeName))
                .withAnchor(new HyUIAnchor().setHeight(40).setBottom(5).setLeft(10))
                .withFlexWeight(1)
                .withText(bladeName)
                .onClick((_, ctx) -> {
                    this.setSelectedBlade(bladeName);
                    ctx.updatePage(true);
                });
    }

    private ButtonBuilder buildHandleButton(String handleName) {
        return ButtonBuilder.tertiaryTextButton()
                .withId(this.buildSafeId("handle-", handleName))
                .withAnchor(new HyUIAnchor().setHeight(40).setBottom(5).setLeft(10))
                .withFlexWeight(1)
                .withText(handleName)
                .onClick((_, ctx) -> {
                    this.setSelectedHandle(handleName);
                    ctx.updatePage(true);
                });
    }

    private String buildSafeId(String prefix, String name) {
        return (prefix + "-item-" + name).toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private Map<String, Float> getBladeBaseStats() {
        return VoxelWeaponConfigsHelper.getBladeStats(this.selectedBlade).getBaseDamage();
    }

    private Map<String, Float> getBladeScalingStats() {
        return VoxelWeaponConfigsHelper.getBladeStats(this.selectedBlade).getDamageScaling();
    }

    private Map<String, Float> getBladePassiveStats() {
        return VoxelWeaponConfigsHelper.getBladeStats(this.selectedBlade).getPassives();
    }

    private Map<String, Float> getHandleBaseStats() {
        return VoxelWeaponConfigsHelper.getHandleStats(this.selectedHandle).getBaseDamage();
    }

    private Map<String, Float> getHandleScalingStats() {
        return VoxelWeaponConfigsHelper.getHandleStats(this.selectedHandle).getDamageScaling();
    }

    private Map<String, Float> getHandlePassiveStats() {
        return VoxelWeaponConfigsHelper.getHandleStats(this.selectedHandle).getPassives();
    }

    private Map<String, Float> buildForgedBaseStats() {
        Map<String, Float> forged = new HashMap<>(this.getBladeBaseStats());
        this.getHandleBaseStats().forEach((key, value) ->
                forged.merge(key, value, (bladeValue, handleValue) -> (bladeValue + handleValue) / 2f)
        );
        return forged;
    }

    private Map<String, Float> buildForgedScalingStats() {
        Map<String, Float> forged = new HashMap<>(this.getBladeScalingStats());
        this.getHandleScalingStats().forEach((key, value) ->
                forged.merge(key, value, (bladeValue, handleValue) -> (bladeValue + handleValue) / 2f)
        );
        return forged;
    }

    private Map<String, Float> buildForgedPassiveStats() {
        Map<String, Float> forged = new HashMap<>(this.getBladePassiveStats());
        this.getHandlePassiveStats().forEach((key, value) ->
                forged.merge(key, value, Float::sum)
        );
        return forged;
    }

    @FunctionalInterface
    private interface SidebarButtonFactory {
        UIElementBuilder<?> create(String name);
    }
}
