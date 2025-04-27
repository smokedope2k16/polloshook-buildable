package me.pollos.polloshook.api.minecraft.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.rotations.RenderRotations;
import me.pollos.polloshook.asm.ducks.render.ILivingEntityRenderer;
import me.pollos.polloshook.impl.manager.minecraft.movement.RotationManager;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class RenderMethods implements Minecraftable {
   public static void color(Color c) {
      float alpha = (float)(c.getAlpha() >> 24 & 255) / 255.0F;
      float red = (float)(c.getRed() >> 16 & 255) / 255.0F;
      float green = (float)(c.getGreen() >> 8 & 255) / 255.0F;
      float blue = (float)(c.getBlue() & 255) / 255.0F;
      RenderSystem.setShaderColor(red, green, blue, alpha);
   }

   public static void color(int hex) {
      float alpha = (float)(hex >> 24 & 255) / 255.0F;
      float red = (float)(hex >> 16 & 255) / 255.0F;
      float green = (float)(hex >> 8 & 255) / 255.0F;
      float blue = (float)(hex & 255) / 255.0F;
      RenderSystem.setShaderColor(red, green, blue, alpha);
   }

   public static void resetColor() {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void enable3D() {
      Render2DMethods.enable2D();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.disableCull();
   }

   public static void disable3D() {
      Render2DMethods.disable2D();
      RenderSystem.enableDepthTest();
      RenderSystem.depthMask(true);
      RenderSystem.enableCull();
   }

   public static void drawBox(MatrixStack matrixStack, Box bb, Color color) {
      Matrix4f matrix = matrixStack.peek().getPositionMatrix();
      Tessellator tessellator = RenderSystem.renderThreadTesselator();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.minY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.maxX, (float)bb.maxY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.minZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.minY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.maxZ).color(color.getRGB());
      bufferBuilder.vertex(matrix, (float)bb.minX, (float)bb.maxY, (float)bb.minZ).color(color.getRGB());
      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
   }

   public static void drawCrossBox(MatrixStack matrixStack, Box bb, Color color, float lineWidth, boolean top) {
      Tessellator tessellator = Tessellator.getInstance();
      RenderSystem.lineWidth(lineWidth);
      RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
      RenderSystem.defaultBlendFunc();
      BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.LINES);
      float maxX = (float)bb.maxX;
      float maxY = (float)bb.maxY;
      float maxZ = (float)bb.maxZ;
      float minX = (float)bb.minX;
      float minY = (float)bb.minY;
      float minZ = (float)bb.minZ;
      drawLine(matrixStack, bufferBuilder, maxX, maxY, maxZ, maxX, minY, minZ, color);
      drawLine(matrixStack, bufferBuilder, minX, maxY, minZ, minX, minY, maxZ, color);
      drawLine(matrixStack, bufferBuilder, minX, minY, minZ, minX, maxY, maxZ, color);
      drawLine(matrixStack, bufferBuilder, minX, minY, minZ, maxX, maxY, minZ, color);
      drawLine(matrixStack, bufferBuilder, minX, maxY, minZ, maxX, minY, minZ, color);
      drawLine(matrixStack, bufferBuilder, maxX, maxY, minZ, maxX, minY, maxZ, color);
      drawLine(matrixStack, bufferBuilder, maxX, maxY, maxZ, minX, minY, maxZ, color);
      drawLine(matrixStack, bufferBuilder, maxX, minY, maxZ, minX, maxY, maxZ, color);
      drawLine(matrixStack, bufferBuilder, minX, minY, minZ, maxX, minY, maxZ, color);
      drawLine(matrixStack, bufferBuilder, maxX, minY, minZ, minX, minY, maxZ, color);
      if (top) {
         drawLine(matrixStack, bufferBuilder, maxX, maxY, minZ, minX, maxY, maxZ, color);
         drawLine(matrixStack, bufferBuilder, minX, maxY, minZ, maxX, maxY, maxZ, color);
      }

      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
   }

   public static void drawOutlineBox(MatrixStack stack, Box box, Color color, float lineWidth) {
      Tessellator tessellator = Tessellator.getInstance();
      RenderSystem.lineWidth(lineWidth);
      RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
      RenderSystem.defaultBlendFunc();
      BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.LINES);
      WorldRenderer.drawBox(stack, bufferBuilder, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
   }

   public static void drawOutlineBox(MatrixStack stack, Cuboid box, Color color, float lineWidth) {
      Tessellator tessellator = Tessellator.getInstance();
      RenderSystem.lineWidth(lineWidth);
      RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
      RenderSystem.defaultBlendFunc();
      BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.LINES);
      WorldRenderer.drawBox(stack, bufferBuilder, (double)box.minX, (double)box.minY, (double)box.minZ, (double)box.maxX, (double)box.maxY, (double)box.maxZ, (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
   }

   public static void drawLine(MatrixStack matrices, VertexConsumer buffer, Vec3d firstVec, Vec3d secondVec, Color lineColor) {
      drawLine(matrices, buffer, (float)firstVec.x, (float)firstVec.y, (float)firstVec.z, (float)secondVec.x, (float)secondVec.y, (float)secondVec.z, lineColor);
   }

   public static void drawLine(MatrixStack matrices, VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, Color lineColor) {
      Matrix4f model = matrices.peek().getPositionMatrix();
      Vector3f normalVec = normalize(x1, y1, z1, x2, y2, z2);
      buffer.vertex(model, x1, y1, z1).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(matrices.peek(), normalVec.x(), normalVec.y(), normalVec.z());
      buffer.vertex(model, x2, y2, z2).color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha()).normal(matrices.peek(), normalVec.x(), normalVec.y(), normalVec.z());
   }

   private static Vector3f normalize(float x1, float y1, float z1, float x2, float y2, float z2) {
      double dx = (double)(x2 - x1);
      double dy = (double)(y2 - y1);
      double dz = (double)(z2 - z1);
      double invMag = 1.0D / Math.sqrt(dx * dx + dy * dy + dz * dz);
      float nx = (float)(dx * invMag);
      float ny = (float)(dy * invMag);
      float nz = (float)(dz * invMag);
      return new Vector3f(nx, ny, nz);
   }

   private static RenderRotations rotatePre(LivingEntity livingEntity) {
      Freecam FREECAM = (Freecam)Managers.getModuleManager().get(Freecam.class);
      RotationManager ROTATION = Managers.getRotationManager();
      Entity cameraEntity = Interpolation.getRenderEntity();
      boolean equal = FREECAM.isEnabled() || livingEntity == cameraEntity;
      if (mc.player != null && livingEntity == mc.player && ROTATION.isRotated() && equal) {
         RenderRotations renderRotations = new RenderRotations(livingEntity.getYaw(), livingEntity.getHeadYaw(), livingEntity.getBodyYaw(), livingEntity.getPitch(), livingEntity.prevYaw, livingEntity.prevHeadYaw, livingEntity.prevBodyYaw, livingEntity.prevPitch);
         livingEntity.setYaw(ROTATION.getRenderYaw());
         livingEntity.headYaw = ROTATION.getRotationYawHead();
         livingEntity.bodyYaw = ROTATION.getRenderBodyYaw();
         livingEntity.setPitch(ROTATION.getRenderPitch());
         livingEntity.prevYaw = ROTATION.getPrevYaw();
         livingEntity.prevHeadYaw = ROTATION.getPrevRotationYawHead();
         livingEntity.prevBodyYaw = ROTATION.getPrevRenderBodyYaw();
         livingEntity.prevPitch = ROTATION.getPrevPitch();
         return renderRotations;
      } else {
         return null;
      }
   }

   private static void rotatePost(LivingEntity livingEntity, RenderRotations renderRotations) {
      if (mc.player != null && livingEntity == mc.player && renderRotations != null) {
         livingEntity.setYaw(renderRotations.getYaw());
         livingEntity.headYaw = renderRotations.getHeadYaw();
         livingEntity.bodyYaw = renderRotations.getBodyYaw();
         livingEntity.setPitch(renderRotations.getPitch());
         livingEntity.prevYaw = renderRotations.getPrevYaw();
         livingEntity.prevHeadYaw = renderRotations.getPrevHeadYaw();
         livingEntity.prevBodyYaw = renderRotations.getPrevBodyYaw();
         livingEntity.prevPitch = renderRotations.getPrevPitch();
      }

   }

   public static void drawEntity(MatrixStack matrixStack, PlayerEntity player, float tickDelta, VertexConsumerProvider vertexConsumerProvider) {
      drawEntity(matrixStack, player, tickDelta, vertexConsumerProvider, true);
   }

   public static void drawEntity(MatrixStack matrixStack, PlayerEntity player, float tickDelta, VertexConsumerProvider vertexConsumerProvider, boolean correctPos) {
      if (!Managers.getRotationManager().isInv()) {
         double x = MathHelper.lerp((double)tickDelta, player.lastRenderX, player.getX());
         double y = MathHelper.lerp((double)tickDelta, player.lastRenderY, player.getY());
         double z = MathHelper.lerp((double)tickDelta, player.lastRenderZ, player.getZ());
         float yaw = MathHelper.lerp(tickDelta, player.prevYaw, player.getYaw());
         EntityRenderer<? super PlayerEntity> entityRenderer = mc.getEntityRenderDispatcher().getRenderer(player);
         double interpX = x - Interpolation.getRenderPosX();
         double interpY = y - Interpolation.getRenderPosY();
         double interpZ = z - Interpolation.getRenderPosZ();
         matrixStack.push();
         RenderRotations rotations = null;
         if (correctPos) {
            matrixStack.translate(interpX, interpY, interpZ);
            rotations = rotatePre(player);
         }

         ((ILivingEntityRenderer)entityRenderer).renderFast(player, yaw, tickDelta, matrixStack, vertexConsumerProvider, 666666, correctPos);
         rotatePost(player, rotations);
         matrixStack.pop();
      }
   }
}
