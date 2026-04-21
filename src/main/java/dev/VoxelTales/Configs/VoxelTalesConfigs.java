package dev.VoxelTales.Configs;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class VoxelTalesConfigs {
    private float globalXpMultiplier = 1.0f;
    private int maxLevel = 50;
    private int spPerLevel = 5;

    // Math curve constants (XP = base * level ^ exponent)
    private int xpBaseValue = 100;
    private float xpExponent = 1.5f;

    private boolean serverSetUP = false;

    public VoxelTalesConfigs() {}

    // Getters for your LevelingController to use
    public float getGlobalXpMultiplier() { return globalXpMultiplier; }
    public int getMaxLevel() { return maxLevel; }
    public int getSpPerLevel() { return spPerLevel; }
    public int getXpBaseValue() { return xpBaseValue; }
    public float getXpExponent() { return xpExponent; }
    public boolean isServerSetUP() { return serverSetUP; }
    public void setServerSetUP(boolean serverSetUP) { this.serverSetUP = serverSetUP; }

    public static final BuilderCodec<VoxelTalesConfigs> CODEC = BuilderCodec.builder(VoxelTalesConfigs.class, VoxelTalesConfigs::new)
            .append(new KeyedCodec<>("GlobalXpMultiplier", Codec.FLOAT),
                    (c, v) -> c.globalXpMultiplier = v, (c) -> c.globalXpMultiplier).add()

            .append(new KeyedCodec<>("MaxLevel", Codec.INTEGER),
                    (c, v) -> c.maxLevel = v, (c) -> c.maxLevel).add()

            .append(new KeyedCodec<>("SpPerLevel", Codec.INTEGER),
                    (c, v) -> c.spPerLevel = v, (c) -> c.spPerLevel).add()

            .append(new KeyedCodec<>("XpBaseValue", Codec.INTEGER),
                    (c, v) -> c.xpBaseValue = v, (c) -> c.xpBaseValue).add()

            .append(new KeyedCodec<>("XpExponent", Codec.FLOAT),
                    (c, v) -> c.xpExponent = v, (c) -> c.xpExponent).add()
            .append(new KeyedCodec<>("ServerSetUP", Codec.BOOLEAN),
                    (c, v) -> c.serverSetUP = v, (c) -> c.serverSetUP).add()
            .build();
}
