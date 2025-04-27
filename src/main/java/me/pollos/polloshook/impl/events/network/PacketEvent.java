package me.pollos.polloshook.impl.events.network;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.network.packet.Packet;

public class PacketEvent<T extends Packet<?>> extends Event {
   private final T packet;

   private PacketEvent(T packet) {
      this.packet = packet;
   }

   
   public T getPacket() {
      return this.packet;
   }

   public static class NoEvent<T extends Packet<?>> extends PacketEvent<T> {
      public NoEvent(T packet) {
         super(packet);
      }
   }

   public static class Post<T extends Packet<?>> extends PacketEvent<T> {
      public Post(T packet) {
         super(packet);
      }
   }

   public static class Receive<T extends Packet<?>> extends PacketEvent<T> {
      public Receive(T packet) {
         super(packet);
      }
   }

   public static class Send<T extends Packet<?>> extends PacketEvent<T> {
      public Send(T packet) {
         super(packet);
      }
   }
}
