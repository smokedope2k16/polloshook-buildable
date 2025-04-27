package me.pollos.polloshook.impl.module.combat.autocrystal;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.RenderMode;
import me.pollos.polloshook.impl.module.combat.autocrystal.util.CrystalRenderPos;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends ModuleListener<AutoCrystal, RenderEvent> {
   public ListenerRender(AutoCrystal module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      MatrixStack matrix = event.getMatrixStack();
      CrystalRenderPos cache = null;
      matrix.push();
      RenderMethods.enable3D();
      MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
      Framebuffer framebuffer = mc.getFramebuffer();
      MSAAFramebuffer.start(smoothBuffer, framebuffer);
      float alphaFactor = (Float)((AutoCrystal)this.module).getAlphaFactor().getValue();
      CrystalRenderPos renderPos = ((AutoCrystal)this.module).getRender();
      ArrayList rendered;
      int alpha;
      int boxAlpha;
      if (((AutoCrystal)this.module).getRenderMode().getValue() == RenderMode.FADE) {
         rendered = new ArrayList();
         Iterator var9 = (new ArrayList(((AutoCrystal)this.module).getFadePositions())).iterator();

         while(var9.hasNext()) {
            CrystalRenderPos crystalPos = (CrystalRenderPos)var9.next();
            if (System.currentTimeMillis() - crystalPos.getTime() > 300L) {
               rendered.add(crystalPos);
            } else {
               alpha = (int)ColorUtil.fade((double)crystalPos.getTime(), (double)(250.0F * (Float)((AutoCrystal)this.module).getRenderFactor().getValue()));
               boxAlpha = ColorUtil.fixColor((int)((float)alpha / 4.0F * alphaFactor));
               int outlineBoxAlpha = ColorUtil.fixColor((int)((float)alpha * alphaFactor));
               if (BlockUtil.canPlaceCrystal(crystalPos.getPos(), false, (Boolean)((AutoCrystal)this.module).getProtocolPlace().getValue())) {
                  Box box = Interpolation.interpolatePos(crystalPos.getPos());
                  RenderMethods.drawBox(matrix, box, this.getColor(boxAlpha));
                  RenderMethods.drawOutlineBox(matrix, box, this.getColor(outlineBoxAlpha), 1.4F);
                  if (renderPos != null && crystalPos.getPos().equals(renderPos.getPos())) {
                     cache = crystalPos;
                     crystalPos.setTime(System.currentTimeMillis());
                  }
               }
            }
         }

         ((AutoCrystal)this.module).getFadePositions().removeAll(rendered);
      } else if (renderPos != null) {
         rendered = new ArrayList();
         double extraFactor = ((AutoCrystal)this.module).getAttacks().contains(renderPos.getPos().up()) && (Boolean)((AutoCrystal)this.module).getRenderAttacks().getValue() ? 1.5D : 1.0D;
         alpha = ColorUtil.fixColor((int)((double)(50.0F * alphaFactor) * extraFactor));
         boxAlpha = ColorUtil.fixColor((int)((double)(185.0F * alphaFactor) * extraFactor));
         if (BlockUtil.canPlaceCrystal(renderPos.getPos(), false, (Boolean)((AutoCrystal)this.module).getProtocolPlace().getValue())) {
            Box box = Interpolation.interpolatePos(renderPos.getPos());
            RenderMethods.drawBox(matrix, box, this.getColor(alpha));
            RenderMethods.drawOutlineBox(matrix, box, this.getColor(boxAlpha), 1.4F);
            cache = renderPos;
            rendered.add(renderPos.getPos());
         }

         if (((AutoCrystal)this.module).getRenderMode().getValue() == RenderMode.DEBUG) {
            ((AutoCrystal)this.module).getPendingPlacePositions().forEach((pos, time) -> {
               if (!rendered.contains(pos)) {
                  int pendingBoxAlpha = ColorUtil.fixColor((int)(30.0F * alphaFactor));
                  int pendingOutlineAlpha = ColorUtil.fixColor((int)(125.0F * alphaFactor));
                  if (BlockUtil.canPlaceCrystal(pos.down(), false, (Boolean)((AutoCrystal)this.module).getProtocolPlace().getValue())) {
                     Box bb = Interpolation.interpolatePos(pos.down());
                     RenderMethods.drawBox(matrix, bb, this.getColor(pendingBoxAlpha));
                     RenderMethods.drawOutlineBox(matrix, bb, this.getColor(pendingOutlineAlpha), 1.2F);
                     rendered.add(pos);
                  }
               }

            });
         }
      }

      if ((Boolean)((AutoCrystal)this.module).getRenderExtrapolation().getValue() && (Integer)((AutoCrystal)this.module).getExtrapolation().getValue() > 0) {
         Tessellator tessellator = Tessellator.getInstance();
         RenderSystem.lineWidth(1.2F);
         RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
         RenderSystem.defaultBlendFunc();
         BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.LINES);
         int i = 0;
         Iterator var24 = (new ArrayList(((AutoCrystal)this.module).getTargets())).iterator();

         label86:
         while(true) {
            PlayerEntity player;
            Vec3d playerVec;
            do {
               LivingEntity entity;
               do {
                  if (!var24.hasNext()) {
                     if (i > 0) {
                        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                     }
                     break label86;
                  }

                  entity = (LivingEntity)var24.next();
               } while(!(entity instanceof PlayerEntity));

               player = (PlayerEntity)entity;
               playerVec = ((ILivingEntity)player).getServerVec();
            } while(playerVec.x == 0.0D && playerVec.y == 0.0D && playerVec.z == 0.0D);

            List<Vec3d> vecList = new ArrayList();

            for(int i1 = (Integer)((AutoCrystal)this.module).getExtrapolation().getValue(); i1 > 0; --i1) {
               Entity fakePlayer = CombatUtil.predictEntity(player, i1);
               vecList.add(fakePlayer.getPos());
            }

            Vec3d lastVec = null;
            Iterator var30 = vecList.iterator();

            while(var30.hasNext()) {
               Vec3d vec3d = (Vec3d)var30.next();
               Vec3d vec = Interpolation.interpolateVec(vec3d);
               if (lastVec == null) {
                  RenderMethods.drawLine(matrix, bufferBuilder, Interpolation.interpolateVec(playerVec), vec, this.getColorForLines(i++));
                  lastVec = vec3d;
               } else {
                  RenderMethods.drawLine(matrix, bufferBuilder, Interpolation.interpolateVec(lastVec), vec, this.getColor(i++));
               }
            }
         }
      }

      MSAAFramebuffer.end(smoothBuffer, framebuffer);
      RenderMethods.disable3D();
      matrix.pop();
      if ((Boolean)((AutoCrystal)this.module).getRenderDamage().getValue() && cache != null) {
         this.drawTag(matrix, renderPos);
      }

   }

   private Color getColorForLines(int i) {
      float damage = Math.min(1.0F, (float)i / 10.0F);
      float red = 255.0F * damage;
      float green = 255.0F - 255.0F * damage;
      return new Color((int)red, (int)green, 100);
   }

   private Box getBox(double factor) {
      BlockPos slide = ((AutoCrystal)this.module).getLastCrystalPos().toPos();
      double x = (double)slide.getX() + (double)(((AutoCrystal)this.module).getRender().getPos().getX() - slide.getX()) * factor;
      double y = (double)slide.getY() + (double)(((AutoCrystal)this.module).getRender().getPos().getY() - slide.getY()) * factor;
      double z = (double)slide.getZ() + (double)(((AutoCrystal)this.module).getRender().getPos().getZ() - slide.getZ()) * factor;
      return new Box(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
   }

   protected void drawTag(MatrixStack matrix, CrystalRenderPos pos) {
      float x = (float)pos.getPos().getX() + 0.5F;
      float y = (float)pos.getPos().getY() + 0.4F;
      float z = (float)pos.getPos().getZ() + 0.5F;
      float s = 0.021666668F * (Float)((AutoCrystal)this.module).getScaleFactor().getValue();
      String displayTag = "%.1f".formatted(new Object[]{pos.getDamage()});
      int textWidth = (int)(Managers.getTextManager().getWidth(displayTag) / 2.0F);
      matrix.push();
      matrix.translate((double)x - Interpolation.getRenderPosX(), (double)y - Interpolation.getRenderPosY(), (double)z - Interpolation.getRenderPosZ());
      matrix.multiply(mc.getEntityRenderDispatcher().getRotation());
      matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      matrix.scale(-s, -s, -s);
      Managers.getTextManager().drawString(matrix, displayTag, (double)(-textWidth), (double)(-Managers.getTextManager().getHeight() + 1), Color.WHITE.getRGB());
      matrix.pop();
   }

   private Color getColor(int alpha) {
      return Colours.get().getColourCustomAlpha(alpha);
   }
}
