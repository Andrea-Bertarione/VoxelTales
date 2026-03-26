package dev.VoxelTales.Components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VoxelPlayerComponent implements Component<EntityStore> {
    private short weaponSlot;
    public Map<String, Float> weaponStatModifiers;
    public boolean areStatsUpdated = false;

    public static final BuilderCodec<VoxelPlayerComponent> CODEC = BuilderCodec.builder(VoxelPlayerComponent.class, VoxelPlayerComponent::new)
            .append(new KeyedCodec<>("WeaponSlot", Codec.SHORT),
                    (config, value) -> config.weaponSlot = value,
                    (config) -> config.weaponSlot)
            .add()
            .append(
                    new KeyedCodec<>("WeaponStatModifiers", new MapCodec<>(Codec.FLOAT, HashMap::new)),
                    (config, value) -> config.weaponStatModifiers = new HashMap<>(value),
                    (config) -> config.weaponStatModifiers
            )
            .add()
            .build();

    public VoxelPlayerComponent() {
        this.weaponSlot = 0;
    }

    public VoxelPlayerComponent(VoxelPlayerComponent clone) {
        this.weaponSlot = clone.weaponSlot;
        this.weaponStatModifiers = clone.weaponStatModifiers;
        this.areStatsUpdated = clone.areStatsUpdated;
    }

    public short getWeaponSlot() {
        return weaponSlot;
    }

    public void setWeaponSlot(short weaponSlot) {
        this.weaponSlot = weaponSlot;
        this.weaponStatModifiers = new HashMap<>();
        this.areStatsUpdated = false;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() { return this; }
}
