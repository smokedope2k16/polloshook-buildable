package me.pollos.polloshook.impl.module.render.chams;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderEndCrystalEvent;
import me.pollos.polloshook.impl.module.render.chams.util.ChamsType;
import me.pollos.polloshook.impl.module.render.chams.util.EntityRenderRunnable;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class ListenerPostEndCrystalRender extends ModuleListener<Chams, RenderEndCrystalEvent.Post> {
   private static final float SINE_45_DEGREES = (float)Math.sin(0.7853981633974483D);

   public ListenerPostEndCrystalRender(Chams module) {
      super(module, RenderEndCrystalEvent.Post.class);
   }

   public void call(RenderEndCrystalEvent.Post event) {
      if (((Chams)this.module).crystals.getValue() != ChamsType.FILL && ((Chams)this.module).crystals.getValue() != ChamsType.OFF) {
         EndCrystalEntity endCrystal = event.getEndCrystal();
         ((Chams)this.module).renderings.add(new EntityRenderRunnable(endCrystal, () -> {
            MatrixStack matrix = event.getMatrixStack();
            float tickDelta = event.getTickDelta();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.disableDepthTest();
            MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
            Framebuffer framebuffer = mc.getFramebuffer();
            MSAAFramebuffer.start(smoothBuffer, framebuffer);
            matrix.push();
            matrix.push();
            matrix.scale(2.0F, 2.0F, 2.0F);
            matrix.translate(0.0F, -0.5F, 0.0F);
            if (endCrystal.shouldShowBottom()) {
               this.render(event.getBottom(), event);
            }

            float ticks = (float)endCrystal.endCrystalAge + tickDelta;
            float bounceSpeed = this.getYOffset(ticks);
            float spinSpeedValue = (Float)((Chams)this.module).rotateSpeed.getValue();
            float spinSpeed = ticks * 3.0F * spinSpeedValue;
            float factor = 0.875F;
            matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinSpeed));
            matrix.translate(0.0F, 1.5F + bounceSpeed / 2.0F, 0.0F);
            matrix.multiply((new Quaternionf()).setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
            this.render(event.getFrame(), event);
            matrix.scale(factor, factor, factor);
            matrix.multiply((new Quaternionf()).setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
            matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinSpeed));
            this.render(event.getFrame(), event);
            matrix.scale(factor, factor, factor);
            matrix.multiply((new Quaternionf()).setAngleAxis(1.0471976F, SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
            matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinSpeed));
            this.render(event.getCore(), event);
            matrix.pop();
            matrix.pop();
            MSAAFramebuffer.end(smoothBuffer, framebuffer);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
         }));
      }
   }

   private void render(ModelPart modelPart, RenderEndCrystalEvent event) {
      MatrixStack matrix = event.getMatrixStack();
      matrix.push();
      matrix.scale(0.0625F, 0.0625F, 0.0625F);
      modelPart.forEachCuboid(matrix, (entry, path, i, cuboid) -> {
         RenderMethods.drawOutlineBox(matrix, cuboid, ((Chams)this.module).wireColor.getColor(), (Float)((Chams)this.module).lineWidth.getValue());
      });
      matrix.pop();
   }

   private float getYOffset(float ticks) {
      float g = MathHelper.sin(ticks * 0.2F * (Float)((Chams)this.module).bounceSpeed.getValue()) / 2.0F + 0.5F;
      g = (g * g + g) * ((Boolean)((Chams)this.module).legacyHeight.getValue() ? 0.2F : 0.4F);
      return g - 1.4F;
   }
}
