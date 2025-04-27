package me.pollos.polloshook.impl.module.movement.holesnap;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.block.HoleUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.obj.hole.Hole;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends ModuleListener<HoleSnap, RenderEvent> {
   public ListenerRender(HoleSnap module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if (((HoleSnap)this.module).hole != null) {
         MatrixStack stack = event.getMatrixStack();
         stack.push();
         RenderMethods.enable3D();
         MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
         Framebuffer framebuffer = mc.getFramebuffer();
         MSAAFramebuffer.start(smoothBuffer, framebuffer);
         Tessellator tessellator = Tessellator.getInstance();
         RenderSystem.lineWidth(1.6F);
         RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
         RenderSystem.defaultBlendFunc();
         BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.LINES);
         Vec3d rotateYaw = new Vec3d(0.0D, 0.0D, 1.0D);
         Camera camera = mc.gameRenderer.getCamera();
         Vec3d offset = this.getDifference(mc.cameraEntity);
         rotateYaw = rotateYaw.rotateX((float)(-Math.toRadians((double)camera.getPitch())));
         rotateYaw = rotateYaw.rotateY((float)(-Math.toRadians((double)camera.getYaw())));
         rotateYaw = rotateYaw.add(mc.cameraEntity.getEyePos());
         rotateYaw = rotateYaw.subtract(offset);
         Hole hole = ((HoleSnap)this.module).hole;
         double x = HoleUtil.getCenter(hole).x - Interpolation.getRenderPosX();
         double y = HoleUtil.getCenter(hole).y - Interpolation.getRenderPosY();
         double z = HoleUtil.getCenter(hole).z - Interpolation.getRenderPosZ();
         Vec3d target = new Vec3d(x, y, z);
         RenderMethods.drawLine(stack, bufferBuilder, (float)rotateYaw.x, (float)rotateYaw.y, (float)rotateYaw.z, (float)target.x, (float)target.y, (float)target.z, Colours.get().getColourCustomAlpha(125));
         BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
         MSAAFramebuffer.end(smoothBuffer, framebuffer);
         RenderMethods.disable3D();
         stack.pop();
      }
   }

   private Vec3d getDifference(Entity entity) {
      Vec3d interpolated = Interpolation.interpolateEntity(entity);
      return entity.getPos().subtract(interpolated);
   }
}
