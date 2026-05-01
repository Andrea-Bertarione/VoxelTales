package dev.VoxelTales.Registries;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Components.CombatComponents.CombatTrackerComponent;
import dev.VoxelTales.Components.PlayerComponents.DialogueStateComponent;
import dev.VoxelTales.Components.PlayerComponents.PlayerWeaponProgressComponent;
import dev.VoxelTales.Components.PlayerComponents.VoxelPlayerComponent;
import dev.VoxelTales.Components.PlayerComponents.WeaponHandlerComponent;
import dev.VoxelTales.Interfaces.IVoxelRegistry;
import dev.VoxelTales.Registries.RegistryEnums.ComponentEnum;
import dev.VoxelTales.VoxelTalesPlugin;

import java.util.concurrent.ConcurrentHashMap;

public class VoxelComponentsRegistry implements IVoxelRegistry {
    private static final ConcurrentHashMap<Class<?>, ComponentType<EntityStore, ?>> componentRegistry = new ConcurrentHashMap<>();

    public static void init(VoxelTalesPlugin plugin) {
        ComponentRegistryProxy<EntityStore> entityStoreRegistry = plugin.getEntityStoreRegistry();

        registerClass(entityStoreRegistry, ComponentEnum.VOXEL_PLAYER_COMPONENT, VoxelPlayerComponent.class, VoxelPlayerComponent.CODEC);
        registerClass(entityStoreRegistry, ComponentEnum.WEAPON_HANDLER_COMPONENT, WeaponHandlerComponent.class, WeaponHandlerComponent.CODEC);
        registerClass(entityStoreRegistry, ComponentEnum.COMBAT_TRACKER_COMPONENT, CombatTrackerComponent.class, CombatTrackerComponent.CODEC);
        registerClass(entityStoreRegistry, ComponentEnum.PLAYER_WEAPON_PROGRESS_COMPONENT, PlayerWeaponProgressComponent.class, PlayerWeaponProgressComponent.CODEC);
        registerClass(entityStoreRegistry, ComponentEnum.DIALOGUE_STATE_COMPONENT, DialogueStateComponent.class, DialogueStateComponent.CODEC);

        LoggerUtil.getLogger().info("[VoxelComponentRegistry] Registered " + componentRegistry.size() + " components.");
    }

    @SuppressWarnings("unchecked")
    public static <T extends Component<EntityStore>> ComponentType<EntityStore, T> getComponentType(Class<T> clazz) {
        return (ComponentType<EntityStore, T>) componentRegistry.get(clazz);
    }

    private static <T extends Component<EntityStore>> void registerClass(
            ComponentRegistryProxy<EntityStore> entityStoreRegistry,
            ComponentEnum name,
            Class<T> clazz,
            BuilderCodec<T> codec) {

        ComponentType<EntityStore, T> type = entityStoreRegistry.registerComponent(
                clazz,
                name.getName(),
                codec
        );

        componentRegistry.put(clazz, type);
    }
}
