package me.pollos.polloshook.impl.module.render.logoutspots;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.asm.ducks.render.IEntityRenderer;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.render.chams.Chams;
import me.pollos.polloshook.impl.module.render.logoutspots.point.LogoutPoint;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends SafeModuleListener<LogoutSpots, RenderEvent> {
   public ListenerRender(LogoutSpots module) {
      super(module, RenderEvent.class);
   }

   public void safeCall(RenderEvent event) {
      if (!((LogoutSpots)this.module).spots.isEmpty()) {
         List<LogoutPoint> spotList = new ArrayList(((LogoutSpots)this.module).spots.values());
         spotList.sort(Comparator.comparing(LogoutPoint::getDistance));
         Collections.reverse(spotList);
         MatrixStack matrix = event.getMatrixStack();
         Iterator var4 = spotList.iterator();

         while(var4.hasNext()) {
            LogoutPoint spot = (LogoutPoint)var4.next();
            Box bb = Interpolation.interpolateAxis(spot.getBoundingBox());
            PlayerEntity player = spot.getPlayer();
            if (Interpolation.isVisible(bb.expand(0.5D), event)) {
               if ((Boolean)((LogoutSpots)this.module).ghost.getValue()) {
                  this.renderGhost(matrix, event.getTickDelta(), player);
               } else {
                  this.renderOutline(matrix, bb);
               }

               RenderMethods.resetColor();
               String name = spot.getName();
               String text = name + " logged out at " + MathUtil.round(spot.getX(), 2) + ", " + MathUtil.round(spot.getY(), 2) + ", " + MathUtil.round(spot.getZ(), 2);
               if ((Boolean)((LogoutSpots)this.module).totemCount.getValue() && Managers.getPopManager().getPopMap().get(spot.getName()) != null && (Integer)Managers.getPopManager().getPopMap().get(spot.getName()) != 0) {
                  text = text + " after popping their %s%s totem".formatted(new Object[]{Managers.getPopManager().getPopMap().get(spot.getName()), TextUtil.toOrdinal((Integer)Managers.getPopManager().getPopMap().get(spot.getName()))});
               }

               if ((Boolean)((LogoutSpots)this.module).distance.getValue()) {
                  text = text + " [%sm]".formatted(new Object[]{MathUtil.round((double)mc.player.distanceTo(player), 1)});
               }

               ((LogoutSpots)this.module).drawTag(matrix, text, spot.getX() - Interpolation.getCameraPos().x, spot.getY() - Interpolation.getCameraPos().y, spot.getZ() - Interpolation.getCameraPos().z, Interpolation.getMcPlayerInterpolation());
            }
         }

      }
   }

   protected void renderOutline(MatrixStack matrix, Box bb) {
      matrix.push();
      RenderMethods.enable3D();
      MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
      Framebuffer framebuffer = mc.getFramebuffer();
      MSAAFramebuffer.start(smoothBuffer, framebuffer);
      RenderMethods.drawOutlineBox(matrix, bb, ((LogoutSpots)this.module).color.getColor(), 1.5F);
      MSAAFramebuffer.end(smoothBuffer, framebuffer);
      RenderMethods.disable3D();
      matrix.pop();
   }

   protected void renderGhost(MatrixStack matrix, float delta, PlayerEntity player) {
      if (((LogoutSpots)this.module).ghostColor.getColor().getAlpha() > 0) {
         player.limbAnimator.updateLimbs(0.0F, 0.0F);
         player.limbAnimator.setSpeed(0.0F);
         player.hurtTime = 0;
         player.age = 0;
         player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
         player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
         player.setOnFire(false);
         player.clearActiveItem();
         matrix.push();
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         RenderMethods.color(((LogoutSpots)this.module).ghostColor.getColor().getRGB());
         Vec3d vec3d = ((ILivingEntity)player).getServerVec();
         double interpX = vec3d.x - Interpolation.getRenderPosX();
         double interpY = vec3d.y - Interpolation.getRenderPosY();
         double interpZ = vec3d.z - Interpolation.getRenderPosZ();
         ((Chams)Managers.getModuleManager().get(Chams.class)).setStop(true);
         ((IEntityRenderer)mc.getEntityRenderDispatcher()).renderNoShadows(player, interpX, interpY, interpZ, player.getYaw(delta), delta, matrix, mc.getBufferBuilders().getEntityVertexConsumers(), 15728880);
         ((Chams)Managers.getModuleManager().get(Chams.class)).setStop(false);
         RenderSystem.disableBlend();
         matrix.pop();
         player.setPosition(vec3d);
         player.prevPitch = player.getPitch();
         player.prevYaw = player.getYaw();
         player.prevHeadYaw = player.headYaw;
      }
   }
}
