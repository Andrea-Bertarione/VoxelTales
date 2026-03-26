package dev.VoxelTales.Utils;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class VoxelCacheRegistry {
    public record CacheDefinition<T>(Function<PlayerRef, T> factory) {
    }
    // Stores the "Rules" for each cache type
    private static final Map<String, CacheDefinition<?>> definitions = new HashMap<>();

    // Stores the actual data: Map<CacheKey, Map<PlayerUUID, Object>>
    private static final Map<String, Map<UUID, Object>> playerCaches = new ConcurrentHashMap<>();

    public static <T> void register(String key, Function<PlayerRef, T> factory) {
        definitions.put(key, new CacheDefinition<>(factory));
        playerCaches.put(key, new ConcurrentHashMap<>());
    }

    public static <T> T get(String key, PlayerRef player, Class<T> type) {
        var cache = playerCaches.get(key);
        return type.cast(cache.computeIfAbsent(player.getUuid(), _ -> definitions.get(key).factory().apply(player)));
    }

    public static <T> T getSynced(String key, UUID uuid, Class<T> type) {
        var cache = playerCaches.get(key);
        if (cache == null) return null;
        Object data = cache.get(uuid);
        return data != null ? type.cast(data) : null;
    }

    public static void cleanup(UUID uuid) {
        playerCaches.values().forEach(map -> map.remove(uuid));
    }
}
