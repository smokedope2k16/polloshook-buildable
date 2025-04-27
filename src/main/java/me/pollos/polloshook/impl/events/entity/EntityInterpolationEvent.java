package me.pollos.polloshook.impl.events.entity;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.LivingEntity;

public class EntityInterpolationEvent extends Event {
   private final LivingEntity entity;
   private final double x;
   private final double y;
   private final double z;
   private final float yaw;
   private final float pitch;

   
   public LivingEntity getEntity() {
      return this.entity;
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

   
   public float getYaw() {
      return this.yaw;
   }

   
   public float getPitch() {
      return this.pitch;
   }

   
   public EntityInterpolationEvent(LivingEntity entity, double x, double y, double z, float yaw, float pitch) {
      this.entity = entity;
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
   }
}
