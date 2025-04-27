package me.pollos.polloshook.impl.events.movement;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

public class MoveEvent extends Event {
   private MovementType type;
   private Vec3d vec;

   public void setVec(double x, double y, double z) {
      this.vec = new Vec3d(x, y, z);
   }

   public void setY(double y) {
      this.vec = new Vec3d(this.getVec().x, y, this.getVec().z);
   }

   public void setXZ(double x, double z) {
      this.vec = new Vec3d(x, this.getVec().y, z);
   }

   public double getX() {
      return this.getVec().getX();
   }

   public double getY() {
      return this.getVec().getY();
   }

   public double getZ() {
      return this.getVec().getZ();
   }

   
   public MovementType getType() {
      return this.type;
   }

   
   public Vec3d getVec() {
      return this.vec;
   }

   
   public void setType(MovementType type) {
      this.type = type;
   }

   
   public void setVec(Vec3d vec) {
      this.vec = vec;
   }

   
   public MoveEvent(MovementType type, Vec3d vec) {
      this.type = type;
      this.vec = vec;
   }
}
