package me.pollos.polloshook.asm.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.render.RenderHandEvent;
import me.pollos.polloshook.impl.events.render.RenderWorldTailEvent;
import me.pollos.polloshook.impl.events.world.AntiHitboxEvent;
import me.pollos.polloshook.impl.events.world.RaycastEvent;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import me.pollos.polloshook.impl.module.player.liquidinteract.LiquidInteract;
import me.pollos.polloshook.impl.module.render.fovmodifier.FOVModifier;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer implements me.pollos.polloshook.asm.ducks.world.IGameRenderer {
   @Shadow
   @Final
   MinecraftClient client;
   @Shadow
   private boolean renderingPanorama;
   @Shadow
   private float lastFovMultiplier;
   @Shadow
   private float fovMultiplier;
   @Shadow
   @Final
   private Camera camera;
   @Shadow
   private float zoom;
   @Shadow
   private float zoomX;
   @Shadow
   private float zoomY;
   @Unique
   private Entity cameraEntity;
   @Unique
   private boolean renderingHand = false;
   @Final
   @Shadow
   private BufferBuilderStorage buffers;
   @Final
   @Shadow
   public HeldItemRenderer firstPersonRenderer;
   @Final
   @Shadow
   private LightmapTextureManager lightmapTextureManager;

   @Shadow
   public abstract Matrix4f getBasicProjectionMatrix(double var1);

   @Shadow
   protected abstract double getFov(Camera var1, float var2, boolean var3);

   @Shadow
   private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
      return null;
   }

   @Shadow
   public abstract Camera getCamera();

   @Shadow
   protected abstract HitResult findCrosshairTarget(Entity var1, double var2, double var4, float var6);

   @Shadow
   public abstract float getFarPlaneDistance();

   @Shadow
   protected abstract void renderHand(Camera var1, float var2, Matrix4f var3);

   @Shadow
   public abstract void loadProjectionMatrix(Matrix4f var1);

   @Shadow
   protected abstract void tiltViewWhenHurt(MatrixStack var1, float var2);

   @Shadow
   protected abstract void bobView(MatrixStack var1, float var2);

   @Inject(
      method = {"showFloatingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )

   private void showFloatingItemHook(ItemStack floatingItem, CallbackInfo info) {
      if (floatingItem.getItem() == Items.TOTEM_OF_UNDYING) {
         NoRender.TotemOverlayEvent event = NoRender.TotemOverlayEvent.create();
         event.dispatch();
         if (event.isCanceled()) {
            info.cancel();
         }

      }
   }

   @Inject(
      method = {"renderWorld"},
      at = {@At("RETURN")}
   )
   private void hook(RenderTickCounter tickCounter, CallbackInfo ci) {
      RenderWorldTailEvent event = new RenderWorldTailEvent(tickCounter);
      event.dispatch();
   }

   @Inject(
      method = {"renderHand"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/GameRenderer;getBasicProjectionMatrix(D)Lorg/joml/Matrix4f;"
)},
      cancellable = true
   )
   private void renderHandHook(Camera camera, float tickDelta, Matrix4f matrix4f, CallbackInfo ci) {
      RenderHandEvent event = new RenderHandEvent();
      event.dispatch();
      this.renderingHand = true;
      if (event.isCanceled()) {
         ci.cancel();
         this.renderingHand = false;
      }

   }

   @Inject(
      method = {"renderHand"},
      at = {@At("TAIL")}
   )
   private void renderHandTailHook(Camera camera, float tickDelta, Matrix4f matrix4f, CallbackInfo ci) {
      this.renderingHand = false;
   }

@Redirect(
      method = {"findCrosshairTarget"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"
)
   )
   private HitResult findCrosshairTargetHook(Entity entity, double maxDistance, float tickDelta, boolean includeFluids) {
      if (((LiquidInteract)Managers.getModuleManager().get(LiquidInteract.class)).isEnabled()) {
         HitResult result = entity.raycast(maxDistance, tickDelta, includeFluids);
         return result.getType() != Type.MISS ? result : entity.raycast(maxDistance, tickDelta, true);
      } else {
         return entity.raycast(maxDistance, tickDelta, includeFluids);
      }
   }

   @Redirect(
      method = {"updateCrosshairTarget"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/GameRenderer;findCrosshairTarget(Lnet/minecraft/entity/Entity;DDF)Lnet/minecraft/util/hit/HitResult;"
)
   )
   private HitResult updateCrosshairTargetHook(GameRenderer instance, Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
      Freecam.FindCrosshairEvent event = Freecam.FindCrosshairEvent.of(camera);
      event.dispatch();
      this.cameraEntity = camera;
      return this.findCrosshairTarget(event.getEntity(), blockInteractionRange, entityInteractionRange, tickDelta);
   }

   @Redirect(
      method = {"findCrosshairTarget"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/GameRenderer;ensureTargetInRange(Lnet/minecraft/util/hit/HitResult;Lnet/minecraft/util/math/Vec3d;D)Lnet/minecraft/util/hit/HitResult;"
)
   )
   private HitResult findCrosshairTargetHook(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
      AntiHitboxEvent event = new AntiHitboxEvent();
      event.dispatch();
      if (event.isCanceled()) {
         double d = Math.max(this.client.player.getBlockInteractionRange(), this.client.player.getEntityInteractionRange());
         Entity entity = this.cameraEntity == null ? this.client.getCameraEntity() : this.cameraEntity;
         return entity.raycast(d, this.client.getRenderTickCounter().getTickDelta(true), false);
      } else {
         return ensureTargetInRange(hitResult, cameraPos, interactionRange);
      }
   }

   @Inject(
      method = {"updateCrosshairTarget"},
      at = {@At("RETURN")}
   )
   private void updateCrosshairTargetHook(float tickDelta, CallbackInfo ci) {
      RaycastEvent raycastEvent = new RaycastEvent(this.client.crosshairTarget);
      PollosHook.getEventBus().dispatch(raycastEvent);
      this.client.crosshairTarget = raycastEvent.getResult();
   }

   @Inject(
      method = {"getFov"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getFovHook(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
      FOVModifier FOV_MODIFIER_MODULE = (FOVModifier)Managers.getModuleManager().get(FOVModifier.class);
      if (FOV_MODIFIER_MODULE.getNoLavaFOV()) {
         if (this.renderingPanorama) {
            cir.setReturnValue(90.0D);
         }

         double d = 70.0D;
         if (changingFov) {
            d = (double)(Integer)this.client.options.getFov().getValue();
            d *= (double)MathHelper.lerp(tickDelta, this.lastFovMultiplier, this.fovMultiplier);
         }

         if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).isDead()) {
            float f = Math.min((float)((LivingEntity)camera.getFocusedEntity()).deathTime + tickDelta, 20.0F);
            d /= (double)((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
         }

         cir.setReturnValue(d);
      }

   }

   @Redirect(
      method = {"getBasicProjectionMatrix"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/Window;getFramebufferHeight()I"
)
   )
   private int getBasicProjectionMatrixHook_height(Window instance) {
      Manager.AspectRatioHeightEvent event = Manager.AspectRatioHeightEvent.of((float)instance.getFramebufferHeight());
      event.dispatch();
      return (int)event.getHeight();
   }

   @Redirect(
      method = {"getBasicProjectionMatrix"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/Window;getFramebufferWidth()I"
)
   )
   private int getBasicProjectionMatrixHook_width(Window instance) {
      Manager.AspectRatioWidthEvent event = Manager.AspectRatioWidthEvent.of((float)instance.getFramebufferWidth());
      event.dispatch();
      return (int)event.getWidth();
   }

   @Redirect(
      method = {"renderHand"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/GameRenderer;getBasicProjectionMatrix(D)Lorg/joml/Matrix4f;"
)
   )
   private Matrix4f renderHandHook(GameRenderer instance, double fov) {
      return this.noAspectRatioMatrix(fov);
   }

   @Unique
   private Matrix4f noAspectRatioMatrix(double fov) {
      Matrix4f matrix4f = new Matrix4f();
      if (this.zoom != 1.0F) {
         matrix4f.translate(this.zoomX, -this.zoomY, 0.0F);
         matrix4f.scale(this.zoom, this.zoom, 1.0F);
      }

      return matrix4f.perspective((float)(fov * 0.01745329238474369D), (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight(), 0.05F, this.getFarPlaneDistance());
   }

   public void getRenderHand(Camera camera, float tickDelta, Matrix4f matrix4f) {
      this.renderHand(camera, tickDelta, matrix4f);
   }

   public void renderHandFast(Camera camera, float tickDelta, Matrix4f matrix4f) {
      if (!this.renderingPanorama) {
         this.loadProjectionMatrix(this.getBasicProjectionMatrix(this.getFov(camera, tickDelta, false)));
         MatrixStack matrixStack = new MatrixStack();
         matrixStack.push();
         matrixStack.multiplyPositionMatrix(matrix4f.invert(new Matrix4f()));
         Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
         matrix4fStack.pushMatrix().mul(matrix4f);
         RenderSystem.applyModelViewMatrix();
         this.tiltViewWhenHurt(matrixStack, tickDelta);
         if ((Boolean)this.client.options.getBobView().getValue()) {
            this.bobView(matrixStack, tickDelta);
         }

         boolean bl = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
         if (this.client.options.getPerspective().isFirstPerson() && !bl && !this.client.options.hudHidden && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
            this.lightmapTextureManager.enable();
            this.firstPersonRenderer.renderItem(tickDelta, matrixStack, this.buffers.getEntityVertexConsumers(), this.client.player, this.client.getEntityRenderDispatcher().getLight(this.client.player, tickDelta));
            this.lightmapTextureManager.disable();
         }

         matrix4fStack.popMatrix();
         RenderSystem.applyModelViewMatrix();
         matrixStack.pop();
      }

   }

   public boolean isRenderingHand() {
      return this.renderingHand;
   }
}
