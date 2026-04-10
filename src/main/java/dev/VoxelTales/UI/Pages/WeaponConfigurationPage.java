package dev.VoxelTales.UI.Pages;

import au.ellie.hyui.builders.*;
import au.ellie.hyui.elements.LayoutModeSupported;
import au.ellie.hyui.events.SelectedTabChangedEventData;
import au.ellie.hyui.events.UIContext;
import au.ellie.hyui.types.NumberFieldFormat;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Configs.VoxelWeaponConfigs;
import dev.VoxelTales.UI.Components.ModalUI;
import dev.VoxelTales.UI.Pages.Default.VoxelEditorPageUI;
import dev.VoxelTales.Utils.VoxelWeaponConfigsHelper;

import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WeaponConfigurationPage extends VoxelEditorPageUI {
    private static final String HTML_PATH = "Pages/WeaponConfigurator.html";

    private static final class Tabs {
        private static final String OVERVIEW = "overview";
        private static final String BLADES = "blades";
        private static final String HANDLES = "handles";
        private static final String NAVIGATION = "weapon-tabs";
        private static final String QUICK_JUMP_BLADES = "quick-jump-blades";
    }

    private static final class Categories {
        private static final String DAMAGE = "damage";
        private static final String SCALING = "scaling";
        private static final String PASSIVE = "passive";
    }

    private static final class Fields {
        private static final String NAME = "Name";
        private static final String VALUE = "Value";
        private static final String NEW_ID = "New ID";
    }

    private static final class ModalText {
        private static final String CREATE_NEW_PREFIX = "Create New ";
        private static final String INSERT_NEW_PREFIX = "Insert a new ";
        private static final String CONFIRM_DELETION = "Confirm Deletion";
        private static final String RENAME_ITEM = "Rename Item";
        private static final String CREATE = "Create";
        private static final String APPLY = "Apply";
        private static final String DELETE_PERMANENTLY = "Delete Permanently";
        private static final String CONFIRM = "Confirm";
    }

    private static final class ElementIds {
        private static final String ITEM_LIST_SUFFIX = "-item-list";
        private static final String TIER_INPUT_SUFFIX = "-tier-input";
        private static final String ATK_SPEED_INPUT_SUFFIX = "-atk-speed-input";
        private static final String SEARCH_BAR_SUFFIX = "-search-bar";
        private static final String TIER_FILTER_SUFFIX = "-tier-filter";
        private static final String EMPTY_STATE_SUFFIX = "-empty-state";
        private static final String EDITOR_UI_SUFFIX = "-editor-ui";
        private static final String DISPLAY_NAME_SUFFIX = "-display-name";
        private static final String INTERNAL_ID_SUFFIX = "-internal-id";
        private static final String ADD_ITEM_SUFFIX = "-add-item";
        private static final String DELETE_ITEM_SUFFIX = "-delete-item";
        private static final String RENAME_BTN_SUFFIX = "-rename-btn";
        private static final String SAVE_BTN_SUFFIX = "-save-btn";
        private static final String RESET_BTN_SUFFIX = "-reset-btn";
        private static final String ADD_CATEGORY_BTN_SUFFIX = "-add-";
        private static final String CATEGORY_BTN_SUFFIX = "-btn";
        private static final String ITEM_PREFIX = "-item-";
    }

    private static final List<String> TABS = List.of(Tabs.OVERVIEW, Tabs.BLADES, Tabs.HANDLES);
    private static final List<String> TYPES = List.of(Tabs.BLADES, Tabs.HANDLES);
    private static final List<String> TYPE_ENTRIES = List.of(Categories.DAMAGE, Categories.SCALING, Categories.PASSIVE);

    // Currently selected data
    private final HashMap<String, String> textBarFilter = new HashMap<>();
    private final HashMap<String, String> tierFilter = new HashMap<>();

    private String selectedName;
    private HashMap<String, Float> damageScaling = new HashMap<>();
    private HashMap<String, Float> baseDamage = new HashMap<>();
    private HashMap<String, Float> passives = new HashMap<>();
    private Integer tier;
    private Float attackSpeed;

    private final Map<String, Set<UIElementBuilder<?>>> inspectionElements = new HashMap<>();
    private final Map<String, Set<UIElementBuilder<?>>> sideBarElements = new HashMap<>();

    public WeaponConfigurationPage(PlayerRef playerRef) {
        super(playerRef);
    }

    public void update() {
        super.update(HTML_PATH);

        for (String tabId : TYPES) {
            this.builder.getById(tabId + ElementIds.ITEM_LIST_SUFFIX, GroupBuilder.class)
                    .ifPresent(group -> populateSidebar(tabId, group));

            this.builder.getById(tabId + ElementIds.TIER_INPUT_SUFFIX, NumberFieldBuilder.class).ifPresent(el ->
                    el.addEventListener(CustomUIEventBindingType.ValueChanged, (newTier) -> {
                        this.isDirty = true;
                        this.tier = newTier.intValue();
                    })
            );

            this.builder.getById(tabId + ElementIds.ATK_SPEED_INPUT_SUFFIX, NumberFieldBuilder.class).ifPresent(el ->
                    el.addEventListener(CustomUIEventBindingType.ValueChanged, (newSpd) -> {
                        this.isDirty = true;
                        this.attackSpeed = newSpd.floatValue();
                    })
            );

            this.builder.getById(tabId + ElementIds.SEARCH_BAR_SUFFIX, TextFieldBuilder.class).ifPresent(el -> {
                        el.addEventListener(CustomUIEventBindingType.FocusLost, (newVal, context) -> {
                            this.textBarFilter.put(tabId, newVal);
                            context.getById(tabId + ElementIds.ITEM_LIST_SUFFIX, GroupBuilder.class)
                                    .ifPresent(group -> populateSidebar(tabId, group));

                            context.updatePage(true);
                        });
                    }
            );
            this.builder.getById(tabId + ElementIds.TIER_FILTER_SUFFIX, DropdownBoxBuilder.class).ifPresent(el -> {
                el.addEventListener(CustomUIEventBindingType.ValueChanged, (newVal, context) -> {
                    this.tierFilter.put(tabId, newVal);
                    context.getById(tabId + ElementIds.ITEM_LIST_SUFFIX, GroupBuilder.class)
                            .ifPresent(group -> populateSidebar(tabId, group));

                    el.withValue(newVal);
                    context.updatePage(true);
                });
            });

            this.bindAddItemButton(tabId);
            this.bindDeleteButton(tabId);
            this.bindRenameButton(tabId);
            this.bindAddButtons(tabId);
            this.bindResetButton(tabId);
            this.bindSaveButton(tabId);
        }

        this.refreshTabVisibility(Tabs.OVERVIEW, null);
        this.builder.addEventListener(Tabs.NAVIGATION, CustomUIEventBindingType.SelectedTabChanged, (data, context) -> {
            String selected = ((SelectedTabChangedEventData) data).getSelectedTab();

            Runnable navigationTask = () -> {
                this.refreshTabVisibility(selected, context);
                context.updatePage(false);
            };

            withDiscardConfirmation(context, navigationTask);
        });

        bindButtonClick(Tabs.QUICK_JUMP_BLADES, (_, context) -> {
            this.refreshTabVisibility(Tabs.BLADES, context);
            context.updatePage(false);
        });
    }

    public void open() {
        this.isDirty = false;
        super.open();
    }

    private void refreshTabVisibility(String activeTab, UIContext context) {
        if (context != null) {
            context.getById(Tabs.NAVIGATION, NativeTabNavigationBuilder.class)
                    .ifPresent(nav -> nav.withSelectedTab(activeTab));
        }

        for (String tabId : TABS) {
            boolean isVisible = tabId.equals(activeTab);

            if (context != null) {
                context.getById(tabId, TabContentBuilder.class)
                        .ifPresent(el -> el.withVisible(isVisible));
            } else {
                this.builder.getById(tabId, TabContentBuilder.class)
                        .ifPresent(el -> el.withVisible(isVisible));
            }
        }
    }

    private LinkedHashMap<String, ModalUI.FieldType> getNewPairField() {
        LinkedHashMap<String, ModalUI.FieldType> newWeaponFields = new LinkedHashMap<>();
        newWeaponFields.put(Fields.NAME, ModalUI.FieldType.TEXT);
        newWeaponFields.put(Fields.VALUE, ModalUI.FieldType.NUMBER);

        return newWeaponFields;
    }

    private void bindAddItemButton(String type) {
        bindButtonClick(type + ElementIds.ADD_ITEM_SUFFIX, (_, context) -> {
            LinkedHashMap<String, ModalUI.FieldType> fields = new LinkedHashMap<>();
            fields.put(Fields.NAME, ModalUI.FieldType.TEXT);

            ModalUI modal = new ModalUI(ModalText.CREATE_NEW_PREFIX + type, ModalText.CREATE, fields);
            modal.setDescription("Enter a unique Name (e.g., 'Sharp').");
            modal.setHeight(200);

            modalConfirmHelper(modal, (root, dataMap) -> {
                String newId = dataMap.get(Fields.NAME).toString().trim();
                if (newId.isEmpty()) return;

                VoxelWeaponConfigs.ComponentStats newStats = new VoxelWeaponConfigs.ComponentStats();
                VoxelWeaponConfigsHelper.saveStatsOf(type, newId, newStats);

                context.getById(type + ElementIds.ITEM_LIST_SUFFIX, GroupBuilder.class).ifPresent(group -> {
                    this.populateSidebar(type, group);
                });

                this.selectEntry(type, newId, context);

                context.updatePage(true);
            });
            context.updatePage(true);
        });
    }

    private void bindDeleteButton(String type) {
        bindButtonClick(type + ElementIds.DELETE_ITEM_SUFFIX, (_, context) -> {
            ModalUI modal = new ModalUI(ModalText.CONFIRM_DELETION, ModalText.DELETE_PERMANENTLY, new LinkedHashMap<>());
            modal.setDescription("Are you sure you want to delete '" + this.selectedName + "'? This action cannot be undone!");
            modal.setHeight(230);
            modal.setButtonDirection(ModalUI.ButtonDirection.VERTICAL);

            modalConfirmHelper(modal, (_, _) -> {
                VoxelWeaponConfigsHelper.deleteEntry(type, this.selectedName);

                context.getById(type + ElementIds.ITEM_LIST_SUFFIX, GroupBuilder.class).ifPresent(group -> {
                    this.populateSidebar(type, group);
                });

                context.getById(type + ElementIds.EMPTY_STATE_SUFFIX, GroupBuilder.class).ifPresent(el -> el.withVisible(true));
                context.getById(type + ElementIds.EDITOR_UI_SUFFIX, GroupBuilder.class).ifPresent(el -> el.withVisible(false));

                this.selectedName = null;
                this.isDirty = false;

                context.updatePage(true);
            });
            context.updatePage(true);
        });
    }

    private void bindRenameButton(String type) {
        bindButtonClick(type + ElementIds.RENAME_BTN_SUFFIX, (_, context) -> {
            LinkedHashMap<String, ModalUI.FieldType> renameFields = new LinkedHashMap<>();
            renameFields.put(Fields.NEW_ID, ModalUI.FieldType.TEXT);

            ModalUI modal = new ModalUI(ModalText.RENAME_ITEM, ModalText.APPLY, renameFields);
            modal.setHeight(250);
            modal.setButtonDirection(ModalUI.ButtonDirection.VERTICAL);

            modalConfirmHelper(modal, (root, dataMap) -> {
                String newId = dataMap.get(Fields.NEW_ID).toString();

                if (!newId.isEmpty()) {
                    VoxelWeaponConfigsHelper.renameEntry(type, this.selectedName, newId);

                    this.selectedName = newId;

                    context.getById(type + ElementIds.DISPLAY_NAME_SUFFIX, LabelBuilder.class).ifPresent(el -> el.withText(newId));
                    context.getById(type + ElementIds.INTERNAL_ID_SUFFIX, LabelBuilder.class).ifPresent(el -> el.withText("ID: " + newId));

                    context.getById(type + ElementIds.ITEM_LIST_SUFFIX, GroupBuilder.class).ifPresent(group -> {
                        this.populateSidebar(type, group);
                    });
                }

                context.updatePage(false);
            });
            context.updatePage(true);
        });
    }

    private void bindSaveButton(String type) {
        bindButtonClick(type + ElementIds.SAVE_BTN_SUFFIX, (_, _) -> {
            VoxelWeaponConfigs.ComponentStats stats = VoxelWeaponConfigsHelper.getStatsOf(type, this.selectedName);
            if (stats == null) {
                stats = new VoxelWeaponConfigs.ComponentStats();
            }

            stats.setBaseDamage(this.baseDamage);
            stats.setDamageScaling(this.damageScaling);
            stats.setPassives(this.passives);
            stats.setTier(this.tier);
            stats.setAttackSpeed(this.attackSpeed);

            VoxelWeaponConfigsHelper.saveStatsOf(type, this.selectedName, stats);

            this.isDirty = false;
            this.playSaveNotification(type, this.selectedName);
        });
    }

    private void bindResetButton(String type) {
        bindButtonClick(type + ElementIds.RESET_BTN_SUFFIX, (_, context) ->
                this.withDiscardConfirmation(context, () ->
                        this.selectEntry(type, this.selectedName, context)));
    }

    private void bindAddButtons(String type) {
        TYPE_ENTRIES.forEach(categoryName -> bindButtonClick(type + ElementIds.ADD_CATEGORY_BTN_SUFFIX + categoryName + ElementIds.CATEGORY_BTN_SUFFIX, (_, context) -> {
            ModalUI modal = new ModalUI(ModalText.INSERT_NEW_PREFIX + categoryName, ModalText.CONFIRM, getNewPairField());
            modal.setHeight(230);

            modalConfirmHelper(modal, (_, dataMap) -> {
                Map<String, HashMap<String, Float>> categories = Map.of(
                        Categories.DAMAGE, this.baseDamage,
                        Categories.SCALING, this.damageScaling,
                        Categories.PASSIVE, this.passives
                );

                String name = dataMap.get(Fields.NAME).toString();
                float value = Float.parseFloat(dataMap.get(Fields.VALUE).toString());

                this.isDirty = true;

                categories.get(categoryName).put(name, value);
                buildSelectedSide(context, type);

                context.updatePage(true);
            });

            context.updatePage(true);
        }));
    }

    private GroupBuilder buildInspectionRow(String key, Float value, Consumer<Double> valueChangedCallback) {
        return new GroupBuilder()
                .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                .withAnchor(new HyUIAnchor()
                        .setBottom(10)
                        .setHeight(32)
                )
                .addChild(
                        new LabelBuilder()
                                .withFlexWeight(1)
                                .withAnchor(new HyUIAnchor().setVertical(0).setLeft(0))
                                .withText(key)
                )
                .addChild(
                        NumberFieldBuilder.numberInput()
                                .withStyle(NumberFieldBuilder.numberInput().getHyUIStyle())
                                .withFlexWeight(1)
                                .withFormat(new NumberFieldFormat()
                                        .withMaxDecimalPlaces(2)
                                        .withStep(0.01f)
                                        .withMinValue(0.01f)
                                )
                                .withValue(value)
                                .addEventListener(CustomUIEventBindingType.ValueChanged, valueChangedCallback)
                );
    }

    private void selectEntry(String type, String name, UIContext context) {
        VoxelWeaponConfigs.ComponentStats stats = VoxelWeaponConfigsHelper.getStatsOf(type, name);
        if (stats == null) {
            return;
        }

        this.selectedName = name;
        this.tier = stats.getTier();
        this.attackSpeed = stats.getAttackSpeed() != null ? stats.getAttackSpeed() : 1.0f;
        this.baseDamage = new HashMap<>(stats.getBaseDamage());
        this.damageScaling = new HashMap<>(stats.getDamageScaling());
        this.passives = new HashMap<>(stats.getPassives());

        if (this.selectedName != null && !this.selectedName.equals(name)) {
            String oldSafeId = (type + ElementIds.ITEM_PREFIX + this.selectedName).toLowerCase().replaceAll("[^a-z0-9]", "");
            context.getById(oldSafeId, ButtonBuilder.class)
                    .ifPresent(el -> el.withStyle(ButtonBuilder.tertiaryTextButton().getHyUIStyle()));
        }

        context.getById(type + ElementIds.EMPTY_STATE_SUFFIX, GroupBuilder.class).ifPresent(el -> el.withVisible(false));
        context.getById(type + ElementIds.EDITOR_UI_SUFFIX, GroupBuilder.class).ifPresent(el -> el.withVisible(true));
        context.getById(type + ElementIds.DISPLAY_NAME_SUFFIX, LabelBuilder.class).ifPresent(el -> el.withText(name));
        context.getById(type + ElementIds.INTERNAL_ID_SUFFIX, LabelBuilder.class).ifPresent(el -> el.withText("ID: " + name));

        context.getById(type + ElementIds.TIER_INPUT_SUFFIX, NumberFieldBuilder.class).ifPresent(el ->
                el.withValue(this.tier)
        );

        context.getById(type + ElementIds.ATK_SPEED_INPUT_SUFFIX, NumberFieldBuilder.class).ifPresent(el ->
                el.withValue(this.attackSpeed)
        );

        this.isDirty = false;
        buildSelectedSide(context, type);

        context.updatePage(true);
    }

    private void buildSelectedSide(UIContext context, String type) {
        Set<UIElementBuilder<?>> currentInspectionElements = this.inspectionElements.computeIfAbsent(type, _ -> new HashSet<>());
        currentInspectionElements.forEach(this.builder::removeElement);
        currentInspectionElements.clear();

        Map<String, HashMap<String, Float>> categories = Map.of(
                Categories.DAMAGE, this.baseDamage,
                Categories.SCALING, this.damageScaling,
                Categories.PASSIVE, this.passives
        );

        categories.forEach((categoryName, dataMap) -> context.getById(type + "-" + categoryName + "-container", GroupBuilder.class).ifPresent(container -> dataMap.forEach((key, value) -> {
            GroupBuilder created = buildInspectionRow(key, value, (newVal) -> {
                this.isDirty = true;
                dataMap.put(key, newVal.floatValue());
            });

            currentInspectionElements.add(created);
            container.addChild(created);
        })));
    }

    private void populateSidebar(String type, GroupBuilder group) {
        Set<UIElementBuilder<?>> currentTabElements = this.sideBarElements.computeIfAbsent(type, _ -> new HashSet<>());
        currentTabElements.forEach(this.builder::removeElement);
        currentTabElements.clear();

        Set<String> names = VoxelWeaponConfigsHelper.getListOfNames(type);

        String nameFilter = this.textBarFilter.getOrDefault(type, "").toLowerCase();
        String tierFilter = this.tierFilter.getOrDefault(type, "0");

        List<String> sortedNames = names.stream()
                .filter(name -> {
                    if (!nameFilter.isEmpty() && !name.toLowerCase().contains(nameFilter)) {
                        return false;
                    }

                    if (!tierFilter.equals("0")) {
                        VoxelWeaponConfigs.ComponentStats stats = VoxelWeaponConfigsHelper.getStatsOf(type, name);
                        if (stats == null) return false;

                        try {
                            int targetTier = Integer.parseInt(tierFilter);
                            if (stats.getTier() != targetTier) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            LoggerUtil.getLogger().warning("Tier filter value wasn't a number!\n" + Arrays.toString(e.getStackTrace()));

                            return false;
                        }
                    }

                    return true;
                })
                .sorted()
                .toList();

        for (String name : sortedNames) {
            if (name.equals("default")) {
                continue;
            }

            ButtonBuilder btn = buildSideButton(type, name, (Void _, UIContext ctx) ->
                    withDiscardConfirmation(ctx, () -> selectEntry(type, name, ctx))
            );

            if (name.equals(this.selectedName)) {
                btn.withStyle(ButtonBuilder.secondaryTextButton().getHyUIStyle());
            }

            group.addChild(btn);
        }
    }

    private ButtonBuilder buildSideButton(String type, String name, BiConsumer<Void, UIContext> callback) {
        String safeId = (type + ElementIds.ITEM_PREFIX + name).toLowerCase().replaceAll("[^a-z0-9]", "");

        var btn = ButtonBuilder.tertiaryTextButton()
                .withId(safeId)
                .withAnchor(new HyUIAnchor().setHeight(40).setBottom(5))
                .withFlexWeight(1)
                .withText(name)
                .onClick(callback);

        this.sideBarElements.computeIfAbsent(type, _ -> new HashSet<>()).add(btn);

        return btn;
    }

    private void playSaveNotification(String type, String name) {
        super.notifySuccess("Success!", "Successfully saved " + type + ": " + name, "Weapon_Sword_Steel", "SFX_Level_Up_Generic");
    }
}