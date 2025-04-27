package me.pollos.polloshook.asm.mixins.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.asm.ducks.util.IClientConnection;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientConnection.class})
public abstract class MixinClientConnection implements IClientConnection {
   @Shadow
   private Channel channel;
   @Shadow
   @Final
   private NetworkSide side;

   @Shadow
   public abstract boolean isOpen();

   @Shadow
   public abstract void flush();

   @Shadow
   public abstract void send(Packet<?> var1, @Nullable PacketCallbacks var2);

   @Inject(
      method = {"channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void channelRead0Hook(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo info) {
      if (this.channel.isOpen()) {
         if (packet instanceof BundleS2CPacket) {
            BundleS2CPacket bundle = (BundleS2CPacket)packet;
            List<Packet<?>> packets = new ArrayList();
            Iterator var6 = bundle.getPackets().iterator();

            while(var6.hasNext()) {
               Packet<?> p = (Packet)var6.next();
               PacketEvent.Receive<?> bundleEvent = new PacketEvent.Receive(p);
               if (p != null) {
                  PollosHook.getEventBus().dispatch(bundleEvent, p.getClass());
                  if (!bundleEvent.isCanceled()) {
                     packets.add(p);
                  }
               }
            }

            ((IBundlePacket)bundle).setPackets(packets);
         }

         PacketEvent.Receive packetEvent = new PacketEvent.Receive(packet);

         try {
            if (packet == null) {
               return;
            }

            PollosHook.getEventBus().dispatch(packetEvent, packet.getClass());
         } catch (Throwable var9) {
            var9.printStackTrace();
         }

         if (packetEvent.isCanceled()) {
            info.cancel();
         }
      }

   }

   @Inject(
      method = {"sendImmediately"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendImmediatelyHook(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush, CallbackInfo info) {
      if (this.side == NetworkSide.CLIENTBOUND) {
         PacketEvent.Send<?> event = new PacketEvent.Send(packet);
         PollosHook.getEventBus().dispatch(event, packet.getClass());
         if (event.isCanceled()) {
            info.cancel();
         }

      }
   }

   @Inject(
      method = {"sendInternal"},
      at = {@At("RETURN")}
   )
   private void sendInternalHook(Packet<?> packet, @Nullable PacketCallbacks callbacks, boolean flush, CallbackInfo info) {
      PacketEvent.Post<?> packetEvent = new PacketEvent.Post(packet);
      PollosHook.getEventBus().dispatch(packetEvent, packet.getClass());
   }

   @Inject(
      method = {"exceptionCaught"},
      at = {@At("RETURN")}
   )
   public void exceptionCaughtHook(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
      ex.printStackTrace();
      System.out.println("----------------------------------------------");
      Thread.dumpStack();
   }

   @Unique
   public Packet<?> sendPacketNoEvent(Packet<?> packet) {
      PacketEvent.NoEvent<?> event = new PacketEvent.NoEvent(packet);
      event.dispatch(packet.getClass());
      if (event.isCanceled()) {
         return packet;
      } else if (this.isOpen()) {
         this.flush();
         this.send(packet, (PacketCallbacks)null);
         return packet;
      } else {
         return null;
      }
   }
}