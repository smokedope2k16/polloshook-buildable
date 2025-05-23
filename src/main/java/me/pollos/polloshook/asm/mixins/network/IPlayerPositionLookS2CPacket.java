package me.pollos.polloshook.asm.mixins.network;

import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PlayerPositionLookS2CPacket.class})
public interface IPlayerPositionLookS2CPacket {
   @Mutable
   @Accessor("yaw")
   void setYaw(float var1);

   @Mutable
   @Accessor("pitch")
   void setPitch(float var1);
}
