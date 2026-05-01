package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.util.Config;
import dev.VoxelTales.Configs.EntityXPConfigs;
import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.Configs.VoxelWeaponConfigs;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.Registries.RegistryEnums.ConfigEnum;
import dev.VoxelTales.VoxelTalesPlugin;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;

public class VoxelConfigsRegistry implements IVoxelRegistry {
    private static final String CONFIG_PATH = "Server/Config/";
    private static final ConcurrentHashMap<Class<?>, Config<?>> configRegistry = new ConcurrentHashMap<>();

    public static void init(VoxelTalesPlugin plugin) {
        registerClass(plugin, ConfigEnum.VOXELTALES_GENERAL_CONFIGS, VoxelTalesConfigs.class, VoxelTalesConfigs.CODEC);
        registerClass(plugin, ConfigEnum.ENTITY_XP_CONFIGS, EntityXPConfigs.class, EntityXPConfigs.CODEC);
        registerClass(plugin, ConfigEnum.WEAPON_LOOKUP_CONFIGS, VoxelWeaponConfigs.class, VoxelWeaponConfigs.CODEC, true);

        LoggerUtil.getLogger().info("[VoxelConfigsRegistry] Registered " + configRegistry.size() + " configs.");
    }

    private static void registerClass(VoxelTalesPlugin plugin, ConfigEnum configEnum, Class<?> clazz, BuilderCodec<?> codec) {
        configRegistry.put(clazz, plugin.registerConfig(configEnum.getName(), codec));
    }

    private static void registerClass(VoxelTalesPlugin plugin, ConfigEnum configEnum, Class<?> clazz, BuilderCodec<?> codec, boolean copyConfig) {
        registerClass(plugin, configEnum, clazz, codec);

        if (copyConfig) {
            copyConfigIfMissing(plugin, configEnum);
        }
    }

    public static void saveAll() {
        for (Config<?> config : configRegistry.values()) {
            config.save();
        }
    }

    public static void save(Class<?> clazz) {
        Config<?> config = configRegistry.get(clazz);
        if (config != null) {
            config.save();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getConfig(Class<T> clazz) {
        return (T) configRegistry.get(clazz).get();
    }

    private static void copyConfigIfMissing(VoxelTalesPlugin plugin, ConfigEnum configName) {
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
}
