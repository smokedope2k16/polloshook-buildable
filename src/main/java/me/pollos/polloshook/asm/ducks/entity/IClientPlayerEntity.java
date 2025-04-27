package me.pollos.polloshook.asm.ducks.entity;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public interface IClientPlayerEntity {
   void sendMovePackets();

   double squaredDistanceTo(BlockPos var1);

   void setActiveHand(Hand var1);

   boolean getIsCamera();

   float getLastYaw();

   float getLastPitch();

   void setLastYaw(float var1);

   void setLastPitch(float var1);

   boolean getLastSprinting();

   boolean getLastSneaking();

   void setLastSneaking(boolean var1);

   void setLastSprinting(boolean var1);

   HitResult raycastFromCustomAngles(double var1, float var3, boolean var4, float var5, float var6);

   void $tick();

   boolean isEatingFlag();
}
