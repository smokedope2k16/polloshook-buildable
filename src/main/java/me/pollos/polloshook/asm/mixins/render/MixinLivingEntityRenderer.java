package me.pollos.polloshook.asm.mixins.render;

import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.rotations.RenderRotations;
import me.pollos.polloshook.asm.ducks.render.ILivingEntityRenderer;
import me.pollos.polloshook.impl.events.render.RenderLivingEntityEvent;
import me.pollos.polloshook.impl.manager.minecraft.movement.RotationManager;
import me.pollos.polloshook.impl.module.render.chams.Chams;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntityRenderer.class})
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements ILivingEntityRenderer {
   @Shadow
   protected M model;
   @Mutable
   @Shadow
   @Final
   protected List<FeatureRenderer<T, M>> features;
   @Unique
   private RenderRotations renderRotations = null;

   @Shadow
   protected abstract RenderLayer getRenderLayer(T var1, boolean var2, boolean var3, boolean var4);

   @Shadow
   protected abstract float getHandSwingProgress(T var1, float var2);

   @Shadow
   protected abstract float getAnimationProgress(T var1, float var2);

   @Shadow
   protected abstract void setupTransforms(T var1, MatrixStack var2, float var3, float var4, float var5, float var6);

   @Shadow
   protected abstract void scale(T var1, MatrixStack var2, float var3);

   @Shadow
   protected abstract float getAnimationCounter(T var1, float var2);

   protected MixinLivingEntityRenderer(Context ctx) {
      super(ctx);
   }

   @Inject(
      method = {"render*"},
      at = {@At("HEAD")}
   )
   public void renderPreHook(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
      Chams CHAMS = (Chams)Managers.getModuleManager().get(Chams.class);
      if (CHAMS.isEnabled() && (Boolean)CHAMS.getSimplePlayers().getValue() && livingEntity instanceof PlayerEntity) {
         GL11C.glEnable(32823);
         GL11C.glPolygonOffset(1.0F, -1100000.0F);
      }

      if (!PollosHook.isFuture()) {
         Freecam FREECAM = (Freecam)Managers.getModuleManager().get(Freecam.class);
         RotationManager ROTATION = Managers.getRotationManager();
         Entity cameraEntity = Interpolation.getRenderEntity();
         boolean equal = FREECAM.isEnabled() || livingEntity == cameraEntity;
         boolean invCheck = Managers.getRotationManager().isInv();
         if (MinecraftClient.getInstance().player != null && livingEntity instanceof ClientPlayerEntity && ROTATION.isRotated() && equal && !invCheck) {
            this.renderRotations = new RenderRotations(livingEntity.getYaw(), livingEntity.getHeadYaw(), livingEntity.getBodyYaw(), livingEntity.getPitch(), livingEntity.prevYaw, livingEntity.prevHeadYaw, livingEntity.prevBodyYaw, livingEntity.prevPitch);
            livingEntity.setYaw(ROTATION.getRenderYaw());
            livingEntity.headYaw = ROTATION.getRotationYawHead();
            livingEntity.bodyYaw = ROTATION.getRenderBodyYaw();
            livingEntity.setPitch(ROTATION.getRenderPitch());
            livingEntity.prevYaw = ROTATION.getPrevYaw();
            livingEntity.prevHeadYaw = ROTATION.getPrevRotationYawHead();
            livingEntity.prevBodyYaw = ROTATION.getPrevRenderBodyYaw();
            livingEntity.prevPitch = ROTATION.getPrevPitch();
         }

      }
   }

   @Inject(
      method = {"render*"},
      at = {@At("TAIL")}
   )
   public void renderPostHook(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
      if (MinecraftClient.getInstance().player != null && livingEntity instanceof ClientPlayerEntity && this.renderRotations != null) {
         livingEntity.setYaw(this.renderRotations.getYaw());
         livingEntity.headYaw = this.renderRotations.getHeadYaw();
         livingEntity.bodyYaw = this.renderRotations.getBodyYaw();
         livingEntity.setPitch(this.renderRotations.getPitch());
         livingEntity.prevYaw = this.renderRotations.getPrevYaw();
         livingEntity.prevHeadYaw = this.renderRotations.getPrevHeadYaw();
         livingEntity.prevBodyYaw = this.renderRotations.getPrevBodyYaw();
         livingEntity.prevPitch = this.renderRotations.getPrevPitch();
         this.renderRotations = null;
      }

      Chams CHAMS = (Chams)Managers.getModuleManager().get(Chams.class);
      if (CHAMS.isEnabled() && (Boolean)CHAMS.getSimplePlayers().getValue() && livingEntity instanceof PlayerEntity) {
         GL11C.glDisable(32823);
         GL11.glPolygonOffset(1.0F, 1100000.0F);
      }

   }

   @Inject(
      method = {"render*"},
      at = {@At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V",
          shift = Shift.BEFORE
      )},
      cancellable = true
  )
  private void renderPreModelHook(T livingEntity, float f, float g, MatrixStack matrixStack, 
                                 VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
      
      RenderLivingEntityEvent pre = new RenderLivingEntityEvent.Pre(livingEntity, matrixStack, vertexConsumerProvider, 
                                     i, this.getTexture(livingEntity), this.model);
      PollosHook.getEventBus().dispatch(pre);
      if (pre.isCanceled()) {
          RenderLivingEntityEvent post = new RenderLivingEntityEvent.Post(livingEntity, matrixStack, vertexConsumerProvider, 
                                         i, this.getTexture(livingEntity), this.model);
          PollosHook.getEventBus().dispatch(post);
          float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
          float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
          float k = j - h;
          float l;
          if (livingEntity.hasVehicle()) {
              Entity var11 = livingEntity.getVehicle();
              if (var11 instanceof LivingEntity) {
                  LivingEntity livingEntity2 = (LivingEntity)var11;
                  h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
                  k = j - h;
                  l = MathHelper.wrapDegrees(k);
                  if (l < -85.0F) {
                      l = -85.0F;
                  }
  
                  if (l >= 85.0F) {
                      l = 85.0F;
                  }
  
                  h = j - l;
                  if (l * l > 2500.0F) {
                      h += l * 0.2F;
                  }
  
                  k = j - h;
              }
          }
  
          float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.getPitch());
          if (LivingEntityRenderer.shouldFlipUpsideDown(livingEntity)) {
              m *= -1.0F;
              k *= -1.0F;
          }
  
          l = this.getAnimationProgress(livingEntity, g);
          float n = 0.0F;
          float o = 0.0F;
          if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
              n = livingEntity.limbAnimator.getSpeed(g);
              o = livingEntity.limbAnimator.getPos(g);
              if (livingEntity.isBaby()) {
                  o *= 3.0F;
              }
  
              if (n > 1.0F) {
                  n = 1.0F;
              }
          }
  
          if (!livingEntity.isSpectator()) {
              for (FeatureRenderer<T, M> featureRenderer : this.features) {
                  featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, o, n, g, l, k, m);
              }
          }
  
          matrixStack.pop();
          super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
          ci.cancel();
      }
  }

   @Inject(
      method = {"render*"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V",
   shift = Shift.AFTER
)}
   )
   private void renderPostModelHook(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
      RenderLivingEntityEvent post = new RenderLivingEntityEvent.Post(livingEntity, matrixStack, vertexConsumerProvider, i, this.getTexture((T) livingEntity), this.model);
      PollosHook.getEventBus().dispatch(post);
   }

   public void renderFast(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
      this.renderFast(livingEntity, f, g, matrixStack, vertexConsumerProvider, i, false);
   }

   public void renderFast(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, boolean setup) {
      matrixStack.push();
      this.model.handSwingProgress = this.getHandSwingProgress((T) livingEntity, g);
      this.model.riding = livingEntity.hasVehicle();
      this.model.child = livingEntity.isBaby();
      float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
      float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
      float k = j - h;
      float l;
      if (livingEntity.hasVehicle()) {
         Entity var11 = livingEntity.getVehicle();
         if (var11 instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)var11;
            h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
            k = j - h;
            l = MathHelper.wrapDegrees(k);
            if (l < -85.0F) {
               l = -85.0F;
            }

            if (l >= 85.0F) {
               l = 85.0F;
            }

            h = j - l;
            if (l * l > 2500.0F) {
               h += l * 0.2F;
            }

            k = j - h;
         }
      }

      float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.getPitch());
      if (LivingEntityRenderer.shouldFlipUpsideDown(livingEntity)) {
         m *= -1.0F;
         k *= -1.0F;
      }

      k = MathHelper.wrapDegrees(k);
      float n;
      if (livingEntity.isInPose(EntityPose.SLEEPING)) {
         Direction direction = livingEntity.getSleepingDirection();
         if (direction != null) {
            n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
            matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0F, (float)(-direction.getOffsetZ()) * n);
         }
      }

      l = livingEntity.getScale();
      if (setup) {
         matrixStack.scale(l, l, l);
      }

      n = getAnimationProgress((T) livingEntity, g);
      if (setup) {
         this.setupTransforms((T) livingEntity, matrixStack, n, h, g, l);
      }

      if (setup) {
         matrixStack.scale(-1.0F, -1.0F, 1.0F);
         this.scale((T) livingEntity, matrixStack, g);
         matrixStack.translate(0.0F, -1.501F, 0.0F);
      }

      float o = 0.0F;
      float p = 0.0F;
      if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
         o = livingEntity.limbAnimator.getSpeed(g);
         p = livingEntity.limbAnimator.getPos(g);
         if (livingEntity.isBaby()) {
            p *= 3.0F;
         }

         if (o > 1.0F) {
            o = 1.0F;
         }
      }

      this.model.animateModel((T) livingEntity, p, o, g);
      this.model.setAngles((T) livingEntity, p, o, n, k, m);
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      boolean bl = !livingEntity.isInvisible();
      boolean bl2 = !bl && !livingEntity.isInvisibleTo(minecraftClient.player);
      boolean bl3 = minecraftClient.hasOutline(livingEntity);
      RenderLayer renderLayer = this.getRenderLayer((T) livingEntity, bl, bl2, bl3);
      if (renderLayer != null) {
         VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
         int q = LivingEntityRenderer.getOverlay(livingEntity, this.getAnimationCounter((T) livingEntity, g));
         this.model.render(matrixStack, vertexConsumer, i, q, bl2 ? 654311423 : -1);
      }

      matrixStack.pop();
   }
}
