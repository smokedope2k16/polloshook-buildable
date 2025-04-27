package me.pollos.polloshook.impl.module.render.logoutspots.point;


import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class LogoutPoint implements Minecraftable {
   private final String name;
   private final PlayerEntity player;
   private final Box boundingBox;
   private final double x;
   private final double y;
   private final double z;

   public LogoutPoint(PlayerEntity player) {
      this.player = player;
      this.name = player.getName().getString();
      this.boundingBox = player.getBoundingBox();
      Vec3d vec = ((ILivingEntity)player).getServerVec();
      this.x = vec.getX();
      this.y = vec.getY();
      this.z = vec.getZ();
   }

   public double getDistance() {
      return Interpolation.getCameraPos().squaredDistanceTo(this.x, this.y, this.z);
   }

   public Vec3d rounded() {
      return new Vec3d(MathUtil.round(this.x, 1), MathUtil.round(this.y, 1), MathUtil.round(this.z, 1));
   }

   
   public String getName() {
      return this.name;
   }

   
   public PlayerEntity getPlayer() {
      return this.player;
   }

   
   public Box getBoundingBox() {
      return this.boundingBox;
   }

   
   public double getX() {
      return this.x;
   }

   
   public double getY() {
      return this.y;
   }

   
   public double getZ() {
      return this.z;
   }
}
