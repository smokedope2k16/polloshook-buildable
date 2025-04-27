package me.pollos.polloshook.asm.mixins.network;

import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BundlePacket.class})
public interface IBundlePacket {
   @Mutable
   @Accessor("packets")
   void setPackets(Iterable<Packet<?>> var1);
}
