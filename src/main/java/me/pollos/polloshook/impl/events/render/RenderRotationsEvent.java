package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;

public class RenderRotationsEvent extends Event {
   private final float yaw;
   private final float pitch;

   public RenderRotationsEvent(float[] rotations) {
      this(rotations[0], rotations[1]);
   }

   
   public float getYaw() {
      return this.yaw;
   }

   
   public float getPitch() {
      return this.pitch;
   }

   
   public RenderRotationsEvent(float yaw, float pitch) {
      this.yaw = yaw;
      this.pitch = pitch;
   }
}
