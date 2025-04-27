package me.pollos.polloshook.impl.module.misc.deathcoordslog;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.misc.deathcoordslog.util.DeathWaypoint;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends ModuleListener<DeathCoordsLog, RenderEvent> {
   public ListenerRender(DeathCoordsLog module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if ((Boolean)((DeathCoordsLog)this.module).waypoint.getValue()) {
         MatrixStack matrix = event.getMatrixStack();
         List<DeathWaypoint> remove = new ArrayList();

         for(int i = 0; i < ((DeathCoordsLog)this.module).waypointList.size(); ++i) {
            DeathWaypoint waypoint = (DeathWaypoint)((DeathCoordsLog)this.module).waypointList.get(i);
            if (waypoint.dimension() == mc.world.getDimension()) {
               if ((float)(System.currentTimeMillis() - waypoint.time()) > (Float)((DeathCoordsLog)this.module).timeout.getValue() * 60000.0F) {
                  remove.add(waypoint);
               } else {
                  Vec3d vec = waypoint.vec();
                  Box bb = new Box(vec.x - 0.25D, 0.0D, vec.z - 0.25D, vec.x + 0.25D, 255.0D, vec.z + 0.25D);
                  bb = Interpolation.interpolateAxis(bb);
                  RenderMethods.enable3D();
                  double playerDistance = mc.player.squaredDistanceTo(vec);
                  double range = 30.0D;
                  double normalizedDistance = Math.max(0.0D, Math.min(1.0D, playerDistance / 32.0D));
                  int value = (int)(2.0D + range * normalizedDistance);
                  RenderMethods.drawBox(matrix, bb, Colours.get().getColourCustomAlpha(value));
                  RenderMethods.disable3D();
                  Box nametagBox = Interpolation.interpolateAxis(new Box(vec, vec));
                  double sAgo = (double)(System.currentTimeMillis() - waypoint.time()) / 10000.0D;
                  String helper = "";
                  if (((DeathCoordsLog)this.module).waypointList.size() > 3) {
                     if (i == 0) {
                        helper = " (Oldest)";
                     } else if (i == ((DeathCoordsLog)this.module).waypointList.size() - 1) {
                        helper = " (Newest)";
                     }
                  }

                  String nameTag = String.format("DeathPoint %d (%.1fs ago)%s", i + 1, sAgo, helper);
                  this.renderNameTag(matrix, nameTag, nametagBox);
               }
            }
         }

         ((DeathCoordsLog)this.module).waypointList.removeAll(remove);
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
      RenderSystem.enablePolygonOffset();
      RenderSystem.polygonOffset(1.0F, -1500000.0F);
      int width = (int)(Managers.getTextManager().getWidth(displayTag) / 2.0F);
      matrix.translate((float)x, (float)y, (float)z);
      matrix.multiply(mc.getEntityRenderDispatcher().getRotation());
      matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      matrix.scale((float)(-s), (float)(-s), (float)(-s));
      RenderSystem.disableDepthTest();
      RenderSystem.enableBlend();
      Render2DMethods.drawNameTagRect(matrix, (float)(-width - 2), (float)(-Managers.getTextManager().getHeight()), (float)width + 2.0F, Managers.getTextManager().getHeightScale(displayTag), 1426064384, 855638016, 1.4F);
      Managers.getTextManager().drawString(matrix, displayTag, (double)(-width), (double)(-(Managers.getTextManager().getHeight() - 1)), color.getRGB());
      RenderSystem.disableBlend();
      RenderSystem.enableDepthTest();
      RenderSystem.disableBlend();
      RenderSystem.polygonOffset(1.0F, 1500000.0F);
      RenderSystem.disablePolygonOffset();
      matrix.pop();
   }
}
