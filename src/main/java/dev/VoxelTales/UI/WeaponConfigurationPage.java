package dev.VoxelTales.UI;

import au.ellie.hyui.builders.*;
import au.ellie.hyui.elements.LayoutModeSupported;
import au.ellie.hyui.events.SelectedTabChangedEventData;
import au.ellie.hyui.events.UIContext;
import au.ellie.hyui.types.NumberFieldFormat;
import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import dev.VoxelTales.Configs.VoxelWeaponLookup;
import dev.VoxelTales.UI.Components.ModalUI;
import dev.VoxelTales.UI.Default.VoxelEditorPageUI;
import dev.VoxelTales.UI.Default.VoxelPageUI;
import dev.VoxelTales.Utils.VoxelWeaponConfigsHelper;

import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WeaponConfigurationPage extends VoxelEditorPageUI {
    private static final List<String> TABS = List.of("overview", "blades", "handles");
    private static final List<String> TYPE_ENTRIES = List.of("damage", "scaling", "passive");

    //Currently selected data;
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
        super.update("Pages/WeaponConfigurator.html");

        for (String tabId : List.of("blades", "handles")) {
            this.builder.getById(tabId + "-item-list", GroupBuilder.class)
                    .ifPresent(group -> populateSidebar(tabId, group));

            this.builder.getById(tabId + "-tier-input", NumberFieldBuilder.class).ifPresent(el ->
                    el.addEventListener(CustomUIEventBindingType.ValueChanged, (newTier) -> {
                        this.isDirty = true;
                        this.tier = newTier.intValue();
                    })
            );

            this.builder.getById(tabId + "-atk-speed-input", NumberFieldBuilder.class).ifPresent(el ->
                    el.addEventListener(CustomUIEventBindingType.ValueChanged, (newSpd) -> {
                        this.isDirty = true;
                        this.attackSpeed = newSpd.floatValue();
                    })
            );

            this.builder.getById(tabId + "-search-bar", TextFieldBuilder.class).ifPresent(el -> {
                el.addEventListener(CustomUIEventBindingType.FocusLost, (newVal, context) -> {
                            this.textBarFilter.put(tabId, newVal);
                            context.getById(tabId + "-item-list", GroupBuilder.class)
                                    .ifPresent(group -> populateSidebar(tabId, group));

                            context.updatePage(true);
                        });
                    }
            );
            this.builder.getById(tabId + "-tier-filter", DropdownBoxBuilder.class).ifPresent(el -> {
                el.addEventListener(CustomUIEventBindingType.ValueChanged, (newVal, context) -> {
                    this.tierFilter.put(tabId, newVal);
                    context.getById(tabId + "-item-list", GroupBuilder.class)
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

        this.refreshTabVisibility("overview", null);
        this.builder.addEventListener("weapon-tabs", CustomUIEventBindingType.SelectedTabChanged, (data, context) -> {
            String selected = ((SelectedTabChangedEventData) data).getSelectedTab();

            Runnable navigationTask = () -> {
                this.refreshTabVisibility(selected, context);
                context.updatePage(false);
            };

            withDiscardConfirmation(context, navigationTask);
        });

        this.builder.getById("quick-jump-blades", ButtonBuilder.class).ifPresent(btn ->
                btn.onClick((_, context) -> {
                    this.refreshTabVisibility("blades", context);
                    context.updatePage(false);
                })
        );
    }

    @Override
    public void open() {
        this.isDirty = false;
        super.open();
    }

    private void refreshTabVisibility(String activeTab, UIContext context) {
        if (context != null) {
            context.getById("weapon-tabs", NativeTabNavigationBuilder.class)
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
        newWeaponFields.put("Name", ModalUI.FieldType.TEXT);
        newWeaponFields.put("Value", ModalUI.FieldType.NUMBER);

        return newWeaponFields;
    }

    private void bindAddItemButton(String type) {
        this.builder.getById(type + "-add-item", ButtonBuilder.class).ifPresent(btn ->
                btn.onClick((_, context) -> {
                    LinkedHashMap<String, ModalUI.FieldType> fields = new LinkedHashMap<>();
                    fields.put("Name", ModalUI.FieldType.TEXT);

                    ModalUI modal = new ModalUI("Create New " + type, "Create", fields);
                    modal.setDescription("Enter a unique Name (e.g., 'Sharp').");
                    modal.setHeight(200);

                    this.builder.getById("page-root", GroupBuilder.class).ifPresent(root -> {
                        this.builder.getById("main-overlay", PageOverlayBuilder.class).ifPresent(overlay -> {
                            overlay.withVisible(false);
                            modal.onFinally(() -> overlay.withVisible(true));
                        });

                        modal.onConfirm(dataMap -> {
                            String newId = dataMap.get("Name").toString().trim();
                            if (newId.isEmpty()) return;

                            VoxelWeaponLookup.ComponentStats newStats = new VoxelWeaponLookup.ComponentStats();
                            VoxelWeaponConfigsHelper.saveStatsOf(type, newId, newStats);

                            context.getById(type + "-item-list", GroupBuilder.class).ifPresent(group -> {
                                this.populateSidebar(type, group);
                            });

                            this.selectEntry(type, newId, context);

                            context.updatePage(true);
                        });

                        modal.open(this.builder, root);
                    });
                    context.updatePage(true);
                })
        );
    }

    private void bindDeleteButton(String type) {
        this.builder.getById(type + "-delete-item", ButtonBuilder.class).ifPresent(btn ->
                btn.onClick((_, context) -> {
                    ModalUI modal = new ModalUI("Confirm Deletion", "Delete Permanently", new LinkedHashMap<>());
                    modal.setDescription("Are you sure you want to delete '" + this.selectedName + "'? This action cannot be undone!");
                    modal.setHeight(230);
                    modal.setButtonDirection(ModalUI.ButtonDirection.VERTICAL);

                    this.builder.getById("page-root", GroupBuilder.class).ifPresent(root -> {
                        this.builder.getById("main-overlay", PageOverlayBuilder.class).ifPresent(overlay -> {
                            overlay.withVisible(false);
                            modal.onFinally(() -> overlay.withVisible(true));
                        });

                        modal.onConfirm(_ -> {
                            VoxelWeaponConfigsHelper.deleteEntry(type, this.selectedName);

                            context.getById(type + "-item-list", GroupBuilder.class).ifPresent(group -> {
                                this.populateSidebar(type, group);
                            });

                            context.getById(type + "-empty-state", GroupBuilder.class).ifPresent(el -> el.withVisible(true));
                            context.getById(type + "-editor-ui", GroupBuilder.class).ifPresent(el -> el.withVisible(false));

                            this.selectedName = null;
                            this.isDirty = false;

                            context.updatePage(true);
                        });

                        modal.open(this.builder, root);
                    });
                    context.updatePage(true);
                })
        );
    }

    private void bindRenameButton(String type) {
        this.builder.getById(type + "-rename-btn", ButtonBuilder.class).ifPresent(btn ->
                btn.onClick((_, context) -> {
                    LinkedHashMap<String, ModalUI.FieldType> renameFields = new LinkedHashMap<>();
                    renameFields.put("New ID", ModalUI.FieldType.TEXT);

                    ModalUI modal = new ModalUI("Rename Item", "Apply", renameFields);
                    modal.setHeight(250);
                    modal.setButtonDirection(ModalUI.ButtonDirection.VERTICAL);

                    this.builder.getById("page-root", GroupBuilder.class).ifPresent(root -> {
                        this.builder.getById("main-overlay", PageOverlayBuilder.class).ifPresent(overlay -> {
                            overlay.withVisible(false);
                            modal.onFinally(() -> overlay.withVisible(true));
                        });

                        modal.onConfirm(dataMap -> {
                            String newId = dataMap.get("New ID").toString();

                            if (!newId.isEmpty()) {
                                VoxelWeaponConfigsHelper.renameEntry(type, this.selectedName, newId);

                                this.selectedName = newId;

                                context.getById(type + "-display-name", LabelBuilder.class).ifPresent(el -> el.withText(newId));
                                context.getById(type + "-internal-id", LabelBuilder.class).ifPresent(el -> el.withText("ID: " + newId));

                                context.getById(type + "-item-list", GroupBuilder.class).ifPresent(group -> {
                                    this.populateSidebar(type, group);
                                });
                            }

                            context.updatePage(false);
                        });

                        modal.open(this.builder, root);
                    });
                    context.updatePage(true);
                })
        );
    }

    private void bindSaveButton(String type) {
        this.builder.getById(type + "-save-btn", ButtonBuilder.class)
                .ifPresent(btn -> btn.onClick((Void _, UIContext _) -> {
                    VoxelWeaponLookup.ComponentStats stats = VoxelWeaponConfigsHelper.getStatsOf(type, this.selectedName);
                    if (stats == null) {
                        stats = new VoxelWeaponLookup.ComponentStats();
                    }

                    stats.setBaseDamage(this.baseDamage);
                    stats.setDamageScaling(this.damageScaling);
                    stats.setPassives(this.passives);
                    stats.setTier(this.tier);
                    stats.setAttackSpeed(this.attackSpeed);

                    VoxelWeaponConfigsHelper.saveStatsOf(type, this.selectedName, stats);

                    this.isDirty = false;
                    this.playSaveNotification(type, this.selectedName);
                }));
    }

    private void bindResetButton(String type) {
        this.builder.getById(type + "-reset-btn", ButtonBuilder.class)
                .ifPresent(btn ->
                        btn.onClick((Void _, UIContext context) ->
                                this.withDiscardConfirmation(context, () ->
                                        this.selectEntry(type, this.selectedName, context))));
    }

    private void bindAddButtons(String type) {
        TYPE_ENTRIES.forEach(categoryName -> {
            String buttonId = type + "-add-" + categoryName + "-btn";

            this.builder.getById(buttonId, ButtonBuilder.class).ifPresent(btn -> btn.onClick((_, context) -> {
                ModalUI modal = new ModalUI("Insert a new " + categoryName, "Confirm", getNewPairField());
                modal.setHeight(230);

                this.builder.getById("page-root", GroupBuilder.class).ifPresent(root -> {
                    this.builder.getById("main-overlay", PageOverlayBuilder.class).ifPresent(overlay -> {
                        overlay.withVisible(false);

                        modal.onFinally(() -> overlay.withVisible(true));
                    });

                    modal.onConfirm(dataMap -> {
                        Map<String, HashMap<String, Float>> categories = Map.of(
                                "damage", this.baseDamage,
                                "scaling", this.damageScaling,
                                "passive", this.passives
                        );

                        String name = dataMap.get("Name").toString();
                        float value = Float.parseFloat(dataMap.get("Value").toString());

                        this.isDirty = true;

                        categories.get(categoryName).put(name, value);
                        buildSelectedSide(context, type);

                        context.updatePage(true);
                        //LoggerUtil.getLogger().info("Data received: \nName: " + name + "\nValue: " + value);
                    });

                    modal.open(this.builder, root);
                });

                context.updatePage(true);
                //LoggerUtil.getLogger().info("Clicked Add " + categoryName + " for " + type);
            }));
        });
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
        VoxelWeaponLookup.ComponentStats stats = VoxelWeaponConfigsHelper.getStatsOf(type, name);
        if (stats == null) { return; }

        this.selectedName = name;
        this.tier = stats.getTier();
        this.attackSpeed = stats.getAttackSpeed() != null ? stats.getAttackSpeed() : 1.0f;
        this.baseDamage = new HashMap<>(stats.getBaseDamage());
        this.damageScaling = new HashMap<>(stats.getDamageScaling());
        this.passives = new HashMap<>(stats.getPassives());

        if (this.selectedName != null && !this.selectedName.equals(name)) {
            String oldSafeId = (type + "-item-" + this.selectedName).toLowerCase().replaceAll("[^a-z0-9]", "");
            context.getById(oldSafeId, ButtonBuilder.class)
                    .ifPresent(el -> el.withStyle(ButtonBuilder.tertiaryTextButton().getHyUIStyle()));
        }

        context.getById(type + "-empty-state", GroupBuilder.class).ifPresent(el -> el.withVisible(false));
        context.getById(type + "-editor-ui", GroupBuilder.class).ifPresent(el -> el.withVisible(true));
        context.getById(type + "-display-name", LabelBuilder.class).ifPresent(el -> el.withText(name));
        context.getById(type + "-internal-id", LabelBuilder.class).ifPresent(el -> el.withText("ID: " + name));

        context.getById(type + "-tier-input", NumberFieldBuilder.class).ifPresent(el ->
                el.withValue(this.tier)
        );

        context.getById(type + "-atk-speed-input", NumberFieldBuilder.class).ifPresent(el ->
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
                "damage", this.baseDamage,
                "scaling", this.damageScaling,
                "passive", this.passives
        );

        categories.forEach((categoryName, dataMap) -> context.getById(type + "-" + categoryName + "-container", GroupBuilder.class).ifPresent(container -> dataMap.forEach((key, value) -> {
            GroupBuilder created = buildInspectionRow(key, value, (newVal) -> {
                this.isDirty = true;
                dataMap.put(key, newVal.floatValue());
            });

            // Add to the specific tab's set
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
                        VoxelWeaponLookup.ComponentStats stats = VoxelWeaponConfigsHelper.getStatsOf(type, name);
                        if (stats == null) return false;

                        try {
                            int targetTier = Integer.parseInt(tierFilter);
                            if (stats.getTier() != targetTier) {
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            LoggerUtil.getLogger().warning("Tier filter value wasn't a number!\n" + Arrays.toString(e.getStackTrace()));
                        }
                    }

                    return true;
                })
                .sorted()
                .toList();

        for (String name : sortedNames) {
            if (name.equals("default")) continue;

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
        String safeId = (type + "-item-" + name).toLowerCase().replaceAll("[^a-z0-9]", "");

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