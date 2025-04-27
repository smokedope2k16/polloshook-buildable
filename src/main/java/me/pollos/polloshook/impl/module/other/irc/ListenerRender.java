package me.pollos.polloshook.impl.module.other.irc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.other.irc.util.IrcPing;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends ModuleListener<IrcModule, RenderEvent> {
   public ListenerRender(IrcModule module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if (Managers.getIrcManager().isConnected()) {
         if (mc.getCurrentServerEntry() != null) {
            MatrixStack matrix = event.getMatrixStack();
            List<IrcPing> toRemove = new ArrayList();
            matrix.push();
            RenderMethods.enable3D();
            MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
            Framebuffer framebuffer = mc.getFramebuffer();
            MSAAFramebuffer.start(smoothBuffer, framebuffer);
            ArrayList<IrcPing> cache = new ArrayList(((IrcModule)this.module).pings);
            Iterator var7 = cache.iterator();

            IrcPing ping;
            while(var7.hasNext()) {
               ping = (IrcPing)var7.next();
               if (System.currentTimeMillis() - ping.time() > 180000L) {
                  toRemove.add(ping);
               } else if (this.isValid(ping)) {
                  Box bb = new Box(ping.x() - 0.25D, 0.0D, ping.z() - 0.25D, ping.x() + 0.25D, 255.0D, ping.z() + 0.25D);
                  bb = Interpolation.interpolateAxis(bb);
                  double playerDistance = mc.player.squaredDistanceTo(new Vec3d(ping.x(), ping.y(), ping.z()));
                  double range = 30.0D;
                  double normalizedDistance = Math.max(0.0D, Math.min(1.0D, playerDistance / 32.0D));
                  int value = (int)(2.0D + range * normalizedDistance);
                  RenderMethods.drawBox(matrix, bb, Colours.get().getColourCustomAlpha(value));
               }
            }

            MSAAFramebuffer.end(smoothBuffer, framebuffer);
            RenderMethods.disable3D();
            matrix.pop();
            var7 = cache.iterator();

            while(var7.hasNext()) {
               ping = (IrcPing)var7.next();
               if (this.isValid(ping)) {
                  int dist = (int)StrictMath.sqrt(mc.player.squaredDistanceTo(ping.x(), ping.y(), ping.z()));
                  Box bb = new Box(ping.x(), ping.y(), ping.z(), ping.x(), ping.y(), ping.z());
                  bb = Interpolation.interpolateAxis(bb);
                  this.renderNameTag(matrix, "%s's ping (%sm)".formatted(new Object[]{ping.name(), dist}), bb);
               }
            }

            ((IrcModule)this.module).pings.removeAll(toRemove);
         }
      }
   }

   protected void renderNameTag(MatrixStack matrix, String text, Box interpolated) {
      double x = (interpolated.minX + interpolated.maxX) / 2.0D;
      double z = (interpolated.minZ + interpolated.maxZ) / 2.0D;
      this.drawTag(matrix, text, x, interpolated.minY + 0.5D, z, new Color(-1), Interpolation.getMcPlayerInterpolation());
   }

   private void drawTag(MatrixStack matrix, String displayTag, double x, double y, double z, Color color, Vec3d mcPlayerInterpolation) {
      double xDist = mcPlayerInterpolation.x - x;
      double yDist = mcPlayerInterpolation.y - y;
      double zDist = mcPlayerInterpolation.z - z;
      float dist = MathHelper.sqrt((float)(xDist * xDist + yDist * yDist + zDist * zDist));
      double s = 0.0018D + (double)(MathUtil.fixedNametagScaling(0.3F) * dist);
      if (dist <= 8.0F) {
         s = 0.0245D;
      }

      matrix.push();
      int width = (int)(Managers.getTextManager().getWidth(displayTag) / 2.0F);
      matrix.translate((float)x, (float)y, (float)z);
      matrix.multiply(mc.getEntityRenderDispatcher().getRotation());
      matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      matrix.scale((float)(-s), (float)(-s), (float)(-s));
      RenderMethods.enable3D();
      Render2DMethods.drawNameTagRect(matrix, (float)(-width - 2), (float)(-Managers.getTextManager().getHeight()), (float)width + 2.0F, Managers.getTextManager().getHeightScale(displayTag), 1426064384, 855638016, 1.4F);
      RenderMethods.disable3D();
      Managers.getTextManager().drawString(matrix, displayTag, (double)(-width), (double)(-(Managers.getTextManager().getHeight() - 1)), color.getRGB());
      matrix.pop();
   }

   private boolean isValid(IrcPing ping) {
      boolean flag = NetworkUtil.getOnlinePlayersProfile().stream().anyMatch((m) -> {
         return m.getName().equals(ping.name());
      });
      return mc.world.getRegistryKey().getValue().getPath().equals(ping.dimension()) && flag;
   }
}
