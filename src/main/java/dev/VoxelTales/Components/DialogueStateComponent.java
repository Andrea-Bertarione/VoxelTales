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

public class DialogueStateComponent implements Component<EntityStore> {
    public Map<String, Boolean> dialogueFlags;

    public static final BuilderCodec<DialogueStateComponent> CODEC =
            BuilderCodec.builder(DialogueStateComponent.class, DialogueStateComponent::new)
                    .append(
                            new KeyedCodec<>("DialogueFlags", new MapCodec<>(Codec.BOOLEAN, HashMap::new)),
                            (cmp, value) -> cmp.dialogueFlags = new HashMap<>(value),
                            cmp -> cmp.dialogueFlags
                    )
                    .add()
                    .build();

    public DialogueStateComponent() {
        this.dialogueFlags = new HashMap<>();
    }

    public DialogueStateComponent(DialogueStateComponent clone) {
        this.dialogueFlags = new HashMap<>(clone.dialogueFlags);
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new DialogueStateComponent(this);
    }

    public boolean hasFlag(String flag) {
        return Boolean.TRUE.equals(this.dialogueFlags.get(flag));
    }

    public void setFlag(String flag, boolean value) {
        this.dialogueFlags.put(flag, value);
    }

    public void clearFlag(String flag) {
        this.dialogueFlags.remove(flag);
    }

    public void toggleFlag(String flag) {
        this.dialogueFlags.put(flag, !this.hasFlag(flag));
    }
}
