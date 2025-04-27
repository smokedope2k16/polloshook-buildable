package me.pollos.polloshook.asm.ducks.entity;

import net.minecraft.entity.LimbAnimator;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public interface ILivingEntity {
   int getJumpCooldown();

   void setLastJumpCooldown(int var1);

   int getArmSwingAnim();

   void setServerVec(Vec3d var1);

   void setServerXYZ(double var1, double var3, double var5);

   void setServerYawPitch(float var1, float var2);

   void interpolateSteps(int var1);

   void setLimbAnimator(LimbAnimator var1);

   Vec3d getServerVec();

   Box getServerBoundingBox();
}
