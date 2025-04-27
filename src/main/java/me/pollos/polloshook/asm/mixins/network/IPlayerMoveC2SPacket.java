package me.pollos.polloshook.asm.mixins.network;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PlayerMoveC2SPacket.class})
public interface IPlayerMoveC2SPacket {
   @Accessor("x")
   double getRawX();

   @Accessor("y")
   double getRawY();

   @Accessor("z")
   double getRawZ();

   @Accessor("pitch")
   float getRawPitch();

   @Accessor("yaw")
   float getRawYaw();

   @Mutable
   @Accessor("yaw")
   void setYaw(float var1);

   @Mutable
   @Accessor("pitch")
   void setPitch(float var1);

   @Mutable
   @Accessor("onGround")
   void setOnGround(boolean var1);

   @Mutable
   @Accessor("y")
   void setY(double var1);
}
