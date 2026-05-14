package dev.VoxelTales.Components.PlayerComponents;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Registries.VoxelComponentsRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VoxelPlayerComponent implements Component<EntityStore> {
    private short weaponSlot;
    private Map<String, Float> weaponStatModifiers;
    private boolean areStatsUpdated = false;

    public static ComponentType<EntityStore, VoxelPlayerComponent> getComponentType() {
        return VoxelComponentsRegistry.staticGetComponentType(VoxelPlayerComponent.class);
    }

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
        this.areStatsUpdated = false;
        this.weaponStatModifiers = new HashMap<>();
    }

    public VoxelPlayerComponent(VoxelPlayerComponent clone) {
        this.weaponSlot = clone.weaponSlot;
        this.weaponStatModifiers = clone.weaponStatModifiers != null ? new HashMap<>(clone.weaponStatModifiers) : null;
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

    public Map<String, Float> getWeaponStatModifiers() {
        return Collections.unmodifiableMap(this.weaponStatModifiers);
    }

    public void setWeaponStatModifiers(Map<String, Float> weaponStatModifiers) {
        this.weaponStatModifiers = weaponStatModifiers;
    }

    public boolean isAreStatsUpdated() {
        return areStatsUpdated;
    }

    public void setAreStatsUpdated(boolean areStatsUpdated) {
        this.areStatsUpdated = areStatsUpdated;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() { return new VoxelPlayerComponent(this); }

    public static Short getWeaponSlot (PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) { return 0; }

        Store<EntityStore> store = ref.getStore();
        VoxelPlayerComponent component = store.ensureAndGetComponent(ref, VoxelPlayerComponent.getComponentType());

        return component.getWeaponSlot();
    }


}
