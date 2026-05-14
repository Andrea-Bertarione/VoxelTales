package dev.VoxelTales.Configs;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import dev.VoxelTales.Assets.Gameplay.WeaponType;
import dev.VoxelTales.Registries.VoxelConfigsRegistry;

import java.util.*;

public class VoxelSkillConfigs {
    public static VoxelSkillConfigs get() {
        return VoxelConfigsRegistry.staticGet(VoxelSkillConfigs.class);
    }
    private Map<String, SkillDefinition> skillDefinitions;

    public static class SkillDefinition {
        private String name;
        private String displayName;
        private WeaponType weaponType;
        private boolean isUltimate;

        public static final BuilderCodec<SkillDefinition> CODEC = BuilderCodec.builder(SkillDefinition.class, SkillDefinition::new)
                .append(new KeyedCodec<>("Name", Codec.STRING), SkillDefinition::setName, SkillDefinition::getName).add()
                .append(new KeyedCodec<>("DisplayName", Codec.STRING), SkillDefinition::setDisplayName, SkillDefinition::getDisplayName).add()
                .append(new KeyedCodec<>("WeaponType", Codec.STRING), (s, v) -> s.setWeaponType(WeaponType.valueOf(v.toUpperCase())), (s) -> s.weaponType.name.toUpperCase()).add()
                .append(new KeyedCodec<>("IsUltimate", Codec.BOOLEAN), (s, v) -> s.isUltimate = v, (s) -> s.isUltimate).add()
                .build();

        public SkillDefinition() {
            this.name = "Root_Thrust_Simple";
            this.displayName = "Thrust";
            this.weaponType = WeaponType.SWORD;
            this.isUltimate = false;
        }

        public SkillDefinition(String name, String displayName, WeaponType weaponType, boolean isUltimate) {
            this.name = name;
            this.displayName = displayName;
            this.weaponType = weaponType;
            this.isUltimate = isUltimate    ;
        }

        public String getName() {
            return name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public WeaponType getWeaponType() {
            return this.weaponType;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setWeaponType(WeaponType weaponType) {
            this.weaponType = weaponType;
        }

        public boolean isUltimate() {
            return isUltimate;
        }

        public void setUltimate(boolean isUltimate) {
            this.isUltimate = isUltimate;
        }
    }

    public static final BuilderCodec<VoxelSkillConfigs> CODEC = BuilderCodec.builder(VoxelSkillConfigs.class, VoxelSkillConfigs::new)
            .append(new KeyedCodec<>("SkillDefinitions", new MapCodec<>(SkillDefinition.CODEC, LinkedHashMap::new)),
                    (configs, value) -> configs.skillDefinitions = new LinkedHashMap<>(value),
                    (configs) -> Collections.unmodifiableMap(configs.skillDefinitions)
            )
            .add()
            .build();

    public VoxelSkillConfigs() {
        this.skillDefinitions = new LinkedHashMap<>();
    }

    public void addSkillDefinition(SkillDefinition skillDefinition) {
        this.skillDefinitions.put(skillDefinition.name, skillDefinition);
    }

    public SkillDefinition getSkillDefinition(String skillName) {
        return this.skillDefinitions.get(skillName);
    }

    public Map<String, SkillDefinition> getSkillDefinitions() {
        return Collections.unmodifiableMap(this.skillDefinitions);
    }
}