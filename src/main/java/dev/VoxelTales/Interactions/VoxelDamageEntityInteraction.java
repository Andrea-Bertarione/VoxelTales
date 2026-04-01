package dev.VoxelTales.Interactions;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector4d;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.EntitySnapshot;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.ItemUtils;
import com.hypixel.hytale.server.core.entity.knockback.KnockbackComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.meta.DynamicMetaStore;
import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCalculatorSystems;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.SelectInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.DamageEntityInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageCalculator;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageEffects;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.Knockback;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.VoxelTales.Utils.VoxelDamageEntityReflection;
import dev.VoxelTales.Utils.VoxelMetadata;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoxelDamageEntityInteraction extends DamageEntityInteraction {
    @Override
    protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> targetRef = context.getTargetEntity();
        if (targetRef != null && targetRef.isValid() && context.getEntity().isValid()) {
            CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
            assert commandBuffer != null;

            Damage[] queuedDamage = context.getInstanceStore().getIfPresentMetaObject(VoxelDamageEntityReflection.QUEUED_DAMAGE);

            if (!VoxelDamageEntityReflection.invokeProcessDamage(this, context, queuedDamage)) {
                Ref<EntityStore> ref = context.getOwningEntity();
                Vector4d hit = context.getMetaStore().getMetaObject(Interaction.HIT_LOCATION);
                Damage.EntitySource source = new Damage.EntitySource(ref);

                // Route to our custom method instead of the base private one 
                this.voxelAttemptEntityDamage0(source, context, context.getEntity(), targetRef, hit);

                if (SelectInteraction.SHOW_VISUAL_DEBUG && hit != null) {
                    DebugUtils.addSphere(commandBuffer.getExternalData().getWorld(), new Vector3d(hit.x, hit.y, hit.z), new Vector3f(1.0F, 0.0F, 0.0F), 0.2F, 5.0F);
                }
            }
        } else {
            context.jump(context.getLabel(0));
            context.getState().nextLabel = 0;
            context.getState().state = InteractionState.Failed;
        }
    }

    private void voxelAttemptEntityDamage0(@Nonnull Damage.Source source, @Nonnull InteractionContext context, @Nonnull Ref<EntityStore> attackerRef, @Nonnull Ref<EntityStore> targetRef, @Nullable Vector4d hit) {
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        assert commandBuffer != null;

        // Pulling from protected fields natively 
        DamageCalculator damageCalculator = this.damageCalculator;
        DamageEffects damageEffects = this.damageEffects;
        EntitySnapshot targetSnapshot = context.getSnapshot(targetRef, commandBuffer);
        EntitySnapshot attackerSnapshot = context.getSnapshot(attackerRef, commandBuffer);
        Vector3d targetPos = targetSnapshot.getPosition();
        Vector3d attackerPos = attackerSnapshot.getPosition();
        float angleBetween = TrigMathUtil.atan2(attackerPos.x - targetPos.x, attackerPos.z - targetPos.z);
        int nextLabel = 1;

        if (this.angledDamage != null) {
            float angleBetweenRotation = MathUtil.wrapAngle(angleBetween + (float)Math.PI - targetSnapshot.getBodyRotation().getYaw());
            for(int i = 0; i < this.angledDamage.length; ++i) {
                AngledDamage angledDamage = this.angledDamage[i];

                // Grab the protected values via reflection
                float aRad = VoxelDamageEntityReflection.getAngleRad(angledDamage);
                float aDistRad = VoxelDamageEntityReflection.getAngleDistanceRad(angledDamage);
                DamageCalculator adCalc = VoxelDamageEntityReflection.getDamageCalculator(angledDamage);
                DamageEffects adEffects = VoxelDamageEntityReflection.getDamageEffects(angledDamage);

                if (Math.abs(MathUtil.compareAngle(angleBetweenRotation, aRad)) < (double)aDistRad) {
                    damageCalculator = adCalc == null ? damageCalculator : adCalc;
                    damageEffects = adEffects == null ? damageEffects : adEffects;
                    nextLabel = 3 + i;
                    break;
                }
            }
        }

        String hitDetail = context.getMetaStore().getIfPresentMetaObject(Interaction.HIT_DETAIL);
        if (hitDetail != null) {
            TargetedDamage entry = this.targetedDamage.get(hitDetail);
            if (entry != null) {
                // Grab the protected values via reflection
                DamageCalculator entryCalc = VoxelDamageEntityReflection.getDamageCalculator(entry);
                DamageEffects entryEffects = VoxelDamageEntityReflection.getDamageEffects(entry);
                int entryIndex = VoxelDamageEntityReflection.getIndex(entry);

                damageCalculator = entryCalc == null ? damageCalculator : entryCalc;
                damageEffects = entryEffects == null ? damageEffects : entryEffects;
                nextLabel = entryIndex;
            }
        }

        context.getInstanceStore().putMetaObject(VoxelDamageEntityReflection.NEXT_INDEX, nextLabel);

        if (damageCalculator != null) {
            DynamicMetaStore<Interaction> metaStore = context.getMetaStore().getMetaObject(SelectInteraction.SELECT_META_STORE);
            DamageCalculatorSystems.Sequence sequentialHits = metaStore == null ? new DamageCalculatorSystems.Sequence() : metaStore.getMetaObject(VoxelDamageEntityReflection.SEQUENTIAL_HITS);
            Object2FloatMap<DamageCause> damage = damageCalculator.calculateDamage(this.getRunTime());
            HeadRotation attackerHeadRotationComponent = commandBuffer.getComponent(attackerRef, HeadRotation.getComponentType());
            Vector3f attackerDirection;

            if (attackerHeadRotationComponent != null) {
                attackerDirection = attackerHeadRotationComponent.getRotation();
            } else {
                attackerDirection = Vector3f.ZERO;
            }

            if (damage != null && !damage.isEmpty()) {
                double[] knockbackMultiplier = new double[]{(double)1.0F};
                float[] armorDamageModifiers = new float[]{0.0F, 1.0F};

                // Reflection Invocation [cite: 235]
                VoxelDamageEntityReflection.invokeKnockback(damageCalculator.getDamageClass(), damage, targetRef, attackerRef, armorDamageModifiers, knockbackMultiplier, commandBuffer);

                KnockbackComponent knockbackComponent = null;
                if (damageEffects != null && damageEffects.getKnockback() != null) {
                    knockbackComponent = commandBuffer.getComponent(targetRef, KnockbackComponent.getComponentType());
                    if (knockbackComponent == null) {
                        knockbackComponent = new KnockbackComponent();
                        commandBuffer.putComponent(targetRef, KnockbackComponent.getComponentType(), knockbackComponent);
                    }

                    Knockback knockback = damageEffects.getKnockback();
                    knockbackComponent.setVelocity(knockback.calculateVector(attackerPos, attackerDirection.getYaw(), targetPos).scale(knockbackMultiplier[0]));
                    knockbackComponent.setVelocityType(knockback.getVelocityType());
                    knockbackComponent.setVelocityConfig(knockback.getVelocityConfig());
                    knockbackComponent.setDuration(knockback.getDuration());
                }

                ItemStack itemInHand = ItemUtils.canApplyItemStackPenalties(attackerRef, commandBuffer) ? context.getHeldItem() : null;
                Damage[] hits = DamageCalculatorSystems.queueDamageCalculator((commandBuffer.getExternalData()).getWorld(), damage, targetRef, context.getCommandBuffer(), source, itemInHand);

                if (hits.length > 0) {
                    Damage firstDamage = hits[0];
                    DamageCalculatorSystems.DamageSequence seq = new DamageCalculatorSystems.DamageSequence(sequentialHits, damageCalculator);
                    seq.setEntityStatOnHit(this.entityStatsOnHit);
                    firstDamage.putMetaObject(DamageCalculatorSystems.DAMAGE_SEQUENCE, seq);
                    if (damageEffects != null) {
                        damageEffects.addToDamage(firstDamage);
                    }

                    String voxelSource = context.getMetaStore().getIfPresentMetaObject(VoxelMetadata.INTERACTION_SOURCE_KEY);

                    for(Damage damageEvent : hits) {
                        if (knockbackComponent != null) {
                            damageEvent.putMetaObject(Damage.KNOCKBACK_COMPONENT, knockbackComponent);
                        }

                        float damageValue = damageEvent.getAmount();
                        damageValue += armorDamageModifiers[0];
                        damageEvent.setAmount(damageValue * Math.max(0.0F, armorDamageModifiers[1]));
                        if (hit != null) {
                            damageEvent.putMetaObject(Damage.HIT_LOCATION, hit);
                            float hitAngleRad = TrigMathUtil.atan2(attackerPos.x - hit.x, attackerPos.z - hit.z);
                            hitAngleRad = MathUtil.wrapAngle(hitAngleRad - attackerDirection.getYaw());
                            float hitAngleDeg = hitAngleRad * (180F / (float)Math.PI);
                            damageEvent.putMetaObject(Damage.HIT_ANGLE, hitAngleDeg);
                        }

                        damageEvent.getMetaStore().putMetaObject(VoxelMetadata.DAMAGE_SOURCE_KEY, voxelSource);

                        commandBuffer.invoke(targetRef, damageEvent);
                    }

                    VoxelDamageEntityReflection.invokeProcessDamage(this, context, hits);
                }

                context.getInstanceStore().putMetaObject(VoxelDamageEntityReflection.QUEUED_DAMAGE, hits);
            }
        }
    }
}