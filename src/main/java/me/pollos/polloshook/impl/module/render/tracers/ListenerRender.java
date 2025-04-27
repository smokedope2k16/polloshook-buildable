package me.pollos.polloshook.impl.module.render.tracers;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.asm.mixins.render.IGameRenderer;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.render.tracers.mode.ColorMode;
import me.pollos.polloshook.impl.module.render.tracers.mode.TracersBone;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends SafeModuleListener<Tracers, RenderEvent> {
   public ListenerRender(Tracers module) {
      super(module, RenderEvent.class);
   }

   public void safeCall(RenderEvent event) {
      if (!mc.world.getPlayers().isEmpty()) {
         Iterator var2 = mc.world.getPlayers().iterator();

         while(true) {
            PlayerEntity player;
            do {
               do {
                  do {
                     do {
                        if (!var2.hasNext()) {
                           return;
                        }

                        player = (PlayerEntity)var2.next();
                     } while(player.isSpectator());
                  } while(player == Interpolation.getRenderEntity());
               } while((Boolean)((Tracers)this.module).offscreen.getValue() && Interpolation.isVisible(Interpolation.interpolateAxis(player.getVisibilityBoundingBox()), event));
            } while((Boolean)((Tracers)this.module).yLevel.getValue() && player.getY() > (double)(Integer)((Tracers)this.module).yDistance.getValue());

            MatrixStack matrixStack = event.getMatrixStack();
            matrixStack.push();
            RenderMethods.enable3D();
            MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
            Framebuffer framebuffer = mc.getFramebuffer();
            MSAAFramebuffer.start(smoothBuffer, framebuffer);
            Tessellator tessellator = Tessellator.getInstance();
            RenderSystem.lineWidth((Float)((Tracers)this.module).lineWidth.getValue());
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            RenderSystem.defaultBlendFunc();
            BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.LINES);
            Vec3d rotateYaw = new Vec3d(0.0D, 0.0D, 1.0D);
            Camera camera = mc.gameRenderer.getCamera();
            Vec3d offset = ((Tracers)this.module).getDifference(mc.cameraEntity);
            rotateYaw = rotateYaw.rotateX((float)(-Math.toRadians((double)camera.getPitch())));
            rotateYaw = rotateYaw.rotateY((float)(-Math.toRadians((double)camera.getYaw())));
            rotateYaw = rotateYaw.add(mc.cameraEntity.getEyePos());
            rotateYaw = rotateYaw.subtract(offset);
            Vec3d target = Interpolation.interpolateEntity(player);
            Color color;
            if (Managers.getFriendManager().isFriend(player)) {
               Color tempColor = Colours.get().getFriendColor();
               color = new Color((float)tempColor.getRed() / 255.0F, (float)tempColor.getGreen() / 255.0F, (float)tempColor.getBlue() / 255.0F, (float)(Integer)((Tracers)this.module).opacity.getValue() / 100.0F);
            } else {
               switch((ColorMode)((Tracers)this.module).color.getValue()) {
               case AUTO:
                  float distance = Interpolation.getRenderEntity().distanceTo(player);
                  float red;
                  if (distance >= 60.0F) {
                     red = 120.0F;
                  } else {
                     red = distance + distance;
                  }

                  Color tempColor = ColorUtil.toColor(red, 100.0F, 50.0F, (float)(Integer)((Tracers)this.module).opacity.getValue() / 100.0F);
                  color = new Color((float)tempColor.getRed() / 255.0F, (float)tempColor.getGreen() / 255.0F, (float)tempColor.getBlue() / 255.0F, (float)tempColor.getAlpha() / 255.0F);
                  break;
               case CUSTOM:
                  color = ((Tracers)this.module).customColor.getColor();
                  break;
               default:
                  color = new Color(-1);
               }
            }

            switch((TracersBone)((Tracers)this.module).bone.getValue()) {
            case HEAD:
               target = target.add(new Vec3d(0.0D, (double)(player.getHeight() - 0.18F), 0.0D));
               break;
            case CHEST:
               target = target.add(new Vec3d(0.0D, (double)(player.getHeight() / 2.0F), 0.0D));
               break;
            case PENIS:
               target = target.add(new Vec3d(0.0D, (double)(player.getHeight() - 1.1F), 0.0D));
            }

            if ((Boolean)mc.options.getBobView().getValue()) {
               ((IGameRenderer)mc.gameRenderer).invokeBobView(matrixStack, event.getTickDelta());
            }

            RenderMethods.drawLine(matrixStack, bufferBuilder, (float)rotateYaw.x, (float)rotateYaw.y, (float)rotateYaw.z, (float)target.x, (float)target.y, (float)target.z, color);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            MSAAFramebuffer.end(smoothBuffer, framebuffer);
            RenderMethods.disable3D();
            matrixStack.pop();
         }
      }
   }
}
