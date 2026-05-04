package dev.VoxelTales;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import dev.VoxelTales.Core.AVoxelRegistry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryManager {
    public static RegistryManager get() {
        return VoxelTalesPlugin.getRegistryManager();
    }

    private final Map<Class<? extends AVoxelRegistry>, AVoxelRegistry> registries = new ConcurrentHashMap<>();

    public void initRegistries(List<Class<? extends AVoxelRegistry>> classes, VoxelTalesPlugin plugin) {
        classes.forEach(registry -> this.initRegistry(registry, plugin));
    }

    public void initRegistry(Class<? extends AVoxelRegistry> clazz, VoxelTalesPlugin plugin) {
        AVoxelRegistry instance;

        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        }
        catch (Exception e) {
            LoggerUtil.logException("Failed to initialize registry: " + clazz.getName(), e);
            return;
        }

        this.registries.put(clazz, instance);
        instance.init(plugin);
    }

    @SuppressWarnings("unchecked")
    public <T extends AVoxelRegistry> T getRegistry(Class<T> clazz) throws IllegalStateException {
        AVoxelRegistry instance = registries.get(clazz);
        if (instance == null) {
            throw new IllegalStateException("[RegistryManager] Registry not found: " + clazz.getSimpleName());
        }

        return (T) instance;
    }

}
