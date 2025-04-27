package me.pollos.polloshook.impl.module.player.fakeplayer.utils;


import me.pollos.polloshook.api.minecraft.rotations.RenderRotations;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;

public class FakePlayerPosition {
   private final double x;
   private final double y;
   private final double z;
   private final RenderRotations renderRotations;
   private final double motionX;
   private final double motionY;
   private final double motionZ;
   private final EntityPose pose;

   public FakePlayerPosition(PlayerEntity player) {
      this.x = player.getX();
      this.y = player.getY();
      this.z = player.getZ();
      this.renderRotations = new RenderRotations(player.getYaw(), player.headYaw, player.bodyYaw, player.getPitch(), player.prevYaw, player.prevHeadYaw, player.prevBodyYaw, player.prevPitch);
      this.motionX = player.getVelocity().x;
      this.motionY = player.getVelocity().y;
      this.motionZ = player.getVelocity().z;
      this.pose = player.getPose();
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

   
   public RenderRotations getRenderRotations() {
      return this.renderRotations;
   }

   
   public double getMotionX() {
      return this.motionX;
   }

   
   public double getMotionY() {
      return this.motionY;
   }

   
   public double getMotionZ() {
      return this.motionZ;
   }

   
   public EntityPose getPose() {
      return this.pose;
   }
}
