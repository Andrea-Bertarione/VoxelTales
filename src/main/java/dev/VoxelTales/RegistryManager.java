package dev.VoxelTales;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.Core.AVoxelRegistry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryManager {
    public static RegistryManager get() {
        return VoxelTalesPlugin.getRegistryManager();
    }

    private final Map<Class<? extends AVoxelRegistry<?>>, AVoxelRegistry<?>> registries = new ConcurrentHashMap<>();

    public void initRegistries(List<Class<? extends AVoxelRegistry<? extends AVoxelRegistry<?>>>> classes, VoxelTalesPlugin plugin) {
        classes.forEach(registry -> {
            AVoxelRegistry<? extends AVoxelRegistry<?>> instance;

            try {
                instance = registry.getDeclaredConstructor().newInstance();
            }
            catch (Exception e) {
                LoggerUtil.getLogger().severe("Failed to initialize registry: " + registry.getName());
                return;
            }

            this.registries.put(registry, instance);
            instance.init(plugin);
        });
    }

    public void initRegistry(Class<? extends AVoxelRegistry<?>> clazz, VoxelTalesPlugin plugin) {
        AVoxelRegistry<?> instance;

        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        }
        catch (Exception e) {
            LoggerUtil.getLogger().severe("Failed to initialize registry: " + clazz.getName());
            return;
        }

        this.registries.put(clazz, instance);
        instance.init(plugin);
    }

    @SuppressWarnings("unchecked")
    public <T extends AVoxelRegistry<T>> T getRegistry(Class<T> clazz) {
        AVoxelRegistry<?> instance = registries.get(clazz);
        if (instance == null) {
            LoggerUtil.getLogger().severe("[RegistryManager] Registry not found: " + clazz.getSimpleName());
            return null;
        }

        return (T) instance;
    }

}
