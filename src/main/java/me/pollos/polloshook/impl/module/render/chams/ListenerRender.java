package me.pollos.polloshook.impl.module.render.chams;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.asm.ducks.entity.IClientPlayerEntity;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.render.chams.util.ChamsType;
import me.pollos.polloshook.impl.module.render.chams.util.EntityRenderRunnable;
import me.pollos.polloshook.impl.module.render.chams.util.TotemPopPlayer;
import me.pollos.polloshook.impl.module.render.skeleton.util.CacheConsumerProvider;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends ModuleListener<Chams, RenderEvent> {
   public ListenerRender(Chams module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      ((Chams)this.module).damageColor(((Chams)this.module).getDamage());
      MatrixStack matrix = event.getMatrixStack();
      List<TotemPopPlayer> deadNiggas = new ArrayList();
      Iterator var4;
      TotemPopPlayer popPlayer;
      if (!((Chams)this.module).popped.isEmpty()) {
         var4 = (new ArrayList(((Chams)this.module).popped)).iterator();

         while(var4.hasNext()) {
            popPlayer = (TotemPopPlayer)var4.next();
            double alpha = ColorUtil.fade((double)popPlayer.time(), (double)((Float)((Chams)this.module).fadeTime.getValue() * 1000.0F)) / 255.0D;
            if (!(alpha > 0.0D)) {
               deadNiggas.add(popPlayer);
            }
         }
      }

      ((Chams)this.module).popped.removeAll(deadNiggas);
      if (((Chams)this.module).players.getParent().isVisible() && (((Chams)this.module).players.getValue() == ChamsType.BOTH || ((Chams)this.module).players.getValue() == ChamsType.WIRE_FRAME)) {
         var4 = mc.world.getPlayers().iterator();

         label65:
         while(true) {
            AbstractClientPlayerEntity player;
            do {
               if (!var4.hasNext()) {
                  break label65;
               }

               player = (AbstractClientPlayerEntity)var4.next();
            } while(player instanceof ClientPlayerEntity && mc.options.getPerspective() == Perspective.THIRD_PERSON_BACK && ((IClientPlayerEntity)player).getIsCamera());

            CacheConsumerProvider provider = (CacheConsumerProvider)((Chams)this.module).vertexes.get(player);
            if (provider != null) {
               RenderSystem.enableBlend();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableCull();
               RenderSystem.depthMask(false);
               RenderSystem.disableDepthTest();
               MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
               Framebuffer framebuffer = mc.getFramebuffer();
               MSAAFramebuffer.start(smoothBuffer, framebuffer);
               matrix.push();
               RenderSystem.lineWidth((Float)((Chams)this.module).lineWidth.getValue());
               RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
               BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.LINES);
               List<Vec3d[]> positions = provider.getCacheConsumer().positions;
               boolean render = this.renderChina(positions, matrix, builder, ((Chams)this.module).getWireColor(player));
               if (render) {
                  BufferRenderer.drawWithGlobalProgram(builder.end());
               }

               matrix.pop();
               MSAAFramebuffer.end(smoothBuffer, framebuffer);
               RenderSystem.depthMask(true);
               RenderSystem.enableDepthTest();
               RenderSystem.disableBlend();
               RenderSystem.enableCull();
            }
         }
      }

      var4 = (new ArrayList(((Chams)this.module).renderings)).iterator();

      double y;
      double d;
      double e;
      double x;
      while(var4.hasNext()) {
         EntityRenderRunnable action = (EntityRenderRunnable)var4.next();
         MatrixStack matrices = event.getMatrixStack();
         Entity entity = action.entity();
         float tickDelta = event.getTickDelta();
     
         double entityRenderX = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
         double entityRenderY = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
         double entityRenderZ = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ()); 
     
         Vec3d camera = Interpolation.getCameraPos();
     
         double translateX = entityRenderX - camera.getX();
         double translateY = entityRenderY - camera.getY(); 
         double translateZ = entityRenderZ - camera.getZ(); 
     
         matrices.push();
     
         matrices.translate(translateX, translateY, translateZ); 
         action.runnable().run();
     
         matrices.pop();
     }

     ((Chams)this.module).renderings.clear();
     ((Chams)this.module).vertexes.clear();
     if ((Boolean)((Chams)this.module).totems.getValue()) {
         var4 = (new ArrayList(((Chams)this.module).popped)).iterator();
     
         while(var4.hasNext()) {
             popPlayer = (TotemPopPlayer)var4.next();
             RenderSystem.enableBlend();
             RenderSystem.defaultBlendFunc();
             RenderSystem.disableCull();
             RenderSystem.depthMask(false);
             RenderSystem.disableDepthTest();
             MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
             Framebuffer framebuffer = mc.getFramebuffer();
             MSAAFramebuffer.start(smoothBuffer, framebuffer);
             matrix.push();
             PlayerEntity player = popPlayer.player();
             PlayerEntityModel<?> model = popPlayer.model();
             float tickDelta = event.getTickDelta();
             x = MathHelper.lerp((double)tickDelta, player.lastRenderX, player.getX());
             y = MathHelper.lerp((double)tickDelta, player.lastRenderY, player.getY());
             double z = MathHelper.lerp((double)tickDelta, player.lastRenderZ, player.getZ());
             y += (double)((Float)((Chams)this.module).elevate.getValue() / 100.0F * (float)(System.currentTimeMillis() - popPlayer.time()) / (Float)((Chams)this.module).fadeTime.getValue());
             Vec3d camera = Interpolation.getCameraPos();
             d = x - camera.getX();
             e = y - camera.getY();
             double f = z - camera.getZ();
             matrix.translate(d, e, f);
             this.setup(matrix, player);
             int alpha = (int)ColorUtil.fade((double)popPlayer.time(), (double)((Float)((Chams)this.module).fadeTime.getValue() * 1000.0F + 50.0F));
             Color color = Managers.getFriendManager().isFriend(popPlayer.player().getName().getString()) ? Colours.get().getFriendColor() : ((Chams)this.module).totemColor.getColor();
             alpha = (int)((float)alpha * (Float)((Chams)this.module).alphaFactor.getValue());
     
             final int effectiveAlpha = alpha;
     
             List<ModelPart> parts = new ArrayList();
             parts.add(model.head);
             parts.add(model.body);
             parts.add(model.leftArm);
             parts.add(model.rightArm);
             parts.add(model.leftLeg);
             parts.add(model.rightLeg);
             int boxAlpha = (int)((double)effectiveAlpha * 0.25D); 
             parts.forEach((modelPart) -> {
                 this.renderBoxes(matrix, modelPart, ColorUtil.changeAlpha(color, boxAlpha));
             });
             parts.forEach((modelPart) -> {
                 this.renderLines(matrix, modelPart, ColorUtil.changeAlpha(color, effectiveAlpha));
             });
             matrix.translate(-camera.getX(), -camera.getY(), -camera.getZ());
             matrix.pop();
             MSAAFramebuffer.end(smoothBuffer, framebuffer);
             RenderSystem.depthMask(true);
             RenderSystem.enableDepthTest();
             RenderSystem.disableBlend();
             RenderSystem.enableCull();
         }
     }
   }

   private boolean renderChina(List<Vec3d[]> positions, MatrixStack matrixStack, VertexConsumer provider, Color color) {
      boolean render = false;
      List<Vec3d[]> rendered = new ArrayList();
      Iterator var7 = positions.iterator();

      while(var7.hasNext()) {
         Vec3d[] arr = (Vec3d[])var7.next();

         for(int i = 0; i < 4; ++i) {
            Vec3d[] line = new Vec3d[]{arr[i], arr[(i + 1) % 4]};
            if (!this.contains(rendered, line)) {
               RenderMethods.drawLine(matrixStack, provider, line[0], line[1], color);
               rendered.add(line);
               render = true;
            }
         }
      }

      return render;
   }

   private boolean contains(List<Vec3d[]> lines, Vec3d[] line) {
      Iterator var3 = lines.iterator();

      Vec3d[] vec3d;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         vec3d = (Vec3d[])var3.next();
      } while(vec3d[0] != line[0] || vec3d[1] != line[1]);

      return true;
   }

   private void renderBoxes(MatrixStack matrix, ModelPart modelPart, Color color) {
      matrix.push();
      matrix.scale(modelPart.xScale, modelPart.yScale, modelPart.zScale);
      matrix.scale(0.0625F, 0.0625F, 0.0625F);
      float pivotX = modelPart.pivotX;
      float pivotY = modelPart.pivotY;
      float pivotZ = modelPart.pivotZ;
      matrix.translate(pivotX, pivotY, pivotZ);
      matrix.translate(-(pivotX / 16.0F), -(pivotY / 16.0F), -(pivotZ / 16.0F));
      modelPart.forEachCuboid(matrix, (entry, path, i, cuboid) -> {
         Box bb = new Box((double)cuboid.minX, (double)cuboid.minY, (double)cuboid.minZ, (double)cuboid.maxX, (double)cuboid.maxY, (double)cuboid.maxZ);
         RenderMethods.drawBox(matrix, bb, color);
      });
      matrix.pop();
   }

   private void renderLines(MatrixStack matrix, ModelPart modelPart, Color color) {
      matrix.push();
      matrix.scale(modelPart.xScale, modelPart.yScale, modelPart.zScale);
      matrix.scale(0.0625F, 0.0625F, 0.0625F);
      float pivotX = modelPart.pivotX;
      float pivotY = modelPart.pivotY;
      float pivotZ = modelPart.pivotZ;
      matrix.translate(pivotX, pivotY, pivotZ);
      matrix.translate(-(pivotX / 16.0F), -(pivotY / 16.0F), -(pivotZ / 16.0F));
      modelPart.forEachCuboid(matrix, (entry, path, i, cuboid) -> {
         RenderMethods.drawOutlineBox(matrix, cuboid, color, (Float)((Chams)this.module).lineWidth.getValue());
      });
      matrix.pop();
   }

   private void setup(MatrixStack matrixStack, PlayerEntity player) {
      float g = 1.0F;
      float h = MathHelper.lerpAngleDegrees(1.0F, player.prevBodyYaw, player.bodyYaw);
      float j = MathHelper.lerpAngleDegrees(1.0F, player.prevHeadYaw, player.headYaw);
      if (player.hasVehicle()) {
         Entity var11 = player.getVehicle();
         if (var11 instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)var11;
            h = MathHelper.lerpAngleDegrees(1.0F, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
            float k = j - h;
            float l = MathHelper.wrapDegrees(k);
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
         }
      }

      if (player.isInPose(EntityPose.SLEEPING)) {
         Direction direction = player.getSleepingDirection();
         if (direction != null) {
            float n = player.getEyeHeight(EntityPose.STANDING) - 0.1F;
            matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0F, (float)(-direction.getOffsetZ()) * n);
         }
      }

      this.setupTransforms(player, matrixStack, h);
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
      matrixStack.translate(0.0F, -1.501F, 0.0F);
   }

   protected boolean isShaking(LivingEntity entity) {
      return entity.isFrozen();
   }

   private static float getYaw(Direction direction) {
      float var10000;
      switch(direction) {
      case EAST:
         var10000 = 90.0F;
         break;
      case WEST:
         var10000 = 270.0F;
         break;
      case SOUTH:
         var10000 = 180.0F;
         break;
      default:
         var10000 = 0.0F;
      }

      return var10000;
   }

   protected void setupTransforms(LivingEntity entity, MatrixStack matrices, float bodyYaw) {
      if (this.isShaking(entity)) {
         bodyYaw += (float)(Math.cos((double)entity.age * 3.25D) * 3.141592653589793D * 0.4000000059604645D);
      }

      if (!entity.isInPose(EntityPose.SLEEPING)) {
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
      }

      if (entity.deathTime > 0) {
         float f = ((float)entity.deathTime + 1.0F - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 90.0F));
      } else if (entity.isUsingRiptide()) {
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F - entity.getPitch()));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((float)entity.age + 1.0F) * -75.0F));
      } else if (entity.isInPose(EntityPose.SLEEPING)) {
         Direction direction = entity.getSleepingDirection();
         float g = direction != null ? getYaw(direction) : bodyYaw;
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
      } else if (LivingEntityRenderer.shouldFlipUpsideDown(entity)) {
         matrices.translate(0.0F, entity.getHeight() + 0.1F, 0.0F);
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
      }

   }
}
