package me.pollos.polloshook.impl.module.misc.visualrange;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.targeting.TargetUtil;
import me.pollos.polloshook.impl.module.misc.visualrange.mode.DirectionMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class VisualRange extends ToggleableModule {
   protected final Value<Boolean> left = new Value(true, new String[]{"Left", "announceleft", "leaving"});
   protected final Value<Boolean> onlyChunkLeave;
   protected final Value<Boolean> onlyIfArmor;
   protected final Value<Boolean> noFriends;
   protected final Value<Boolean> direction;
   protected final Value<Boolean> onlyOffScreen;
   protected final EnumValue<DirectionMode> directionMode;

   public VisualRange() {
      super(new String[]{"VisualRange", "visualrangenotify", "visrange"}, Category.MISC);
      this.onlyChunkLeave = (new Value(false, new String[]{"OnlyLeavingChunk", "onlyleaving", "onlychunk"})).setParent(this.left);
      this.onlyIfArmor = new Value(false, new String[]{"OnlyIfArmor", "armor", "keyCodec"});
      this.noFriends = new Value(false, new String[]{"NoFriends", "nofrds", "f"});
      this.direction = new Value(false, new String[]{"Direction", "dir", "d"});
      this.onlyOffScreen = (new Value(false, new String[]{"OnlyOffScreen", "offscreen", "screen"})).setParent(this.direction);
      this.directionMode = (new EnumValue(DirectionMode.BOTH, new String[]{"Mode", "m"})).setParent(() -> {
         return (Boolean)this.left.getValue() && (Boolean)this.direction.getValue();
      }, false);
      this.offerValues(new Value[]{this.left, this.onlyChunkLeave, this.onlyIfArmor, this.noFriends, this.direction, this.onlyOffScreen, this.directionMode});
      this.offerListeners(new Listener[]{new ListenerAdd(this), new ListenerLeave(this)});
   }

   protected String getMessage(String str, PlayerEntity player, DirectionMode dir) {
      StringBuilder message = new StringBuilder(str);
      String dirString = this.getDirectionString(player, dir);
      if (!TextUtil.isNullOrEmpty(dirString) && (!(Boolean)this.onlyOffScreen.getValue() || this.inFov(player))) {
         message.append(dirString);
      }

      Formatting color = Managers.getFriendManager().isFriend(player) ? Formatting.AQUA : Formatting.GRAY;
      String var10000 = String.valueOf(color);
      return var10000 + message.toString();
   }

   protected String getDirectionString(PlayerEntity player, DirectionMode dir) {
      boolean isValidDirection = this.directionMode.getValue() == DirectionMode.BOTH || dir == this.directionMode.getValue();
      if ((Boolean)this.direction.getValue() && isValidDirection) {
         double xDiff = player.getX() - mc.player.getX();
         double zDiff = player.getZ() - mc.player.getZ();
         Vec3d forward = this.getVec();
         Vec3d left = new Vec3d(forward.z, 0.0D, -forward.x);
         double product = (new Vec3d(xDiff, 0.0D, zDiff)).dotProduct(left);
         StringBuilder builder = new StringBuilder();
         if (!this.inFov(player)) {
            builder.append(" behind you");
         } else {
            builder.append(" in front of you");
         }

         if (Math.abs(product) >= 10.0D) {
            if (product > 0.0D) {
               builder.append(this.inFov(player) ? " to your right" : " to your right behind you");
            } else {
               builder.append(this.inFov(player) ? " to your left" : " to your left behind you");
            }
         }

         return builder.toString();
      } else {
         return null;
      }
   }

   private Vec3d getVec() {
      float yaw = mc.player.getYaw();
      float radYaw = (float)Math.toRadians((double)yaw);
      return new Vec3d(Math.sin((double)radYaw), 0.0D, -Math.cos((double)radYaw));
   }

   protected boolean isValid(PlayerEntity player) {
      if ((Boolean)this.onlyIfArmor.getValue() && !TargetUtil.hasArmor(player)) {
         return false;
      } else {
         return !(Boolean)this.noFriends.getValue() || !Managers.getFriendManager().isFriend(player);
      }
   }

   private boolean inFov(Entity entity) {
      float[] angle = calculateAngle(entity.getBoundingBox().getCenter());
      double xDist = (double)MathHelper.angleBetween(angle[0], mc.player.getYaw());
      double yDist = (double)MathHelper.angleBetween(angle[1], mc.player.getPitch());
      double angleDistance = Math.hypot(xDist, yDist);
      return angleDistance <= 180.0D;
   }

   public static float[] calculateAngle(Vec3d target) {
      Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + (double)mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
      double dX = target.x - eyesPos.x;
      double dY = (target.y - eyesPos.y) * -1.0D;
      double dZ = target.z - eyesPos.z;
      double dist = Math.sqrt(dX * dX + dZ * dZ);
      return new float[]{(float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dZ, dX)) - 90.0D), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dY, dist)))};
   }
}
