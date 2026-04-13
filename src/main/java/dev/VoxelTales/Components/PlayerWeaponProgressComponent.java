package dev.VoxelTales.Components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.set.SetCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class PlayerWeaponProgressComponent implements Component<EntityStore> {
    public Set<String> unlockedBlades;
    public Set<String> unlockedHandles;

    public static final BuilderCodec<PlayerWeaponProgressComponent> CODEC =
            BuilderCodec.builder(PlayerWeaponProgressComponent.class, PlayerWeaponProgressComponent::new)
                    .append(
                            new KeyedCodec<>("UnlockedBlades", new SetCodec<>(Codec.STRING, HashSet::new, false)),
                            (data, value) -> data.unlockedBlades = new HashSet<>(value),
                            data -> data.unlockedBlades
                    )
                    .add()
                    .append(
                            new KeyedCodec<>("UnlockedHandles", new SetCodec<>(Codec.STRING, HashSet::new, false)),
                            (data, value) -> data.unlockedHandles = new HashSet<>(value),
                            data -> data.unlockedHandles
                    )
                    .add()
                    .build();

    public PlayerWeaponProgressComponent() {
        this.unlockedBlades = new HashSet<>();
        this.unlockedHandles = new HashSet<>();

        this.unlockBlade("Old");
        this.unlockHandle("Old");
    }

    public PlayerWeaponProgressComponent(PlayerWeaponProgressComponent clone) {
        this.unlockedBlades = new HashSet<>(clone.unlockedBlades);
        this.unlockedHandles = new HashSet<>(clone.unlockedHandles);
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new PlayerWeaponProgressComponent(this);
    }

    public Set<String> getUnlockedBlades() {
        return unlockedBlades;
    }

    public void setUnlockedBlades(Set<String> unlockedBlades) {
        this.unlockedBlades = new HashSet<>(unlockedBlades);
    }

    public Set<String> getUnlockedHandles() {
        return unlockedHandles;
    }

    public void setUnlockedHandles(Set<String> unlockedHandles) {
        this.unlockedHandles = new HashSet<>(unlockedHandles);
    }

    // Helper methods

    public boolean isBladeUnlocked(String bladeId) {
        return this.unlockedBlades.contains(bladeId);
    }

    public boolean isHandleUnlocked(String handleId) {
        return this.unlockedHandles.contains(handleId);
    }

    public void unlockBlade(String bladeId) {
        this.unlockedBlades.add(bladeId);
    }

    public void unlockHandle(String handleId) {
        this.unlockedHandles.add(handleId);
    }

    public void lockBlade(String bladeId) {
        this.unlockedBlades.remove(bladeId);
    }

    public void lockHandle(String handleId) {
        this.unlockedHandles.remove(handleId);
    }
}
