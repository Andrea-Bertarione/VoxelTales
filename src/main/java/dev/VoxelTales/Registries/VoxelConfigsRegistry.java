package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.util.Config;
import dev.VoxelTales.Configs.EntityXPConfigs;
import dev.VoxelTales.Configs.VoxelSkillConfigs;
import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.Configs.VoxelWeaponConfigs;
import dev.VoxelTales.Core.AVoxelRegistry;
import dev.VoxelTales.Registries.RegistryEnums.ConfigEnum;
import dev.VoxelTales.VoxelTalesPlugin;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;

public class VoxelConfigsRegistry extends AVoxelRegistry {
    private static final String CONFIG_PATH = "Server/Config/";
    private final ConcurrentHashMap<Class<?>, Config<?>> configRegistry = new ConcurrentHashMap<>();

    private static VoxelConfigsRegistry INSTANCE;

    public void init(VoxelTalesPlugin plugin) {
        INSTANCE = this;

        registerClass(plugin, ConfigEnum.VOXELTALES_GENERAL_CONFIGS, VoxelTalesConfigs.class, VoxelTalesConfigs.CODEC, false);
        registerClass(plugin, ConfigEnum.ENTITY_XP_CONFIGS, EntityXPConfigs.class, EntityXPConfigs.CODEC, false);
        registerClass(plugin, ConfigEnum.WEAPON_LOOKUP_CONFIGS, VoxelWeaponConfigs.class, VoxelWeaponConfigs.CODEC, true);
        registerClass(plugin, ConfigEnum.SKILL_LOOKUP_CONFIGS, VoxelSkillConfigs.class, VoxelSkillConfigs.CODEC, true);

        LoggerUtil.getLogger().info("[VoxelConfigsRegistry] Registered " + super.getRegistryCount() + " configs.");
    }

    private void registerClass(VoxelTalesPlugin plugin, ConfigEnum configEnum, Class<?> clazz, BuilderCodec<?> codec, boolean copyConfig) {
        if (copyConfig) {
            copyConfigIfMissing(plugin, configEnum);
        }

        configRegistry.put(clazz, plugin.registerConfig(configEnum.getName(), codec));
        super.incrementRegistryCount();
    }

    public void saveAll() {
        for (Config<?> config : configRegistry.values()) {
            config.save();
        }
    }

    public void save(Class<?> clazz) {
        Config<?> config = configRegistry.get(clazz);
        if (config != null) {
            config.save();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(Class<T> clazz) {
        return (T) configRegistry.get(clazz).get();
    }

    private void copyConfigIfMissing(VoxelTalesPlugin plugin, ConfigEnum configName) {
        String fileName = configName.getName() + ".json";
        Path targetPath = plugin.getDataDirectory().resolve(fileName);

        if (Files.exists(targetPath)) {
            return;
        }

        try {
            Files.createDirectories(targetPath.getParent());

            try (InputStream stream = plugin.getClass().getClassLoader().getResourceAsStream(CONFIG_PATH + fileName)) {
                if (stream == null) {
                    LoggerUtil.getLogger().warning("[VoxelTales] config not found at: " + CONFIG_PATH + fileName);
                    return;
                }

                Files.copy(stream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                LoggerUtil.getLogger().info("[VoxelTales] Copied config to: " + targetPath);
            }
        } catch (Throwable t) {
            LoggerUtil.getLogger().warning("[VoxelTales] Failed to copy config: " + t.getMessage());
        }
    }

    //Static direct access methods
    public static <T> T staticGet(Class<T> clazz) {
        return INSTANCE.getConfig(clazz);
    }
    public static void staticSave(Class<?> clazz) {
        INSTANCE.save(clazz);
    }
}
