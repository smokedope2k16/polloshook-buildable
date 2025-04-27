package me.pollos.polloshook.api.minecraft.rotations;

public class RenderRotations {
   private float yaw;
   private float headYaw;
   private float bodyYaw;
   private float pitch;
   private float prevYaw;
   private float prevHeadYaw;
   private float prevBodyYaw;
   private float prevPitch;

   public RenderRotations(float yaw, float headYaw, float bodyYaw, float pitch, float prevYaw, float prevHeadYaw, float prevBodyYaw, float prevPitch) {
      this.yaw = yaw;
      this.headYaw = headYaw;
      this.bodyYaw = bodyYaw;
      this.pitch = pitch;
      this.prevYaw = prevYaw;
      this.prevHeadYaw = prevHeadYaw;
      this.prevBodyYaw = prevBodyYaw;
      this.prevPitch = prevPitch;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getHeadYaw() {
      return this.headYaw;
   }

   public float getBodyYaw() {
      return this.bodyYaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public float getPrevYaw() {
      return this.prevYaw;
   }

   public float getPrevHeadYaw() {
      return this.prevHeadYaw;
   }

   public float getPrevBodyYaw() {
      return this.prevBodyYaw;
   }

   public float getPrevPitch() {
      return this.prevPitch;
   }
}