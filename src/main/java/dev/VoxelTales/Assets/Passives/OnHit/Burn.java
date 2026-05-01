package dev.VoxelTales.Assets.Passives.OnHit;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Assets.Passives.VoxelPassive;
import dev.VoxelTales.Utils.VoxelStatsHelper;

public class Burn extends VoxelPassive {
    private static final EntityEffect BURN_EFFECT = EntityEffect.getAssetMap().getAsset("Burn");

    public Burn() {
        super("Burn");
    }

    public void onHit(Ref<EntityStore> attackerRef, Ref<EntityStore> targetRef, float statValue, ComponentAccessor<EntityStore> accessor) {
        if (Math.random() > statValue) return;
        if (targetRef == null || !targetRef.isValid()) return;
        if (BURN_EFFECT == null) return;

        EffectControllerComponent effectControllerComponent = accessor.ensureAndGetComponent(targetRef, EffectControllerComponent.getComponentType());

        float duration = accessor.ensureAndGetComponent(attackerRef, EntityStatMap.getComponentType()).get(VoxelStatsHelper.getStatIndex("Passive_Burn_Duration")).get();
        effectControllerComponent.addEffect(targetRef, BURN_EFFECT, duration, OverlapBehavior.EXTEND, accessor);
    }
}
