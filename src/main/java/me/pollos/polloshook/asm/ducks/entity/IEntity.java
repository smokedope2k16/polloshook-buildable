package me.pollos.polloshook.asm.ducks.entity;

import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.Vec3d;

public interface IEntity {
   boolean isInWeb();

   void setInWeb(boolean var1);

   void setInventory(PlayerInventory var1);

   boolean isPrevOnGround();

   void setX(double var1);

   void setY(double var1);

   void setZ(double var1);

   TrackedData<Boolean> getNameVisible();

   Vec3d adjustForSneaking(Vec3d var1, MovementType var2);

   Vec3d adjustForCollisions(Vec3d var1);
}
