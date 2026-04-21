package dev.VoxelTales.UI.Components;

import au.ellie.hyui.builders.*;
import au.ellie.hyui.elements.LayoutModeSupported;
import au.ellie.hyui.types.LayoutMode;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ModalUI {
    public enum FieldType { TEXT, NUMBER }
    public enum ButtonDirection { VERTICAL, HORIZONTAL }

    private final String title;
    private final String confirmText;
    private String description;
    private final LinkedHashMap<String, FieldType> fields;
    private final Map<String, Object> state = new HashMap<>();
    private Consumer<Map<String, Object>> confirmCallback = _ -> {};
    private Runnable finallyCallback = () -> {};
    private int height = 350;
    private int width = 400;
    private ButtonDirection buttonDirection = ButtonDirection.HORIZONTAL;

    public ModalUI(String title, String confirmText, LinkedHashMap<String, FieldType> fields) {
        this.title = title;
        this.confirmText = confirmText;
        this.fields = fields;
    }

    public void onConfirm(Consumer<Map<String, Object>> callback) { this.confirmCallback = callback; }
    public void onFinally(Runnable callback) {
        this.finallyCallback = callback;
    }

    public void open(InterfaceBuilder<?> builder, UIElementBuilder<?> parent) {
        this.fields.forEach((key, type) -> {
            if (type == FieldType.TEXT) this.state.put(key, "");
            else if (type == FieldType.NUMBER) this.state.put(key, 0.0f);
        });

        PageOverlayBuilder overlay = PageOverlayBuilder.pageOverlay()
                .withVisible(true)
                .withLayoutMode(LayoutMode.Center);

        ContainerBuilder window = ContainerBuilder.container()
                .withTitleText(this.title)
                .withAnchor(new HyUIAnchor().setWidth(this.width).setHeight(this.height));

        GroupBuilder wrapperGroup = GroupBuilder.group()
                .withPadding(HyUIPadding.all(20))
                .withLayoutMode(LayoutMode.Top)
                .withFlexWeight(1);

        if (this.description != null) {
            LabelBuilder descriptionLabel = LabelBuilder.label()
                    .withText(this.description)
                    .withStyle(new HyUIStyle().setWrap(true))
                    .withPadding(HyUIPadding.all(3));

            wrapperGroup.addChild(descriptionLabel);
        }

        if (!this.fields.isEmpty()) {
            GroupBuilder fieldsGroup = GroupBuilder.group()
                    .withLayoutMode(LayoutMode.Top)
                    .withFlexWeight(1)
                    .withAnchor(new HyUIAnchor().setTop(10));

            this.fields.forEach((fieldName, type) -> {
                GroupBuilder row = GroupBuilder.group()
                        .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                        .withAnchor(new HyUIAnchor().setHeight(40).setBottom(10))
                        .addChild(new LabelBuilder().withText(fieldName + ":").withFlexWeight(1));

                if (type == FieldType.TEXT) {
                    row.addChild(TextFieldBuilder.textInput()
                            .withFlexWeight(2)
                            .addEventListener(CustomUIEventBindingType.ValueChanged, (String val) -> this.state.put(fieldName, val))
                    );
                } else if (type == FieldType.NUMBER) {
                    row.addChild(NumberFieldBuilder.numberInput()
                            .withValue(0.0f)
                            .withFlexWeight(2)
                            .addEventListener(CustomUIEventBindingType.ValueChanged, (Double val) -> this.state.put(fieldName, val.floatValue()))
                    );
                }
                fieldsGroup.addChild(row);
            });

            wrapperGroup.addChild(fieldsGroup);
        }

        GroupBuilder footer = GroupBuilder.group()
                .withLayoutMode(this.buttonDirection == ButtonDirection.VERTICAL ?
                        LayoutModeSupported.LayoutMode.Bottom : LayoutModeSupported.LayoutMode.Left )
                .withAnchor(new HyUIAnchor().setHeight((int) (45 * (this.buttonDirection == ButtonDirection.VERTICAL ? 2.1 : 1))).setTop(10))
                .addChild(ButtonBuilder.cancelTextButton()
                        .withText("Cancel")
                        .withFlexWeight(1)
                        .withAnchor(new HyUIAnchor().setHorizontal(5).setVertical(5))
                        .onClick((data, ctx) -> {
                            builder.removeElement(overlay);
                            finallyCallback.run();
                            ctx.updatePage(true);
                        }))
                .addChild(ButtonBuilder.textButton()
                        .withText(this.confirmText)
                        .withFlexWeight(1)
                        .withAnchor(new HyUIAnchor().setHorizontal(5).setVertical(5))
                        .onClick((data, ctx) -> {
                            builder.removeElement(overlay);
                            this.confirmCallback.accept(this.state);
                            finallyCallback.run();
                            ctx.updatePage(true);
                        }));

        // 6. Assemble everything
        window.addContentChild(wrapperGroup);
        wrapperGroup.addChild(footer);
        overlay.addChild(window);

        parent.addChild(overlay);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public String getConfirmText() {
        return confirmText;
    }

    public LinkedHashMap<String, FieldType> getFields() {
        return fields;
    }

    public ButtonDirection getButtonDirection() {
        return buttonDirection;
    }

    public void setButtonDirection(ButtonDirection buttonDirection) {
        this.buttonDirection = buttonDirection;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}