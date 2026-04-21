package dev.VoxelTales.Components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Configs.VoxelWeaponConfigs;
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

    public static final BuilderCodec<WeaponHandlerComponent> CODEC =
            BuilderCodec.builder(WeaponHandlerComponent.class, WeaponHandlerComponent::new)
                    .append(new KeyedCodec<>("CurrentBlade", Codec.STRING),
                            (data, value) -> data.currentBlade = value,
                            data -> data.currentBlade) // getter
                    .addValidator(Validators.nonNull())
                    .add()
                    .append(new KeyedCodec<>("CurrentHandle", Codec.STRING),
                            (data, value) -> data.currentHandle = value,
                            data -> data.currentHandle) // getter
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
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() { return new WeaponHandlerComponent(this); }

    public String getCurrentBlade() {
        return currentBlade;
    }

    public void setCurrentBlade(String currentBlade) {
        this.currentBlade = currentBlade;
    }

    public String getCurrentHandle() {
        return currentHandle;
    }

    public void setCurrentHandle(String currentHandle) {
        this.currentHandle = currentHandle;
    }

    public String getSelectedSkill() {
        return selectedSkill;
    }

    public void setSelectedSkill(String selectedSkill) {
        this.selectedSkill = selectedSkill;
    }

    public String getSelectedUltimate() {
        return selectedUltimate;
    }

    public void setSelectedUltimate(String selectedUltimate) {
        this.selectedUltimate = selectedUltimate;
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

    public Map<String, Float> getCalculatedDamageMap() {

        VoxelWeaponConfigs.ComponentStats bladeStats = VoxelWeaponConfigsHelper.getBladeStats(this.currentBlade);
        VoxelWeaponConfigs.ComponentStats handleStats = VoxelWeaponConfigsHelper.getHandleStats(this.currentHandle);

        Map<String, Float> resultMap = new HashMap<>(bladeStats.getBaseDamage());

        handleStats.getBaseDamage().forEach((key, value) ->
                resultMap.merge(key, value, (val1, val2) -> (val1 + val2) / 2)
        );

        return resultMap;
    }

    public Map<String, Float> getCalculatedScalingMap() {

        VoxelWeaponConfigs.ComponentStats bladeStats = VoxelWeaponConfigsHelper.getBladeStats(this.currentBlade);
        VoxelWeaponConfigs.ComponentStats handleStats = VoxelWeaponConfigsHelper.getHandleStats(this.currentHandle);

        Map<String, Float> resultMap = new HashMap<>(bladeStats.getDamageScaling());

        handleStats.getDamageScaling().forEach((key, value) ->
                resultMap.merge(key, value, (val1, val2) -> (val1 + val2) / 2)
        );

        return resultMap;
    }

    public Map<String, Float> getCalculatedPassivesMap() {

        VoxelWeaponConfigs.ComponentStats bladeStats = VoxelWeaponConfigsHelper.getBladeStats(this.currentBlade);
        VoxelWeaponConfigs.ComponentStats handleStats = VoxelWeaponConfigsHelper.getHandleStats(this.currentHandle);

        Map<String, Float> resultMap = new HashMap<>(bladeStats.getPassives());

        handleStats.getPassives().forEach((key, value) ->
                resultMap.merge(key, value, Float::sum)
        );

        return resultMap;
    }

    public float getCalculatedAttackSpeed() {
        float bladeSpeed = VoxelWeaponConfigsHelper.getBladeStats(this.currentBlade).getAttackSpeed();
        float handleSpeed = VoxelWeaponConfigsHelper.getHandleStats(this.currentHandle).getAttackSpeed();

        return bladeSpeed * handleSpeed;
    }
}
