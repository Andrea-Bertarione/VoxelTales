package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import dev.VoxelTales.Assets.Gameplay.WeaponType;
import dev.VoxelTales.Configs.VoxelSkillConfigs;
import dev.VoxelTales.Configs.VoxelWeaponConfigs;
import dev.VoxelTales.Core.AVoxelRegistry;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VoxelSkillsRegistry extends AVoxelRegistry {
    private final ConcurrentHashMap<String, VoxelSkillConfigs.SkillDefinition> skillRegistry = new ConcurrentHashMap<>();
    private static VoxelSkillsRegistry INSTANCE;

    public void init(VoxelTalesPlugin plugin) {
        INSTANCE = this;

        VoxelSkillConfigs.get().getSkillDefinitions().values().forEach(this::register);

        LoggerUtil.getLogger().info("[VoxelSkillsRegistry] Initialized with " + super.getRegistryCount() + " skills.");
    }

    public void register(VoxelSkillConfigs.SkillDefinition skill) {
        skillRegistry.put(skill.getName(), skill);
        super.incrementRegistryCount();
    }

    public VoxelSkillConfigs.SkillDefinition getSkill(String skillName) {
        return skillRegistry.get(skillName);
    }

    //Static direct access methods
    public static VoxelSkillConfigs.SkillDefinition staticGetSkill(String skillName) { return INSTANCE.getSkill(skillName); }
    public static List<VoxelSkillConfigs.SkillDefinition> staticGetSkills() { return List.copyOf(INSTANCE.skillRegistry.values()); }
}