package me.pollos.polloshook.asm.mixins.entity;


import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.asm.ducks.entity.IClientPlayerEntity;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.impl.events.entity.DeathEvent;
import me.pollos.polloshook.impl.events.entity.EntityInterpolationEvent;
import me.pollos.polloshook.impl.module.misc.swing.Swing;
import me.pollos.polloshook.impl.module.misc.swing.modes.CancelSwing;
import me.pollos.polloshook.impl.module.misc.swing.modes.ForceSwing;
import me.pollos.polloshook.impl.module.movement.elytrafly.ElytraFly;
import me.pollos.polloshook.impl.module.movement.elytrafly.mode.ElytraFlyMode;
import me.pollos.polloshook.impl.module.movement.icespeed.IceSpeed;
import me.pollos.polloshook.impl.module.movement.noslow.NoSlow;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class MixinLivingEntity extends MixinEntity implements ILivingEntity, Minecraftable {
   @Shadow
   @Final
   private static TrackedData<Float> HEALTH;
   @Shadow
   public Hand preferredHand;
   @Shadow
   private int jumpingCooldown;
   @Shadow
   protected double serverX;
   @Shadow
   protected double serverY;
   @Shadow
   protected double serverZ;
   @Shadow
   protected double serverYaw;
   @Shadow
   protected double serverPitch;
   @Shadow
   protected int bodyTrackingIncrements;
   @Mutable
   @Shadow
   @Final
   public LimbAnimator limbAnimator;

   public MixinLivingEntity(EntityType<?> type, World world) {
      super(type, world);
   }

   @Shadow
   protected abstract int getHandSwingDuration();

   @Shadow
   public abstract void remove(RemovalReason var1);

   @Shadow
   public abstract void readCustomDataFromNbt(NbtCompound var1);

   @Shadow
   public abstract EntityDimensions getDimensions(EntityPose var1);

   @Accessor("jumpingCooldown")
   public abstract void setLastJumpCooldown(int var1);

   @Inject(
      method = {"spawnItemParticles"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void spawnItemParticlesHook(ItemStack stack, int count, CallbackInfo ci) {
      NoRender.EatingParticlesEvent event = NoRender.EatingParticlesEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"onTrackedDataSet"},
      at = {@At("RETURN")}
   )
   public void onTrackedDataSetHook(TrackedData<?> key, CallbackInfo info) {
      if (key.equals(HEALTH) && (double)(Float)this.dataTracker.get(HEALTH) <= 0.0D && mc.world != null && mc.world.isClient) {
         DeathEvent deathEvent = new DeathEvent((LivingEntity)LivingEntity.class.cast(this));
         PollosHook.getEventBus().dispatch(deathEvent);
      }

   }

   @Inject(
      method = {"applyClimbingSpeed"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"
)},
      cancellable = true
   )
   private void applyClimbingSpeedHook(Vec3d motion, CallbackInfoReturnable<Vec3d> cir) {
      if (LivingEntity.class.cast(this) == mc.player) {
         NoSlow.ApplyClimbingSpeedEvent event = NoSlow.ApplyClimbingSpeedEvent.of(motion);
         event.dispatch();
         cir.setReturnValue(event.getVec());
      }

   }

   @Inject(
      method = {"isClimbing"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void isClimbingHook(CallbackInfoReturnable<Boolean> cir) {
      if (LivingEntity.class.cast(this) == mc.player) {
         NoSlow.IsClimbingEvent event = NoSlow.IsClimbingEvent.create();
         event.dispatch();
         if (event.isCanceled()) {
            cir.setReturnValue(false);
         }
      }

   }

   @Inject(
      method = {"kill"},
      at = {@At("HEAD")}
   )
   private void killHook(CallbackInfo ci) {
      LivingEntity cast = (LivingEntity)LivingEntity.class.cast(this);
      boolean flag = mc.isInSingleplayer() || mc.world.isClient && cast == mc.player;
      if (flag) {
         DeathEvent event = new DeathEvent(mc.player);
         event.dispatch();
      }

   }

   @Inject(
      method = {"isFallFlying"},
      at = {@At("TAIL")},
      cancellable = true
   )
   private void isFallFlyingHook(CallbackInfoReturnable<Boolean> cir) {
      ElytraFly ELYTRA_FLY = (ElytraFly)Managers.getModuleManager().get(ElytraFly.class);
      if (ELYTRA_FLY.isEnabled() && ELYTRA_FLY.getMode().getValue() == ElytraFlyMode.BOUNCE) {
         if (ELYTRA_FLY.isChina()) {
            mc.options.jumpKey.setPressed(true);
         }

         cir.setReturnValue(ELYTRA_FLY.isChina());
      }

   }

   @Inject(
      method = {"getHandSwingDuration"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getHandSwingDurationHook(CallbackInfoReturnable<Integer> cir) {
      LivingEntity entity = (LivingEntity)LivingEntity.class.cast(this);
      Swing SWING_MODULE = (Swing)Managers.getModuleManager().get(Swing.class);
      if (SWING_MODULE.isEnabled() && entity == mc.player && (Boolean)SWING_MODULE.getSlowSwing().getValue()) {
         cir.setReturnValue((Integer)SWING_MODULE.getDelay().getValue());
      }

   }

   @Redirect(
      method = {"travel"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/Block;getSlipperiness()F"
)
   )
   private float travelHook(Block instance) {
      LivingEntity access = (LivingEntity)LivingEntity.class.cast(this);
      IceSpeed ICE_SPEED = (IceSpeed)Managers.getModuleManager().get(IceSpeed.class);
      if (ICE_SPEED.isEnabled() && IceSpeed.ICE_BLOCKS.contains(instance)) {
         if (access instanceof ClientPlayerEntity) {
            return instance.getSlipperiness();
         } else {
            float factor = instance.equals(Blocks.BLUE_ICE) ? 0.009F : 0.0F;
            return 0.98F + factor;
         }
      } else {
         return instance.getSlipperiness();
      }
   }

   @Redirect(
      method = {"swingHand(Lnet/minecraft/util/Hand;Z)V"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/entity/LivingEntity;preferredHand:Lnet/minecraft/util/Hand;"
)
   )
   private void swingHandHook(LivingEntity instance, Hand value) {
      LivingEntity entity = (LivingEntity)LivingEntity.class.cast(this);
      Swing SWING_MODULE = (Swing)Managers.getModuleManager().get(Swing.class);
      if (SWING_MODULE.isEnabled() && SWING_MODULE.getForceSwing().getValue() != ForceSwing.NONE && entity == mc.player) {
         if (SWING_MODULE.getNoSwing().getValue() != CancelSwing.FULL) {
            boolean cancel = !SWING_MODULE.isCancelSwing() || SWING_MODULE.getNoSwing().getValue() == CancelSwing.NONE;
            if (cancel) {
               this.preferredHand = SWING_MODULE.getHand();
            }

         }
      } else {
         this.preferredHand = value;
      }
   }

   @Inject(
      method = {"swingHand(Lnet/minecraft/util/Hand;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void swingHandHook(Hand hand, CallbackInfo info) {
      LivingEntity entity = (LivingEntity)LivingEntity.class.cast(this);
      Swing SWING_MODULE = (Swing)Managers.getModuleManager().get(Swing.class);
      if (SWING_MODULE.isEnabled() && SWING_MODULE.getNoSwing().getValue() != CancelSwing.NONE) {
         if (SWING_MODULE.getNoSwing().getValue() == CancelSwing.FULL) {
            info.cancel();
         } else {
            if (SWING_MODULE.isCancelSwing() && entity == mc.player) {
               info.cancel();
            }

         }
      }
   }

   @Inject(
      method = {"updateTrackedPositionAndAngles"},
      at = {@At("HEAD")}
   )
   public void updateTrackedPositionAndAnglesHook(double x, double y, double z, float yaw, float pitch, int interpolationSteps, CallbackInfo info) {
      EntityInterpolationEvent event = new EntityInterpolationEvent((LivingEntity)LivingEntity.class.cast(this), x, y, z, yaw, pitch);
      event.dispatch();
   }

   @Inject(
      method = {"tickActiveItemStack"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void tickActiveItemStackHook(CallbackInfo ci) {
      LivingEntity entity = (LivingEntity)LivingEntity.class.cast(this);
      if (entity instanceof ClientPlayerEntity) {
         ClientPlayerEntity clientPlayer = (ClientPlayerEntity)entity;
         IClientPlayerEntity clientPlayerEntity = (IClientPlayerEntity)clientPlayer;
         if (clientPlayerEntity.isEatingFlag()) {
            ci.cancel();
         }
      }

   }

   public Vec3d getServerVec() {
      return new Vec3d(this.serverX, this.serverY, this.serverZ);
   }

   public Box getServerBoundingBox() {
      float f = this.getDimensions(this.getPose()).width() / 2.0F;
      float g = this.getDimensions(this.getPose()).height();
      return new Box(this.serverX - (double)f, this.serverY, this.serverZ - (double)f, this.serverX + (double)f, this.serverY + (double)g, this.serverZ + (double)f);
   }

   public void setServerVec(Vec3d vec) {
      this.serverX = vec.x;
      this.serverY = vec.y;
      this.serverZ = vec.z;
   }

   public void setServerXYZ(double x, double y, double z) {
      this.serverX = x;
      this.serverY = y;
      this.serverZ = z;
   }

   public void setServerYawPitch(float yaw, float pitch) {
      this.serverYaw = (double)yaw;
      this.serverPitch = (double)pitch;
   }

   public void interpolateSteps(int steps) {
      this.bodyTrackingIncrements = steps;
   }

   public int getJumpCooldown() {
      return this.jumpingCooldown;
   }

   public int getArmSwingAnim() {
      return this.getHandSwingDuration();
   }

   
   public void setLimbAnimator(LimbAnimator limbAnimator) {
      this.limbAnimator = limbAnimator;
   }
}