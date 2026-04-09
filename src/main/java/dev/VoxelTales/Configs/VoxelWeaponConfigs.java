package dev.VoxelTales.Configs;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;

import java.util.HashMap;

public class VoxelWeaponConfigs {
    public static class ComponentStats {
        private HashMap<String, Float> damageScaling; // e.g., "Strength": 1.2, "Agility": 0.8
        private HashMap<String, Float> baseDamage;    // e.g., "Physical": 15.0, "Magical": 5.0
        private HashMap<String, Float> passives;      // e.g., "Lifesteal": 0.1, "CritChance": 0.05
        private Integer tier;
        private Float attackSpeed;
        private String itemIconId;

        public static final BuilderCodec<ComponentStats> CODEC = BuilderCodec.builder(ComponentStats.class, ComponentStats::new)
                .append(
                        new KeyedCodec<>("DamageScaling", new MapCodec<>(Codec.FLOAT, HashMap::new)),
                        (stats, value) -> stats.damageScaling = new HashMap<>(value),
                        (stats) -> stats.damageScaling
                )
                .add()
                .append(
                        new KeyedCodec<>("BaseDamage", new MapCodec<>(Codec.FLOAT, HashMap::new)),
                        (stats, value) -> stats.baseDamage = new HashMap<>(value),
                        (stats) -> stats.baseDamage
                )
                .add()
                .append(
                        new KeyedCodec<>("Passives", new MapCodec<>(Codec.FLOAT, HashMap::new)),
                        (stats, value) -> stats.passives = new HashMap<>(value),
                        (stats) -> stats.passives
                )
                .add()
                .append(
                        new KeyedCodec<>("Tier", Codec.INTEGER),
                        (stats, value) -> stats.tier = value,
                        (stats) -> stats.tier
                )
                .add()
                .append(
                        new KeyedCodec<>("AttackSpeed", Codec.FLOAT), // NEW CODEC ENTRY
                        (stats, value) -> stats.attackSpeed = value,
                        (stats) -> stats.attackSpeed
                )
                .add()
                .append(
                        new KeyedCodec<>("ItemIconId", Codec.STRING),
                        (stats, value) -> stats.itemIconId = value,
                        (stats) -> stats.itemIconId
                )
                .add()
                .build();

        public ComponentStats() {
            this.damageScaling = new HashMap<>();
            this.baseDamage = new HashMap<>();
            this.passives = new HashMap<>();
            this.tier = 1;
            this.attackSpeed = 1.0f;
            this.itemIconId = "Weapon_Sword_Steel";
        }

        public HashMap<String, Float> getDamageScaling() { return damageScaling; }
        public HashMap<String, Float> getBaseDamage() { return baseDamage; }
        public HashMap<String, Float> getPassives() { return passives; }
        public Integer getTier() { return tier; }
        public String getItemIconId() { return itemIconId; }

        public void setDamageScaling(HashMap<String, Float> damageScaling) {
            this.damageScaling = damageScaling;
        }

        public void setBaseDamage(HashMap<String, Float> baseDamage) {
            this.baseDamage = baseDamage;
        }

        public void setPassives(HashMap<String, Float> passives) {
            this.passives = passives;
        }

        public void setTier(Integer tier) {
            this.tier = tier;
        }

        public Float getAttackSpeed() { return attackSpeed; }

        public void setAttackSpeed(Float attackSpeed) { this.attackSpeed = attackSpeed; }

        public void setItemIconId(String itemIconId) {
            this.itemIconId = itemIconId;
        }

        public float getPassiveEffectiveness(String passiveName) {
            return passives.getOrDefault(passiveName, 0.0f);
        }
    }

    // --- MAIN CLASS: The Lookup Tables ---
    private HashMap<String, ComponentStats> blades;
    private HashMap<String, ComponentStats> handles;

    public static final BuilderCodec<VoxelWeaponConfigs> CODEC = BuilderCodec.builder(VoxelWeaponConfigs.class, VoxelWeaponConfigs::new)
            .append(
                    new KeyedCodec<>("Blades", new MapCodec<>(ComponentStats.CODEC, HashMap::new)),
                    (config, value) -> config.blades = new HashMap<>(value),
                    (config) -> config.blades
            )
            .add()
            .append(
                    new KeyedCodec<>("Handles", new MapCodec<>(ComponentStats.CODEC, HashMap::new)),
                    (config, value) -> config.handles = new HashMap<>(value),
                    (config) -> config.handles
            )
            .add()
            .build();

    public VoxelWeaponConfigs() {
        this.blades = new HashMap<>();
        this.handles = new HashMap<>();

        // Optional: Provide a fallback/default setup so it doesn't crash if the JSON is empty
        ComponentStats defaultStats = new ComponentStats();
        defaultStats.getBaseDamage().put("Physical", 1.0f);
        defaultStats.getDamageScaling().put("Strength", 1.0f);

        this.blades.put("default", defaultStats);
        this.handles.put("default", defaultStats);
    }

    public HashMap<String, ComponentStats> getBlades() { return blades; }
    public HashMap<String, ComponentStats> getHandles() { return handles; }

    // --- HELPER METHODS ---

    public ComponentStats getBladeStats(String bladeId) {
        return blades.getOrDefault(bladeId, blades.get("default"));
    }

    public ComponentStats getHandleStats(String handleId) {
        return handles.getOrDefault(handleId, handles.get("default"));
    }

    public void setBladeStats(String bladeId, ComponentStats stats) {
        blades.put(bladeId, stats);
    }

    public void setHandleStats(String handleId, ComponentStats stats) {
        handles.put(handleId, stats);
    }

    public void removeBladeStats(String bladeId) { blades.remove(bladeId); }
    public void removeHandleStats(String handleId) { handles.remove(handleId); }
}