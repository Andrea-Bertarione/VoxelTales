package dev.VoxelTales.Controllers;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.npceditor.NPCRoleAssetTypeHandler;
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
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.RoleUtils;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SwordSageController {
    private static final Vector3f DEFAULT_ROTATION = new Vector3f(0.0F, 1.57F, 0.0F);
    //private static final String DEFAULT_MODEL_ID = "Slothian_Elder";
    private static final String DEFAULT_EQUIPMENT_ID = "Weapon_Staff_Bo_Wood";
    private static final String DEFAULT_ROLE_ID = "Sword_Sage";

    public static void spawnSwordSage(World world, Vector3d position) {
        spawnSwordSage(world, position, null, null, null);
    }

    public static void spawnSwordSage(
            World world,
            Vector3d position,
            @Nullable Vector3f rotation,
            //@Nullable String modelId,
            @Nullable String equipmentId,
            @Nullable String roleId
    ) {
        Vector3f finalRotation = rotation != null ? rotation : DEFAULT_ROTATION;
        //String finalModelId = modelId != null ? modelId : DEFAULT_MODEL_ID;
        String finalEquipmentId = equipmentId != null ? equipmentId : DEFAULT_EQUIPMENT_ID;
        String finalRoleId = roleId != null ? roleId : DEFAULT_ROLE_ID;

        Vector3d targetPosition = new Vector3d(position);
        LoggerUtil.getLogger().info("[Sword_Sage] Base position=" + position + ", target position=" + targetPosition);

        Store<EntityStore> store = world.getEntityStore().getStore();

        Pair<Ref<EntityStore>, INonPlayerCharacter> result =
                NPCPlugin.get().spawnNPC(store, finalRoleId, null, new Vector3d(targetPosition), finalRotation);

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

        store.ensureComponent(npcRef, Interactable.getComponentType());

        /*
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(finalModelId);
        LoggerUtil.getLogger().info("[Sword_Sage] Model asset lookup for '" + finalModelId + "' => " + modelAsset);
        if (modelAsset == null) {
            LoggerUtil.getLogger().severe("[Sword_Sage] Missing model asset: " + finalModelId);
            return;
        }

        Model spawnModel = Model.createScaledModel(modelAsset, 1.0f);
        LoggerUtil.getLogger().info("[Sword_Sage] Created scaled model: " + spawnModel);

        store.replaceComponent(npcRef, ModelComponent.getComponentType(), new ModelComponent(spawnModel));
        store.replaceComponent(npcRef, PersistentModel.getComponentType(), new PersistentModel(spawnModel.toReference()));
        LoggerUtil.getLogger().info("[Sword_Sage] Model and persistent model components replaced.");
         */


        NPCEntity npcEntity = store.getComponent(npcRef, Objects.requireNonNull(NPCEntity.getComponentType()));
        LoggerUtil.getLogger().info("[Sword_Sage] NPC entity component present: " + (npcEntity != null));
        if (npcEntity != null) {
            RoleUtils.setItemInHand(npcRef, npcEntity, finalEquipmentId, store);

            LoggerUtil.getLogger().info("[Sword_Sage] NPC type: " + npcEntity.getNPCTypeId() + " and role: " + npcEntity.getRole().getRoleName());
            LoggerUtil.getLogger().info("[Sword_Sage] Equipped item in hand: " + finalEquipmentId);
        }


        LoggerUtil.getLogger().info("[Sword_Sage] Sword_Sage NPC spawned at " + targetPosition);
    }
}
