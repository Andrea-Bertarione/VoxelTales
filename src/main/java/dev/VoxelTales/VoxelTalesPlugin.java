package dev.VoxelTales;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import dev.VoxelTales.Assets.Commands.*;
import dev.VoxelTales.Assets.Interactions.OpenForgeUIInteraction;
import dev.VoxelTales.Components.CombatComponents.CombatTrackerComponent;
import dev.VoxelTales.Components.PlayerWeaponProgressComponent;
import dev.VoxelTales.Components.VoxelPlayerComponent;
import dev.VoxelTales.Components.WeaponHandlerComponent;
import dev.VoxelTales.Configs.EntityXPConfigs;
import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.Configs.VoxelWeaponConfigs;
import dev.VoxelTales.Events.VoxelPlayerDisconnectEvent;
import dev.VoxelTales.Events.VoxelPlayerReadyEvent;
import dev.VoxelTales.Assets.Interactions.RouterSignatureInteraction;
import dev.VoxelTales.Assets.Interactions.RouterSkillInteraction;
import dev.VoxelTales.Assets.Interactions.VoxelDamageEntityInteraction;
import dev.VoxelTales.Events.VoxelAddWorldEvent;
import dev.VoxelTales.PacketListeners.WeaponActivationListener;
import dev.VoxelTales.PacketListeners.WeaponMoveListener;
import dev.VoxelTales.Registries.MetaData.VoxelDamageMetadata;
import dev.VoxelTales.Registries.VoxelCacheRegistry;
import dev.VoxelTales.Registries.VoxelDamageKindRegistry;
import dev.VoxelTales.Systems.DamageDealingSystem;
import dev.VoxelTales.Systems.DamageTrackingSystem;
import dev.VoxelTales.Systems.MobDeathXPSystem;
import dev.VoxelTales.UI.Pages.WeaponConfigurationPage;
import dev.VoxelTales.UI.Pages.WeaponForgerPage;
import dev.VoxelTales.UI.HUD.WeaponHUD;
import dev.VoxelTales.Utils.Reflections.VoxelAssetReflection;
import dev.VoxelTales.Utils.Reflections.VoxelDamageUIReflection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VoxelTalesPlugin extends JavaPlugin {
    private static VoxelTalesPlugin instance;

    //Configs
    private final Config<VoxelTalesConfigs> voxelTalesConfigs = this.withConfig("VoxelTales_GeneralConfigs", VoxelTalesConfigs.CODEC);
    private final Config<EntityXPConfigs> entityXpConfigs = this.withConfig("VoxelTales_EntityXPConfigs", EntityXPConfigs.CODEC);
    private final Config<VoxelWeaponConfigs> weaponLookupConfig = this.withConfig("VoxelTales_WeaponLookupConfigs", VoxelWeaponConfigs.CODEC);

    //Caches
    private final Map<UUID, Short> slotCache = new ConcurrentHashMap<>();
    private final Map<UUID, WeaponHUD> hudCache = new ConcurrentHashMap<>();

    //Components
    private ComponentType<EntityStore, WeaponHandlerComponent> weaponHandlerComponent;
    private ComponentType<EntityStore, VoxelPlayerComponent> voxelPlayerComponent;
    private ComponentType<EntityStore, CombatTrackerComponent> combatTrackerComponent;
    private ComponentType<EntityStore, PlayerWeaponProgressComponent> playerWeaponProgressComponent;

    public VoxelTalesPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        this.voxelTalesConfigs.save();
        this.entityXpConfigs.save();
        this.weaponLookupConfig.save();

        //Register custom Metadata
        VoxelDamageMetadata.registerDamage(Damage.META_REGISTRY);
        VoxelDamageMetadata.registerInteraction(Interaction.META_REGISTRY);

        //Register Damage Kinds
        VoxelDamageKindRegistry.registerDamageKinds();
        VoxelDamageUIReflection.disableBuiltinCombatText(this);

        //Register components
        this.weaponHandlerComponent = this.getEntityStoreRegistry().registerComponent(
                WeaponHandlerComponent.class,
                "VoxelTales:WeaponHandlerComponent",
                WeaponHandlerComponent.CODEC
        );

        this.voxelPlayerComponent = this.getEntityStoreRegistry().registerComponent(
                VoxelPlayerComponent.class,
                "VoxelTales:VoxelPlayerComponent",
                VoxelPlayerComponent.CODEC
        );

        this.combatTrackerComponent = this.getEntityStoreRegistry().registerComponent(
                CombatTrackerComponent.class,
                "VoxelTales:CombatTrackerComponent",
                CombatTrackerComponent.CODEC
        );

        this.playerWeaponProgressComponent = this.getEntityStoreRegistry().registerComponent(
                PlayerWeaponProgressComponent.class,
                "VoxelTales:PlayerWeaponProgressComponent",
                PlayerWeaponProgressComponent.CODEC
        );

        //Register events
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, VoxelPlayerReadyEvent::onPlayerReady);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, VoxelPlayerDisconnectEvent::onPlayerDisconnect);
        this.getEventRegistry().registerGlobal(AddWorldEvent.class, VoxelAddWorldEvent::onAddWorld);

        //Register systems
        this.getEntityStoreRegistry().registerSystem(new DamageTrackingSystem());
        this.getEntityStoreRegistry().registerSystem(new MobDeathXPSystem());
        this.getEntityStoreRegistry().registerSystem(new DamageDealingSystem());

        //Register packet listeners
        PacketAdapters.registerInbound(WeaponMoveListener.weaponFilter());
        PacketAdapters.registerInbound(new WeaponActivationListener());

        //Register Commands
        this.getCommandRegistry().registerCommand(new ChangeSlotCommand());
        this.getCommandRegistry().registerCommand(new VoxelAdminCommandCollection());

        //Register Interactions
        this.getCodecRegistry(Interaction.CODEC).register("RouterSignatureInteraction", RouterSignatureInteraction.class, RouterSignatureInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("RouterSkillInteraction", RouterSkillInteraction.class, RouterSkillInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("DamageEntityInteraction", VoxelDamageEntityInteraction.class, VoxelDamageEntityInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("OpenForgeUIInteraction", OpenForgeUIInteraction.class, OpenForgeUIInteraction.CODEC);

        //Register Caches
        VoxelCacheRegistry.register("WeaponConfigurationPage", WeaponConfigurationPage::new);
        VoxelCacheRegistry.register("WeaponForgerPage", WeaponForgerPage::new);
        VoxelCacheRegistry.register("VoxelPlayerWeaponProgressCache", playerRef -> {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null || !ref.isValid()) {
                Map<String, Set<String>> unlocks = new HashMap<>();
                unlocks.put("blades", Collections.emptySet());
                unlocks.put("handles", Collections.emptySet());

                return unlocks;
            };

            Store<EntityStore> store = ref.getStore();
            PlayerWeaponProgressComponent component =
                    store.ensureAndGetComponent(ref, VoxelTalesPlugin.get().getPlayerWeaponProgressComponent());

            Map<String, Set<String>> unlocks = new HashMap<>();
            unlocks.put("blades", component.getUnlockedBlades());
            unlocks.put("handles", component.getUnlockedHandles());

            return unlocks;
        });
    }

    @Override
    protected void start() {
        //Run asset patching AFTER everything loaded
        VoxelAssetReflection.patch();
    }

    public static VoxelTalesPlugin get() {
        return instance;
    }

    public Config<EntityXPConfigs> getEntityXpConfigs() {
        return entityXpConfigs;
    }

    public Config<VoxelTalesConfigs> getVoxelTalesConfigs() {
        return voxelTalesConfigs;
    }

    public Config<VoxelWeaponConfigs> getWeaponLookupConfig() { return weaponLookupConfig; }

    public ComponentType<EntityStore, WeaponHandlerComponent> getWeaponHandlerComponent() {return this.weaponHandlerComponent;}
    public ComponentType<EntityStore, VoxelPlayerComponent> getVoxelPlayerComponent() { return this.voxelPlayerComponent; }
    public ComponentType<EntityStore, CombatTrackerComponent> getCombatTrackerComponent() { return this.combatTrackerComponent; }
    public ComponentType<EntityStore, PlayerWeaponProgressComponent> getPlayerWeaponProgressComponent() { return this.playerWeaponProgressComponent; }

    public Map<UUID, Short> getSlotCache() {
        return slotCache;
    }
    public Map<UUID, WeaponHUD> getHudCache() { return hudCache; }

    // Helper to get the slot safely, defaulting to 0
    public short getLockedSlot(UUID playerUuid) {
        return slotCache.getOrDefault(playerUuid, (short) 0);
    }
    public void removeLockedSlot(UUID playerUuid) {
        slotCache.remove(playerUuid);
    }

    // Helper to get the hud safely;
    public WeaponHUD getWeaponHud(PlayerRef playerRef) {
        return hudCache.computeIfAbsent(playerRef.getUuid(), _ -> new WeaponHUD(playerRef));
    }
    public void removeWeaponHud(UUID playerUuid) { hudCache.remove(playerUuid); }



}