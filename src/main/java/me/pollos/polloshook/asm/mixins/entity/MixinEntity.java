package me.pollos.polloshook.asm.mixins.entity;


import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.asm.ducks.entity.IEntity;
import me.pollos.polloshook.impl.events.entity.EntityPushEvent;
import me.pollos.polloshook.impl.events.movement.StepEvent;
import me.pollos.polloshook.impl.events.movement.SwimEvent;
import me.pollos.polloshook.impl.events.movement.VelocityMultiplierEvent;
import me.pollos.polloshook.impl.events.render.EntityOutlineColorEvent;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import me.pollos.polloshook.impl.module.player.reach.Reach;
import me.pollos.polloshook.impl.module.player.sprint.Sprint;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import me.pollos.polloshook.impl.module.render.shader.Shader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public abstract class MixinEntity implements IEntity {
   @Final
   @Shadow
   protected DataTracker dataTracker;
   @Shadow
   private Vec3d pos;
   @Shadow
   protected Vec3d movementMultiplier;
   @Shadow
   private Box boundingBox;
   @Shadow
   @Final
   private static TrackedData<Boolean> NAME_VISIBLE;
   @Unique
   private boolean inWeb;
   @Unique
   private Float prevStepHeight;
   @Unique
   boolean prevOnGround;

   public MixinEntity(EntityType<?> type, World world) {
   }

   @Shadow
   public abstract boolean isSpectator();

   @Shadow
   public abstract boolean damage(DamageSource var1, float var2);

   @Shadow
   public abstract Box getBoundingBox();

   @Shadow
   public abstract boolean isOnGround();

   @Shadow
   public abstract boolean equals(Object var1);

   @Shadow
   public abstract boolean isSprinting();

   @Shadow
   public abstract ActionResult interact(PlayerEntity var1, Hand var2);

   @Shadow
   protected abstract void setFlag(int var1, boolean var2);

   @Shadow
   public abstract EntityPose getPose();

   @Shadow
   public abstract BlockState getBlockStateAtPos();

   @Shadow
   public abstract float getStepHeight();

   @Invoker("adjustMovementForCollisions")
   public abstract Vec3d adjustForCollisions(Vec3d var1);

   @Invoker("adjustMovementForSneaking")
   public abstract Vec3d adjustForSneaking(Vec3d var1, MovementType var2);

   @Inject(
      method = {"setOnGround(Z)V"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/entity/Entity;onGround:Z",
   shift = Shift.BEFORE
)}
   )
   private void setOnGroundHook(boolean onGround, CallbackInfo ci) {
      this.prevOnGround = this.isOnGround();
   }

   @Inject(
      method = {"setOnGround(ZLnet/minecraft/util/math/Vec3d;)V"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/entity/Entity;onGround:Z",
   shift = Shift.BEFORE
)}
   )
   private void setOnGroundHook(boolean onGround, Vec3d movement, CallbackInfo ci) {
      this.prevOnGround = this.isOnGround();
   }

   @Inject(
      method = {"move"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"
)}
   )
private void move_stepPosthook0(CallbackInfo info) {
      if (ClientPlayerEntity.class.isInstance(this)) {
         Object thisObj = this;

         ClientPlayerEntity me = (ClientPlayerEntity)thisObj;
         StepEvent stepEvent = new StepEvent(Stage.PRE, this.getBoundingBox(), this.getStepHeight());
         PollosHook.getEventBus().dispatch(stepEvent);
         me.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue((double)stepEvent.getHeight());
         this.prevStepHeight = this.getStepHeight();
      }

   }

   @Inject(
      method = {"move"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;setPosition(DDD)V",
   shift = Shift.AFTER,
   ordinal = 1
)}
   )
   private void move_stepPosthook(MovementType movementType, Vec3d movement, CallbackInfo ci) {
      if (ClientPlayerEntity.class.isInstance(this)) {
         StepEvent event = new StepEvent(Stage.POST, this.getBoundingBox(), this.prevStepHeight != null ? this.prevStepHeight : 0.0F);
         PollosHook.getEventBus().dispatchReversed(event, (Class)null);
      }

   }

   @Inject(
      method = {"move"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;getProfiler()Lnet/minecraft/util/profiler/Profiler;",
   shift = Shift.AFTER
)}
   )
   private void moveHook(MovementType movementType, Vec3d movement, CallbackInfo ci) {
      if (this.inWeb && this.movementMultiplier == Vec3d.ZERO) {
         this.inWeb = false;
      }

   }

   @Inject(
      method = {"isGlowing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isGlowingHook(CallbackInfoReturnable<Boolean> cir) {
      Shader SHADER = (Shader)Managers.getModuleManager().get(Shader.class);
      if (SHADER.isEnabled()) {
         Object thisObj = this;

         boolean valid = SHADER.isValid((Entity)thisObj);
         cir.setReturnValue(valid);
      }

   }

   @Inject(
      method = {"pushAwayFrom"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pushAwayFromHook(Entity entity, CallbackInfo ci) {
      EntityPushEvent pushEvent = new EntityPushEvent(entity);
      PollosHook.getEventBus().dispatch(pushEvent);
      if (pushEvent.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"doesNotCollide(Lnet/minecraft/util/math/Box;)Z"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void doesNotCollideHook(Box box, CallbackInfoReturnable<Boolean> cir) {
      if (((Manager)Managers.getModuleManager().get(Manager.class)).stopCrawling()) {
         cir.setReturnValue(true);
      }

   }

   @Inject(
      method = {"isInvisible"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isInvisibleHook(CallbackInfoReturnable<Boolean> cir) {
      if (!this.isSpectator()) {
         NoRender.InvisibleEntityEvent event = NoRender.InvisibleEntityEvent.create();
         event.dispatch();
         if (event.isCanceled()) {
            cir.setReturnValue(false);
         }

      }
   }

   @Inject(
      method = {"getTeamColorValue"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getTeamColorValueHook(CallbackInfoReturnable<Integer> cir) {
      Object thisObj = this;

      EntityOutlineColorEvent entityOutlineColorEvent = new EntityOutlineColorEvent((Entity)thisObj);
      PollosHook.getEventBus().dispatch(entityOutlineColorEvent);
      if (entityOutlineColorEvent.isCanceled()) {
         cir.setReturnValue(entityOutlineColorEvent.getColor());
      }

   }

   @Redirect(
      method = {"getVelocityMultiplier"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"
)
   )
   private Block getVelocityMultiplierHook(BlockState instance) {
      Object thisObj = this;

      if (thisObj != MinecraftClient.getInstance().player) {
         return instance.getBlock();
      } else {
         VelocityMultiplierEvent multiplierEvent = new VelocityMultiplierEvent(instance.getBlock());
         PollosHook.getEventBus().dispatch(multiplierEvent);
         return multiplierEvent.isCanceled() ? Blocks.AIR : instance.getBlock();
      }
   }

   @Inject(
      method = {"isSprinting"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isSprintingHook(CallbackInfoReturnable<Boolean> cir) {
      Object thisObj = this;

      Entity ent = (Entity)thisObj;
      if (ent instanceof ClientPlayerEntity) {
         Sprint.SprintEvent event = new Sprint.SprintEvent();
         event.dispatch();
         if (event.isCanceled()) {
            cir.setReturnValue(false);
         }
      }

   }

   @Inject(
      method = {"doesRenderOnFire"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void doesRenderOnFireHook(CallbackInfoReturnable<Boolean> cir) {
      NoRender.EntityFireEvent event = NoRender.EntityFireEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"getTargetingMargin"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getTargetingMarginHook(CallbackInfoReturnable<Float> cir) {
      Reach.HitboxEvent event = new Reach.HitboxEvent();
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(event.getAdd());
      }

   }

   @Inject(
      method = {"updateSwimming"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateSwimmingHook(CallbackInfo ci) {
      Object thisObj = this;

      SwimEvent event = new SwimEvent((Entity)thisObj);
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"isSwimming"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isSwimmingHook(CallbackInfoReturnable<Boolean> cir) {
      Object thisObj = this;

      SwimEvent event = new SwimEvent((Entity)thisObj);
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"isInSwimmingPose"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isInSwimmingPoseHook(CallbackInfoReturnable<Boolean> cir) {
      Object thisObj = this;

      SwimEvent event = new SwimEvent((Entity)thisObj);
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(false);
      }

   }

   @Redirect(
      method = {"setSwimming"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;setFlag(IZ)V"
)
   )
   private void setSwimmingHook(Entity instance, int index, boolean value) {
      Object thisObj = this;

      SwimEvent event = new SwimEvent((Entity)thisObj);
      event.dispatch();
      this.setFlag(index, !event.isCanceled() && value);
   }

   @Inject(
      method = {"setSprinting"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void setSprintingHook(boolean sprinting, CallbackInfo ci) {
      Object thisObj = this;

      Entity ent = (Entity)thisObj;
      if (ent instanceof ClientPlayerEntity) {
         Sprint.SprintEvent event = new Sprint.SprintEvent();
         event.dispatch();
         if (event.isCanceled()) {
            ci.cancel();
         }
      }

   }

   public TrackedData<Boolean> getNameVisible() {
      return NAME_VISIBLE;
   }

   public void setX(double x) {
      this.pos = new Vec3d(x, this.pos.y, this.pos.z);
   }

   public void setY(double y) {
      this.pos = new Vec3d(this.pos.x, y, this.pos.z);
   }

   public void setZ(double z) {
      this.pos = new Vec3d(this.pos.x, this.pos.y, z);
   }

   
   public void setInWeb(boolean inWeb) {
      this.inWeb = inWeb;
   }

   
   public boolean isInWeb() {
      return this.inWeb;
   }

   
   public boolean isPrevOnGround() {
      return this.prevOnGround;
   }
}
