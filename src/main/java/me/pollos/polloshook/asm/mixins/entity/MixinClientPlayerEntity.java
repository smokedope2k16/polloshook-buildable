package me.pollos.polloshook.asm.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.asm.ducks.entity.IClientPlayerEntity;
import me.pollos.polloshook.asm.ducks.entity.IEntity;
import me.pollos.polloshook.impl.events.entity.BlockPushEvent;
import me.pollos.polloshook.impl.events.entity.RespawnEvent;
import me.pollos.polloshook.impl.events.item.InputUpdateEvent;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.events.movement.SetSprintEvent;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.movement.noslow.NoSlow;
import me.pollos.polloshook.impl.module.player.sprint.Sprint;
import me.pollos.polloshook.impl.module.render.betterchat.BetterChat;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.dimension.PortalManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientPlayerEntity.class})
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity {
   @Shadow
   @Nullable
   private Hand activeHand;
   @Shadow
   private float lastPitch;
   @Shadow
   private float lastYaw;
   @Shadow
   private boolean lastSneaking;
   @Shadow
   public Input input;
   @Shadow
   @Final
   protected MinecraftClient client;
   @Shadow
   private boolean lastSprinting;
   @Unique
   private MotionUpdateEvent motionUpdateEvent;
   @Unique
   boolean eatingFlag = false;

   private MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
      super(world, profile);
   }

   @Shadow
   protected abstract void autoJump(float var1, float var2);

   @Shadow
   public abstract void requestRespawn();

   @Shadow
   public abstract void swingHand(Hand var1);

   @Shadow
   protected abstract void sendMovementPackets();

   @Shadow
   protected abstract boolean isCamera();

   @Inject(
      method = {"pushOutOfBlocks"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pushOutOfBlocksHook(double x, double z, CallbackInfo ci) {
      BlockPushEvent pushEvent = new BlockPushEvent();
      PollosHook.getEventBus().dispatch(pushEvent);
      if (pushEvent.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"tick()V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V",
   ordinal = 0
)}
   )
   private void tickHook(CallbackInfo ci) {
      UpdateEvent event = new UpdateEvent();
      PollosHook.getEventBus().dispatch(event);
   }

   @Inject(
      method = {"move"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void moveHook(MovementType type, Vec3d movement, CallbackInfo ci) {
      MoveEvent event = new MoveEvent(type, movement);
      PollosHook.getEventBus().dispatch(event);
      if (event.isCanceled()) {
         ci.cancel();
      } else if (!type.equals(event.getType()) || !movement.equals(event.getVec())) {
         double double_1 = this.getX();
         double double_2 = this.getZ();
         super.move(event.getType(), event.getVec());
         this.autoJump((float)(this.getX() - double_1), (float)(this.getZ() - double_2));
         ci.cancel();
      }

   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendMovementPacketsHeadHook(CallbackInfo ci) {
      this.motionUpdateEvent = new MotionUpdateEvent(Stage.PRE, this.getX(), this.getBoundingBox().minY, this.getZ(), this.getYaw(), this.getPitch(), this.isOnGround());
      PollosHook.getEventBus().dispatch(this.motionUpdateEvent);
      ((IEntity)this).setX(this.motionUpdateEvent.getX());
      ((IEntity)this).setY(this.motionUpdateEvent.getY());
      ((IEntity)this).setZ(this.motionUpdateEvent.getZ());
      this.setYaw(this.motionUpdateEvent.getYaw());
      this.setPitch(this.motionUpdateEvent.getPitch());
      this.setOnGround(this.motionUpdateEvent.isOnGround());
      if (this.motionUpdateEvent.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"tick"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V",
   shift = Shift.AFTER
)}
   )
   private void sendMovementPackets_Post(CallbackInfo ci) {
      if (this.getPos().x == this.motionUpdateEvent.getX()) {
         ((IEntity)this).setX(this.motionUpdateEvent.getInitialX());
      }

      if (this.getPos().y == this.motionUpdateEvent.getY()) {
         ((IEntity)this).setY(this.motionUpdateEvent.getInitialY());
      }

      if (this.getPos().z == this.motionUpdateEvent.getZ()) {
         ((IEntity)this).setZ(this.motionUpdateEvent.getInitialZ());
      }

      if (this.getYaw() == this.motionUpdateEvent.getYaw()) {
         this.setYaw(this.motionUpdateEvent.getInitialYaw());
      }

      if (this.getPitch() == this.motionUpdateEvent.getPitch()) {
         this.setPitch(this.motionUpdateEvent.getInitialPitch());
      }

      if (this.isOnGround() == this.motionUpdateEvent.isOnGround()) {
         this.setOnGround(this.motionUpdateEvent.isInitialOnGround());
      }

   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("RETURN")}
   )
   private void sendMovementPacketsReturnHook(CallbackInfo ci) {
      MotionUpdateEvent event = new MotionUpdateEvent(Stage.POST, this.motionUpdateEvent);
      event.setCanceled(this.motionUpdateEvent.isCanceled());
      PollosHook.getEventBus().dispatchReversed(event, (Class)null);
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"
)
   )
   private boolean sendMovementPacketsIsCamera(ClientPlayerEntity instance) {
      return ((Freecam)Managers.getModuleManager().get(Freecam.class)).isEnabled() || this.isCamera();
   }

   @Inject(
      method = {"requestRespawn"},
      at = {@At("HEAD")}
   )
   private void requestRespawnHook(CallbackInfo ci) {
      RespawnEvent respawnEvent = new RespawnEvent();
      PollosHook.getEventBus().dispatch(respawnEvent);
   }

   @Redirect(
      method = {"tickNausea"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/dimension/PortalManager;isInPortal()Z",
   ordinal = 0
)
   )
   private boolean updateNauseaHook(PortalManager instance) {
      BetterChat BETTER_CHAT = (BetterChat)Managers.getModuleManager().get(BetterChat.class);
      return (Boolean)BETTER_CHAT.getPortals().getValue() && BETTER_CHAT.isEnabled() ? false : instance.isInPortal();
   }

   @ModifyExpressionValue(
      method = {"canStartSprinting"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z"
)}
   )
   private boolean canStartSprintingHook(boolean original) {
      SetSprintEvent setSprintEvent = new SetSprintEvent();
      PollosHook.getEventBus().dispatch(setSprintEvent);
      if (!setSprintEvent.isCanceled()) {
         return original;
      } else {
         float forwards = Math.abs(this.input.movementSideways);
         float sideways = Math.abs(this.input.movementForward);
         return this.isSubmergedInWater() ? forwards > 1.0E-5F || sideways > 1.0E-5F : (double)forwards > 0.8D || (double)sideways > 0.8D;
      }
   }

   @Inject(
      method = {"shouldSpawnSprintingParticles"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void shouldSpawnSprintingParticlesHook(CallbackInfoReturnable<Boolean> cir) {
      if (((Sprint)Managers.getModuleManager().get(Sprint.class)).isRage()) {
         cir.setReturnValue(false);
      }

   }

   @ModifyExpressionValue(
      method = {"tickMovement"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z"
)}
   )
   private boolean tickMovementHook(boolean original) {
      SetSprintEvent setSprintEvent = new SetSprintEvent();
      PollosHook.getEventBus().dispatch(setSprintEvent);
      if (!setSprintEvent.isCanceled()) {
         return original;
      } else {
         return Math.abs(this.input.movementSideways) > 1.0E-5F || Math.abs(this.input.movementForward) > 1.0E-5F;
      }
   }

   @Redirect(
      method = {"tickMovement"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/input/Input;tick(ZF)V"
)
   )
   private void tickMovementHook(Input instance, boolean slowDown, float slowDownFactor) {
      this.input.tick(slowDown, slowDownFactor);
      InputUpdateEvent inputUpdateEvent = new InputUpdateEvent(instance);
      PollosHook.getEventBus().dispatch(inputUpdateEvent);
   }

   @Redirect(
      method = {"sendMovementPackets"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSneaking()Z"
)
   )
   private boolean sendMovementPacketsHook(ClientPlayerEntity instance) {
      return instance.isSneaking() || ((NoSlow)Managers.getModuleManager().get(NoSlow.class)).isAirStrict();
   }

   public double squaredDistanceTo(BlockPos pos) {
      return this.squaredDistanceTo((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
   }

   public HitResult raycastFromCustomAngles(double maxDistance, float tickDelta, boolean includeFluids, float yaw, float pitch) {
      Vec3d vec3d = this.getCameraPosVec(tickDelta);
      Vec3d vec3d2 = this.getRotationVector(yaw, pitch);
      Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
      return this.getWorld().raycast(new RaycastContext(vec3d, vec3d3, ShapeType.COLLIDER, includeFluids ? FluidHandling.ANY : FluidHandling.NONE, this));
   }

   public void setActiveHand(Hand hand) {
      if (this.activeHand != null) {
         this.activeHand = hand;
      }
   }

   public float getLastPitch() {
      return this.lastPitch;
   }

   public float getLastYaw() {
      return this.lastYaw;
   }

   public void setLastPitch(float lastPitch) {
      this.lastPitch = lastPitch;
   }

   public void setLastYaw(float lastYaw) {
      this.lastYaw = lastYaw;
   }

   public boolean getLastSneaking() {
      return this.lastSneaking;
   }

   public boolean getLastSprinting() {
      return this.lastSprinting;
   }

   public void setLastSneaking(boolean sneaking) {
      this.lastSneaking = sneaking;
   }

   public void setLastSprinting(boolean lastSprinting) {
      this.lastSprinting = lastSprinting;
   }

   public void $tick() {
      this.eatingFlag = true;
      this.tick();
      this.eatingFlag = false;
   }

   public void sendMovePackets() {
      this.sendMovementPackets();
   }

   public boolean getIsCamera() {
      return this.isCamera();
   }

   
   public boolean isEatingFlag() {
      return this.eatingFlag;
   }
}
