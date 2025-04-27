package me.pollos.polloshook.impl.module.render.chams;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderEndCrystalEvent;
import me.pollos.polloshook.impl.module.render.chams.util.ChamsType;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class ListenerPreEndCrystalRender extends ModuleListener<Chams, RenderEndCrystalEvent.Pre> {
   private static final float SINE_45_DEGREES = (float)Math.sin(0.7853981633974483D);
   private static final Identifier TEXTURE = Identifier.of("textures/entity/end_crystal/end_crystal.png");
   private static final RenderLayer END_CRYSTAL;

   public ListenerPreEndCrystalRender(Chams module) {
      super(module, RenderEndCrystalEvent.Pre.class);
   }

   public void call(RenderEndCrystalEvent.Pre event) {
      EndCrystalEntity endCrystal = event.getEndCrystal();
      Tessellator tessellator = Tessellator.getInstance();
      MatrixStack matrix = event.getMatrixStack();
      VertexConsumerProvider vertexConsumerProvider = event.getVertexConsumerProvider();
      int light = event.getLight();
      float tickDelta = event.getTickDelta();
      matrix.push();
      matrix.push();
      matrix.scale(2.0F, 2.0F, 2.0F);
      matrix.translate(0.0F, -0.5F, 0.0F);
      int overlay = OverlayTexture.DEFAULT_UV;
      if (endCrystal.shouldShowBottom()) {
         this.renderChams(tessellator, event.getBottom(), event, overlay);
      }

      float ticks = (float)endCrystal.endCrystalAge + tickDelta;
      float bounceSpeed = this.getYOffset(ticks);
      float spinSpeedValue = (Float)((Chams)this.module).rotateSpeed.getValue();
      float spinSpeed = ticks * 3.0F * spinSpeedValue;
      float factor = 0.875F;
      matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinSpeed));
      matrix.translate(0.0F, 1.5F + bounceSpeed / 2.0F, 0.0F);
      matrix.multiply((new Quaternionf()).setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
      this.renderChams(tessellator, event.getFrame(), event, overlay);
      matrix.scale(factor, factor, factor);
      matrix.multiply((new Quaternionf()).setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
      matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinSpeed));
      this.renderChams(tessellator, event.getFrame(), event, overlay);
      matrix.scale(factor, factor, factor);
      matrix.multiply((new Quaternionf()).setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
      matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinSpeed));
      this.renderChams(tessellator, event.getCore(), event, overlay);
      matrix.pop();
      matrix.pop();
      BlockPos blockPos = endCrystal.getBeamTarget();
      if (blockPos != null) {
         float m = (float)blockPos.getX() + 0.5F;
         float n = (float)blockPos.getY() + 0.5F;
         float o = (float)blockPos.getZ() + 0.5F;
         float p = (float)((double)m - endCrystal.getX());
         float q = (float)((double)n - endCrystal.getY());
         float r = (float)((double)o - endCrystal.getZ());
         matrix.translate(p, q, r);
         EnderDragonEntityRenderer.renderCrystalBeam(-p, -q + bounceSpeed, -r, tickDelta, endCrystal.endCrystalAge, matrix, vertexConsumerProvider, light);
      }

      event.setCanceled(true);
   }

   private void renderChams(Tessellator tessellator, ModelPart modelPart, RenderEndCrystalEvent event, int overlay) {
      if (((Chams)this.module).crystals.getValue() != ChamsType.WIRE_FRAME && ((Chams)this.module).crystals.getValue() != ChamsType.OFF) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         if ((Boolean)((Chams)this.module).xqz.getValue()) {
            RenderMethods.color(((Chams)this.module).xqzColor.getColor().getRGB());
            RenderSystem.depthMask(false);
            RenderSystem.disableDepthTest();
            this.chams(tessellator, modelPart, event, overlay);
         }

         RenderMethods.color(((Chams)this.module).visibleColor.getColor().getRGB());
         RenderSystem.depthMask(true);
         RenderSystem.enableDepthTest();
         this.chams(tessellator, modelPart, event, overlay);
         RenderMethods.resetColor();
         RenderSystem.disableBlend();
         RenderSystem.enableCull();
      } else {
         VertexConsumer vertexConsumer = event.getVertexConsumerProvider().getBuffer(END_CRYSTAL);
         modelPart.render(event.getMatrixStack(), vertexConsumer, event.getLight(), overlay);
      }
   }

   private void chams(Tessellator tessellator, ModelPart modelPart, RenderEndCrystalEvent event, int overlay) {
      RenderSystem.setShader(GameRenderer::getPositionProgram);
      BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION);
      modelPart.render(event.getMatrixStack(), bufferBuilder, event.getLight(), overlay);
      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
   }

   private float getYOffset(float ticks) {
      float g = MathHelper.sin(ticks * 0.2F * (Float)((Chams)this.module).bounceSpeed.getValue()) / 2.0F + 0.5F;
      g = (g * g + g) * ((Boolean)((Chams)this.module).legacyHeight.getValue() ? 0.2F : 0.4F);
      return g - 1.4F;
   }

   static {
      END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
   }
}
