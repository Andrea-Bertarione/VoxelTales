package dev.VoxelTales;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import dev.VoxelTales.Commands.*;
import dev.VoxelTales.Components.CombatTrackerComponent;
import dev.VoxelTales.Components.VoxelPlayerComponent;
import dev.VoxelTales.Components.WeaponHandlerComponent;
import dev.VoxelTales.Configs.EntityXPConfigs;
import dev.VoxelTales.Configs.VoxelTalesConfigs;
import dev.VoxelTales.Configs.VoxelWeaponLookup;
import dev.VoxelTales.Events.PlayerDisconnect;
import dev.VoxelTales.Events.PlayerReady;
import dev.VoxelTales.Interactions.RouterSignatureInteraction;
import dev.VoxelTales.Interactions.RouterSkillInteraction;
import dev.VoxelTales.Interactions.VoxelDamageEntityInteraction;
import dev.VoxelTales.PacketListeners.WeaponActivationListener;
import dev.VoxelTales.PacketListeners.WeaponMoveListener;
import dev.VoxelTales.Systems.DamageDealingSystem;
import dev.VoxelTales.Systems.DamageTrackingSystem;
import dev.VoxelTales.Systems.MobDeathXPSystem;
import dev.VoxelTales.UI.WeaponConfigurationPage;
import dev.VoxelTales.UI.WeaponForgerPage;
import dev.VoxelTales.UI.WeaponHUD;
import dev.VoxelTales.Utils.VoxelAssetPatcher;
import dev.VoxelTales.Utils.VoxelCacheRegistry;
import dev.VoxelTales.Utils.VoxelMetadata;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoxelTalesPlugin extends JavaPlugin {
    private static VoxelTalesPlugin instance;

    //Configs
    private final Config<VoxelTalesConfigs> voxelTalesConfigs = this.withConfig("VoxelTales_GeneralConfigs", VoxelTalesConfigs.CODEC);
    private final Config<EntityXPConfigs> entityXpConfigs = this.withConfig("VoxelTales_EntityXPConfigs", EntityXPConfigs.CODEC);
    private final Config<VoxelWeaponLookup> weaponLookupConfig = this.withConfig("VoxelTales_WeaponLookupConfigs", VoxelWeaponLookup.CODEC);

    //Caches
    private final Map<UUID, Short> slotCache = new ConcurrentHashMap<>();
    private final Map<UUID, WeaponHUD> hudCache = new ConcurrentHashMap<>();

    //Components
    private ComponentType<EntityStore, WeaponHandlerComponent> weaponHandlerComponent;
    private ComponentType<EntityStore, VoxelPlayerComponent> voxelPlayerComponent;
    private ComponentType<EntityStore, CombatTrackerComponent> combatTrackerComponent;

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
        VoxelMetadata.registerDamage(Damage.META_REGISTRY);
        VoxelMetadata.registerInteraction(Interaction.META_REGISTRY);

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

        //Register events
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, PlayerReady::onPlayerReady);
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, PlayerDisconnect::onPlayerDisconnect);

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

        //Register Caches
        VoxelCacheRegistry.register("WeaponConfigurationPage", WeaponConfigurationPage::new);
        VoxelCacheRegistry.register("WeaponForgerPage", WeaponForgerPage::new);
    }

    @Override
    protected void start() {
        //Run asset patching AFTER everything loaded
        VoxelAssetPatcher.patch();
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

    public Config<VoxelWeaponLookup> getWeaponLookupConfig() { return weaponLookupConfig; }

    public ComponentType<EntityStore, WeaponHandlerComponent> getWeaponHandlerComponent() {return this.weaponHandlerComponent;}
    public ComponentType<EntityStore, VoxelPlayerComponent> getVoxelPlayerComponent() { return this.voxelPlayerComponent; }
    public ComponentType<EntityStore, CombatTrackerComponent> getCombatTrackerComponent() { return this.combatTrackerComponent; }

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