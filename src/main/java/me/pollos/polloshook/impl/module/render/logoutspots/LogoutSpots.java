package me.pollos.polloshook.impl.module.render.logoutspots;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.CommandModule;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.module.render.logoutspots.point.LogoutPoint;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class LogoutSpots extends CommandModule {
   private final NumberValue<Float> scale = new NumberValue(0.3F, 0.1F, 1.0F, 0.1F, new String[]{"Scale", "scaling"});
   protected final Value<Boolean> greeter = new Value(true, new String[]{"Greeter", "greet"});
   protected final Value<Boolean> totemCount = new Value(false, new String[]{"TotemCount", "tc"});
   protected final Value<Boolean> distance = new Value(false, new String[]{"Distance", "dist", "meters"});
   protected final Value<Boolean> bordered = new Value(true, new String[]{"Bordered", "border"});
   protected final Value<Boolean> ghost = new Value(false, new String[]{"Ghost", "ghostly", "ghostly2002"});
   protected final ColorValue ghostColor;
   protected final ColorValue textColor;
   protected final ColorValue color;
   protected final Map<UUID, LogoutPoint> spots;

   public LogoutSpots() {
      super(new String[]{"LogoutSpots", "logoutesp", "logoutpoints", "logouts"}, Category.RENDER, new String[]{"ClearLogs", "clear", "logoutsclear"});
      this.ghostColor = (new ColorValue(new Color(-1), true, new String[]{"GhostColor", "ghostcolor"})).setParent(this.ghost);
      this.textColor = new ColorValue(new Color(9868950), false, new String[]{"TextColor", "textcolour"});
      this.color = new ColorValue(new Color(-1), true, new String[]{"Color", "c"});
      this.spots = new ConcurrentHashMap();
      this.offerValues(new Value[]{this.scale, this.greeter, this.totemCount, this.distance, this.bordered, this.ghost, this.ghostColor, this.textColor, this.color});
      this.offerListeners(new Listener[]{new ListenerJoin(this), new ListenerLeave(this), new ListenerLeaveGame(this), new ListenerRender(this), new ListenerCape(this), new ListenerNametag(this)});
   }

   public String onCommand(String[] args) {
      if (this.spots.isEmpty()) {
         return "No spots, try again later";
      } else {
         HashMap<UUID, LogoutPoint> list = new HashMap(this.spots);
         this.spots.clear();
         return "Cleared %s player%s".formatted(new Object[]{list.size(), list.size() == 1 ? "" : "s"});
      }
   }

   public void onDisable() {
      this.spots.clear();
   }

   public void onWorldLoad() {
      this.spots.clear();
   }

   protected void drawTag(MatrixStack matrix, String displayTag, double x, double y, double z, Vec3d mcPlayerInterpolation) {
      double tempY = y + 0.7D;
      double xDist = mcPlayerInterpolation.x - x;
      double yDist = mcPlayerInterpolation.y - y;
      double zDist = mcPlayerInterpolation.z - z;
      y = (double)MathHelper.sqrt((float)(xDist * xDist + yDist * yDist + zDist * zDist));
      double s = 0.0018D + (double)MathUtil.fixedNametagScaling((Float)this.scale.getValue()) * y;
      if (y <= 8.0D) {
         s = 0.0245D;
      }

      int textWidth = (int)(Managers.getTextManager().getWidth(displayTag) / 2.0F);
      matrix.push();
      matrix.translate((float)x, (float)tempY + 1.4F, (float)z);
      matrix.multiply(mc.getEntityRenderDispatcher().getRotation());
      matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      matrix.scale((float)(-s), (float)(-s), (float)(-s));
      if ((Boolean)this.bordered.getValue()) {
         RenderMethods.enable3D();
         Render2DMethods.drawNameTagRect(matrix, (float)(-textWidth - 2), (float)(-Managers.getTextManager().getHeight()), (float)textWidth + 2.0F, Managers.getTextManager().getHeightScale(displayTag), 1426064384, 855638016, 1.4F);
         RenderMethods.disable3D();
      }

      Managers.getTextManager().drawString(matrix, displayTag, (double)(-textWidth), (double)(-Managers.getTextManager().getHeight() + 1), this.textColor.getColor().getRGB());
      matrix.pop();
   }

   
   public Map<UUID, LogoutPoint> getSpots() {
      return this.spots;
   }
}
