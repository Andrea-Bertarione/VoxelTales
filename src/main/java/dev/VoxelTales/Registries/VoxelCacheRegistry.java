package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.VoxelTales.Components.PlayerComponents.PlayerWeaponProgressComponent;
import dev.VoxelTales.Components.PlayerComponents.VoxelPlayerComponent;
import dev.VoxelTales.Core.AVoxelRegistry;
import dev.VoxelTales.Registries.RegistryEnums.CacheEnum;
import dev.VoxelTales.UI.HUD.WeaponHUD;
import dev.VoxelTales.UI.Pages.DialoguePage;
import dev.VoxelTales.UI.Pages.WeaponConfigurationPage;
import dev.VoxelTales.UI.Pages.WeaponForgerPage;
import dev.VoxelTales.Utils.VoxelMathHelper;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class VoxelCacheRegistry extends AVoxelRegistry<VoxelCacheRegistry> {
    public record CacheDefinition<T>(Function<PlayerRef, T> factory) {}

    private static VoxelCacheRegistry INSTANCE;

    private final Map<String, CacheDefinition<?>> definitions = new HashMap<>();
    private final Map<String, Map<UUID, Object>> playerCaches = new ConcurrentHashMap<>();

    public void init(VoxelTalesPlugin plugin) {
        INSTANCE = this;

        register(CacheEnum.SLOT_CACHE, VoxelPlayerComponent::getWeaponSlot);
        register(CacheEnum.HUD_CACHE, WeaponHUD::new);
        register(CacheEnum.WEAPON_CONFIGURATION_PAGE, WeaponConfigurationPage::new);
        register(CacheEnum.WEAPON_FORGER_PAGE, WeaponForgerPage::new);
        register(CacheEnum.DIALOGUE_PAGE, DialoguePage::new);
        register(CacheEnum.VOXEL_PLAYER_WEAPON_PROGRESS_CACHE, PlayerWeaponProgressComponent::getProgressionData);
        register(CacheEnum.VOXEL_PLAYER_DAMAGE_CACHE, VoxelMathHelper::getCachedDamageMap);
        register(CacheEnum.VOXEL_PLAYER_SCALING_CACHE, VoxelMathHelper::getCachedScalingMap);
        register(CacheEnum.VOXEL_PLAYER_ATKSPEED_CACHE, VoxelMathHelper::getCachedAttackSpeedMap);

        LoggerUtil.getLogger().info("[VoxelCacheRegistry] Registered " + super.getRegistryCount() + " caches.");
    }

    public <T> void register(CacheEnum key, Function<PlayerRef, T> factory) {
        definitions.put(key.getName(), new CacheDefinition<>(factory));
        playerCaches.put(key.getName(), new ConcurrentHashMap<>());

        super.incrementRegistryCount();
    }

    public <T> void update(CacheEnum key, PlayerRef player, T data) {
        playerCaches.get(key.getName()).put(player.getUuid(), data);
    }

    //Get and compute value from the cache
    public <T> T get(CacheEnum key, PlayerRef player, Class<T> type) {
        var cache = playerCaches.get(key.getName());
        return type.cast(cache.computeIfAbsent(player.getUuid(), _ -> definitions.get(key.getName()).factory().apply(player)));
    }

    //Get value from the cache and returns null if not found to avoid factories that needs to be in a specific thread to work
    public <T> T getSynced(CacheEnum key, UUID uuid, Class<T> type) {
        var cache = playerCaches.get(key.getName());
        if (cache == null) return null;
        Object data = cache.get(uuid);
        return data != null ? type.cast(data) : null;
    }

    public void invalidate(CacheEnum key, UUID uuid) {
        playerCaches.get(key.getName()).remove(uuid);
    }
    public void cleanup(UUID uuid) {
        playerCaches.values().forEach(map -> map.remove(uuid));
    }

    //Static direct access helper methods
    public static void staticUpdate(CacheEnum key, PlayerRef player, Object data) {
        INSTANCE.update(key, player, data);
    }

    public static <T> T staticGet(CacheEnum key, PlayerRef player, Class<T> type) {
        return INSTANCE.get(key, player, type);
    }

    public static <T> T staticGetSynced(CacheEnum key, UUID uuid, Class<T> type) {
        return INSTANCE.getSynced(key, uuid, type);
    }

    public static <T> void staticInvalidate(CacheEnum key, UUID uuid) {
        INSTANCE.invalidate(key, uuid);
    }

    public static void staticCleanup(UUID uuid) {
        INSTANCE.cleanup(uuid);
    }

}
