package me.pollos.polloshook.asm.mixins.entity;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.asm.ducks.entity.IPlayerEntity;
import me.pollos.polloshook.impl.events.movement.JumpEvent;
import me.pollos.polloshook.impl.events.movement.KeepSprintEvent;
import me.pollos.polloshook.impl.events.movement.SafeWalkEvent;
import me.pollos.polloshook.impl.module.misc.swing.Swing;
import me.pollos.polloshook.impl.module.misc.swing.modes.CancelSwing;
import me.pollos.polloshook.impl.module.player.reach.Reach;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ PlayerEntity.class })
public abstract class MixinPlayerEntity extends LivingEntity implements IPlayerEntity {
   @Unique
   public final Vec3d[] predictedPositions = new Vec3d[10];
   @Mutable
   @Shadow
   @Final
   PlayerInventory inventory;
   @Unique
   private Vec3d lastSpeedVec;

   protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
      super(entityType, world);
      this.lastSpeedVec = Vec3d.ZERO;
   }

   @Shadow
   public abstract void tick();

   @Shadow
   public abstract void remove(RemovalReason var1);

   @Shadow
   protected abstract boolean clipAtLedge();

   @Shadow
   protected abstract float getDamageAgainst(Entity var1, float var2, DamageSource var3);

   @Inject(method = { "getBlockInteractionRange" }, at = { @At("HEAD") }, cancellable = true)
   private void getBlockInteractionRangeHook(CallbackInfoReturnable<Double> cir) {
      Reach.ReachEvent event = new Reach.ReachEvent(true);
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(
               this.getAttributeValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE) + (double) event.getAdd());
      }

   }

   @Inject(method = { "getEntityInteractionRange" }, at = { @At("HEAD") }, cancellable = true)
   private void getEntityInteractionRangeHook(CallbackInfoReturnable<Double> cir) {
      Reach.ReachEvent event = new Reach.ReachEvent(false);
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(
               this.getAttributeValue(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE) + (double) event.getAdd());
      }

   }

   @Redirect(method = {
         "adjustMovementForSneaking" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;clipAtLedge()Z"))
   private boolean adjustMovementForSneakingHook(PlayerEntity instance) {
      SafeWalkEvent safeWalkEvent = new SafeWalkEvent();
      PollosHook.getEventBus().dispatch(safeWalkEvent);
      return safeWalkEvent.isCanceled() ? false : this.clipAtLedge();
   }

   @Redirect(method = {
         "attack" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
   private void attackHook(PlayerEntity instance, Vec3d vec3d) {
      KeepSprintEvent keepSprintEvent = new KeepSprintEvent(vec3d);
      PollosHook.getEventBus().dispatch(keepSprintEvent);
      this.setVelocity(keepSprintEvent.getMotion());
   }

   @Redirect(method = {
         "attack" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
   private void attackHook(PlayerEntity instance, boolean elementCodec) {
      KeepSprintEvent keepSprintEvent = new KeepSprintEvent(Vec3d.ZERO);
      PollosHook.getEventBus().dispatch(keepSprintEvent);
      if (keepSprintEvent.isCanceled()) {
         instance.setSprinting(true);
      } else {
         instance.setSprinting(false);
      }
   }

   @Inject(method = { "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;" }, at = {
         @At("HEAD") })
   public void dropItemHook(ItemStack stack, boolean throwRandomly, boolean retainOwnership,
         CallbackInfoReturnable<ItemEntity> cir) {
      Swing SWING = (Swing) Managers.getModuleManager().get(Swing.class);
      if (stack != null && this.getWorld().isClient() && SWING.isEnabled()
            && SWING.getNoSwing().getValue() != CancelSwing.NONE) {
         SWING.setCancelSwing(true);
      }

   }

   @Inject(method = { "jump" }, at = { @At("HEAD") }, cancellable = true)
   public void jumpHook(CallbackInfo info) {
      JumpEvent event = new JumpEvent((PlayerEntity) MinecraftClient.getInstance().player);
      PollosHook.getEventBus().dispatch(event);
      if (event.isCanceled()) {
         info.cancel();
      }

   }

   public void $onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source) {
      this.onStatusEffectUpgraded(effect, reapplyEffect, source);
   }

   public void invokeTick() {
      this.tick();
   }

   public boolean isFirstUpdate() {
      return this.firstUpdate;
   }

   public int lastAttackedTicks() {
      return this.lastAttackedTicks;
   }

   public void setLastAttackedTicks(int ticks) {
      this.lastAttackedTicks = ticks;
   }

   public float $getDamageAgainst(LivingEntity livingEntity, float baseDamage, DamageSource damageSource) {
      return this.getDamageAgainst(livingEntity, baseDamage, damageSource);
   }

   public Vec3d[] getPredictedPositions() {
      return this.predictedPositions;
   }

   public void setInventory(PlayerInventory inventory) {
      this.inventory = inventory;
   }

   public void setLastSpeedVec(Vec3d lastSpeedVec) {
      this.lastSpeedVec = lastSpeedVec;
   }

   public Vec3d getLastSpeedVec() {
      return this.lastSpeedVec;
   }
}
