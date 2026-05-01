package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Components.PlayerComponents.PlayerWeaponProgressComponent;
import dev.VoxelTales.Components.PlayerComponents.VoxelPlayerComponent;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.UI.HUD.WeaponHUD;
import dev.VoxelTales.UI.Pages.DialoguePage;
import dev.VoxelTales.UI.Pages.WeaponConfigurationPage;
import dev.VoxelTales.UI.Pages.WeaponForgerPage;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class VoxelCacheRegistry implements IVoxelRegistry {
    public record CacheDefinition<T>(Function<PlayerRef, T> factory) {}

    private static final Map<String, CacheDefinition<?>> definitions = new HashMap<>();
    private static final Map<String, Map<UUID, Object>> playerCaches = new ConcurrentHashMap<>();

    public static void init(VoxelTalesPlugin plugin) {
        register(CacheEnum.SLOT_CACHE, VoxelPlayerComponent::getWeaponSlot);
        register(CacheEnum.HUD_CACHE, WeaponHUD::new);
        register(CacheEnum.WeaponConfigurationPage, WeaponConfigurationPage::new);
        register(CacheEnum.WeaponForgerPage, WeaponForgerPage::new);
        register(CacheEnum.DialoguePage, DialoguePage::new);
        register(CacheEnum.VoxelPlayerWeaponProgressCache, PlayerWeaponProgressComponent::getProgressionData);

        LoggerUtil.getLogger().info("[VoxelCacheRegistry] Registered " + definitions.size() + " caches.");
    }

    public static <T> void register(CacheEnum key, Function<PlayerRef, T> factory) {
        definitions.put(key.getName(), new CacheDefinition<>(factory));
        playerCaches.put(key.getName(), new ConcurrentHashMap<>());
    }

    public static <T> void update(CacheEnum key, PlayerRef player, T data) {
        playerCaches.get(key.getName()).put(player.getUuid(), data);
    }

    public static <T> T get(CacheEnum key, PlayerRef player, Class<T> type) {
        var cache = playerCaches.get(key.getName());
        return type.cast(cache.computeIfAbsent(player.getUuid(), _ -> definitions.get(key.getName()).factory().apply(player)));
    }

    public static <T> T getSynced(CacheEnum key, UUID uuid, Class<T> type) {
        var cache = playerCaches.get(key.getName());
        if (cache == null) return null;
        Object data = cache.get(uuid);
        return data != null ? type.cast(data) : null;
    }

    public static void cleanup(UUID uuid) {
        playerCaches.values().forEach(map -> map.remove(uuid));
    }
}
