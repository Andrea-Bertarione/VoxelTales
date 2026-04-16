package dev.VoxelTales.Events;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.npc.role.RoleUtils;
import dev.VoxelTales.VoxelTalesPlugin;
import it.unimi.dsi.fastutil.Pair;

import java.util.Objects;

public class VoxelAddWorldEvent {
    private static final Vector3d SWORD_SAGE_POSITION_OFFSET = new Vector3d(11, 1, 2);
    private static final Vector3f SWORD_SAGE_ROTATION = new Vector3f(0.0F, 1.57F, 0.0F);
    private static final String SWORD_SAGE_MODEL_ID = "Slothian_Elder";
    private static final String SWORD_SAGE_EQUIPMENT_ID = "Weapon_Staff_Bo_Wood";
    private static final String SWORD_SAGE_ROLE_ID = "Sword_Sage";
    private static final String INTERACTION_ID = "OpenForgeRootInteraction";

    public static void onAddWorld(AddWorldEvent event) {
        LoggerUtil.getLogger().info("[Sword_Sage] StartWorldEvent triggered for world " + event.getWorld().getWorldConfig().getUuid());
        World world = event.getWorld();

        if (VoxelTalesPlugin.get().getVoxelTalesConfigs().get().isServerSetUP()) {
            LoggerUtil.getLogger().warning("[Sword_Sage] World already set up!");
            return;
        }

        world.execute(() -> {
            ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
            if (spawnProvider == null) {
                LoggerUtil.getLogger().warning("[Sword_Sage] World spawn provider is null!");
                return;
            }

            Vector3d basePosition = spawnProvider.getSpawnPoint(world, world.getWorldConfig().getUuid()).getPosition();
            Vector3d targetPosition = new Vector3d(basePosition).add(SWORD_SAGE_POSITION_OFFSET);
            LoggerUtil.getLogger().info("[Sword_Sage] Base position=" + basePosition + ", target position=" + targetPosition);

            Store<EntityStore> store = world.getEntityStore().getStore();

            Pair<Ref<EntityStore>, INonPlayerCharacter> result =
                    NPCPlugin.get().spawnNPC(store, SWORD_SAGE_ROLE_ID, null, new Vector3d(targetPosition), SWORD_SAGE_ROTATION);

            if (result == null) {
                LoggerUtil.getLogger().severe("[Sword_Sage] Failed to spawn Sword_Sage NPC at " + targetPosition);
                return;
            }

            Ref<EntityStore> npcRef = result.first();

            if (npcRef == null || !npcRef.isValid()) {
                LoggerUtil.getLogger().severe("[Sword_Sage] Sword_Sage spawn returned an invalid reference.");
                return;
            }

            TransformComponent transform = store.getComponent(npcRef, TransformComponent.getComponentType());
            LoggerUtil.getLogger().info("[Sword_Sage] Transform component present: " + (transform != null));
            if (transform != null) {
                transform.getPosition().assign(targetPosition);
                LoggerUtil.getLogger().info("[Sword_Sage] Position assigned to target position.");
            }

            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(SWORD_SAGE_MODEL_ID);
            LoggerUtil.getLogger().info("[Sword_Sage] Model asset lookup for '" + SWORD_SAGE_MODEL_ID + "' => " + modelAsset);
            if (modelAsset == null) {
                LoggerUtil.getLogger().severe("[Sword_Sage] Missing model asset: " + SWORD_SAGE_MODEL_ID);
                return;
            }

            Model spawnModel = Model.createScaledModel(modelAsset, 1.0f);
            LoggerUtil.getLogger().info("[Sword_Sage] Created scaled model: " + spawnModel);

            store.replaceComponent(npcRef, ModelComponent.getComponentType(), new ModelComponent(spawnModel));
            store.replaceComponent(npcRef, PersistentModel.getComponentType(), new PersistentModel(spawnModel.toReference()));
            LoggerUtil.getLogger().info("[Sword_Sage] Model and persistent model components replaced.");

            NPCEntity npcEntity = store.getComponent(npcRef, Objects.requireNonNull(NPCEntity.getComponentType()));
            LoggerUtil.getLogger().info("[Sword_Sage] NPC entity component present: " + (npcEntity != null));
            if (npcEntity != null) {
                RoleUtils.setItemInHand(npcRef, npcEntity, SWORD_SAGE_EQUIPMENT_ID, store);
                LoggerUtil.getLogger().info("[Sword_Sage] Equipped item in hand: " + SWORD_SAGE_EQUIPMENT_ID);
            }

            store.ensureAndGetComponent(npcRef, Interactable.getComponentType());
            Interactions interactions = store.ensureAndGetComponent(npcRef, Interactions.getComponentType());
            interactions.setInteractionId(InteractionType.Use, INTERACTION_ID);



            VoxelTalesPlugin.get().getVoxelTalesConfigs().get().setServerSetUP(true);
            VoxelTalesPlugin.get().getVoxelTalesConfigs().save();

            LoggerUtil.getLogger().info("[Sword_Sage] Sword_Sage NPC spawned at " + targetPosition);
        });
    }
}

