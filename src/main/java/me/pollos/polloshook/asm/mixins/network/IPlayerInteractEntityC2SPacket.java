package me.pollos.polloshook.asm.mixins.network;

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PlayerInteractEntityC2SPacket.class})
public interface IPlayerInteractEntityC2SPacket {
   @Accessor("entityId")
   int getEntityID();

   @Mutable
   @Accessor("entityId")
   void setEntityID(int var1);
}
