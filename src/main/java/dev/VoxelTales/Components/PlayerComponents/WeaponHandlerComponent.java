package dev.VoxelTales.Components.PlayerComponents;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Assets.Gameplay.WeaponType;
import dev.VoxelTales.Configs.VoxelSkillConfigs;
import dev.VoxelTales.Configs.VoxelWeaponConfigs;
import dev.VoxelTales.Registries.VoxelComponentsRegistry;
import dev.VoxelTales.Registries.VoxelSkillsRegistry;
import dev.VoxelTales.Utils.VoxelWeaponConfigsHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WeaponHandlerComponent implements Component<EntityStore> {
    private String currentBlade;
    private String currentHandle;
    private String selectedSkill;
    private String selectedUltimate;
    private int swordPoints;
    private int swordXP;
    private int swordInternalLevel;

    private WeaponType composedWeaponType;

    public static ComponentType<EntityStore, WeaponHandlerComponent> getComponentType() {
        return VoxelComponentsRegistry.staticGetComponentType(WeaponHandlerComponent.class);
    }

    public static final BuilderCodec<WeaponHandlerComponent> CODEC =
            BuilderCodec.builder(WeaponHandlerComponent.class, WeaponHandlerComponent::new)
                    .append(new KeyedCodec<>("CurrentBlade", Codec.STRING),
                            WeaponHandlerComponent::setCurrentBlade,
                            WeaponHandlerComponent::getCurrentBlade)
                    .addValidator(Validators.nonNull())
                    .add()
                    .append(new KeyedCodec<>("CurrentHandle", Codec.STRING),
                            WeaponHandlerComponent::setCurrentHandle,
                            WeaponHandlerComponent::getCurrentHandle)
                    .addValidator(Validators.nonNull())
                    .add()
                    .append(new KeyedCodec<>("SelectedSkill", Codec.STRING),
                            (data, value) -> data.selectedSkill = value,
                            data -> data.selectedSkill) // getter
                    .addValidator(Validators.nonNull())
                    .add()
                    .append(new KeyedCodec<>("SelectedUltimate", Codec.STRING),
                            (data, value) -> data.selectedUltimate = value,
                            data -> data.selectedUltimate) // getter
                    .addValidator(Validators.nonNull())
                    .add()
                    .append(new KeyedCodec<>("SwordPoints", Codec.INTEGER),
                            (data, value) -> data.swordPoints = value,
                            data -> data.swordPoints) // getter
                    .addValidator(Validators.nonNull())
                    .add()
                    .append(new KeyedCodec<>("SwordXP", Codec.INTEGER),
                            (data, value) -> data.swordXP = value,
                            data -> data.swordXP) // getter
                    .addValidator(Validators.nonNull())
                    .add()
                    .append(new KeyedCodec<>("SwordInternalLevel", Codec.INTEGER),
                            (data, value) -> data.swordInternalLevel = value,
                            data -> data.swordInternalLevel) // getter
                    .addValidator(Validators.nonNull())
                    .add()
                    .build();

    public WeaponHandlerComponent() {
        this.currentBlade = "Starter";
        this.currentHandle = "Starter";
        this.selectedSkill = "Root_Thrust_Simple";
        this.selectedUltimate = "Root_Vortex_Strike";
        this.swordPoints = 0;
        this.swordXP = 0;
        this.swordInternalLevel = 0;
    }

    public WeaponHandlerComponent(WeaponHandlerComponent clone) {
        this.currentBlade = clone.currentBlade;
        this.currentHandle = clone.currentHandle;
        this.selectedSkill = clone.selectedSkill;
        this.selectedUltimate = clone.selectedUltimate;
        this.swordPoints = clone.swordPoints;
        this.swordXP = clone.swordXP;
        this.swordInternalLevel = clone.swordInternalLevel;
        this.composedWeaponType = clone.composedWeaponType;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() { return new WeaponHandlerComponent(this); }

    private WeaponType recalculateWeight() {
        VoxelWeaponConfigs.ComponentStats bladeStats = VoxelWeaponConfigs.get().getBladeStats(this.currentBlade);
        VoxelWeaponConfigs.ComponentStats handleStats = VoxelWeaponConfigs.get().getHandleStats(this.currentHandle);

        if (bladeStats == null || handleStats == null) {
            LoggerUtil.getLogger().warning("[WeaponHandlerComponent] Unknown blade/handle: " + this.currentBlade + "/" + this.currentHandle);
            return null;
        }

        return WeaponType.resolve(bladeStats.getWeight(), handleStats.getWeight());
    }

    public String getCurrentBlade() {
        return currentBlade;
    }

    public void setCurrentBlade(String currentBlade) {
        this.currentBlade = currentBlade;
        this.composedWeaponType = null;
    }

    public String getCurrentHandle() {
        return currentHandle;
    }

    public void setCurrentHandle(String currentHandle) {
        this.currentHandle = currentHandle;
        this.composedWeaponType = null;
    }

    public WeaponType getComposedWeaponType() {
        if (this.composedWeaponType == null) {
            this.composedWeaponType = recalculateWeight();
        }
        return this.composedWeaponType;
    }

    //Selecting skills using String will be soon deprecated please use the correct Data Type! (internally they are still saved as string)
    @Deprecated
    public String getSelectedSkill() {
        return selectedSkill;
    }
    @Deprecated
    public void setSelectedSkill(String selectedSkill) {
        this.selectedSkill = selectedSkill;
    }
    @Deprecated
    public String getSelectedUltimate() {
        return selectedUltimate;
    }
    @Deprecated
    public void setSelectedUltimate(String selectedUltimate) {
        this.selectedUltimate = selectedUltimate;
    }

    public VoxelSkillConfigs.SkillDefinition getSelectedSkillDefinition() {
        return VoxelSkillsRegistry.staticGetSkill(this.selectedSkill);
    }
    public VoxelSkillConfigs.SkillDefinition getSelectedUltimateDefinition() {
        return VoxelSkillsRegistry.staticGetSkill(this.selectedUltimate);
    }

    public void setSelectedSkill(VoxelSkillConfigs.SkillDefinition selectedSkill) {
        if (selectedSkill == null) throw new IllegalArgumentException("Selected skill cannot be null!");
        if (selectedSkill.getWeaponType() != this.getComposedWeaponType()) throw new IllegalArgumentException("Selected skill weapon type does not match the current weapon type!");

        this.selectedSkill = selectedSkill.getName();
    }
    public void setSelectedUltimate(VoxelSkillConfigs.SkillDefinition selectedUltimate) {
        if (selectedUltimate == null) throw new IllegalArgumentException("Selected ultimate cannot be null!");
        if (selectedUltimate.getWeaponType() != this.getComposedWeaponType()) throw new IllegalArgumentException("Selected ultimate weapon type does not match the current weapon type!");

        this.selectedUltimate = selectedUltimate.getName();
    }

    public int getSwordPoints() {
        return swordPoints;
    }

    public void setSwordPoints(int swordPoints) {
        this.swordPoints = swordPoints;
    }

    public int getSwordInternalLevel() {
        return swordInternalLevel;
    }

    public void setSwordInternalLevel(int swordInternalLevel) {
        this.swordInternalLevel = swordInternalLevel;
    }

    public int getSwordXP() {
        return swordXP;
    }

    public void setSwordXP(int swordXP) {
        this.swordXP = swordXP;
    }

    //Helper methods

    public void incrementLevel() { this.swordInternalLevel++; }
    public void addSP(int amount) { this.swordPoints += amount; }

    public VoxelWeaponConfigs.WeaponStatSnapshot getStatSnapshot() {
        return VoxelWeaponConfigs.WeaponStatSnapshot.of(this.currentBlade, this.currentHandle);
    }
}
