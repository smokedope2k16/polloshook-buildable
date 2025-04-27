package me.pollos.polloshook.impl.manager.minecraft.server;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.Formatting;

public class CrashManager extends SubscriberImpl implements Minecraftable {
   public CrashManager() {
      this.listeners.add(new Listener<PacketEvent.Receive<ParticleS2CPacket>>(
          PacketEvent.Receive.class,
          Integer.MAX_VALUE,      
          ParticleS2CPacket.class   
      ) {
         @Override
         public void call(PacketEvent.Receive<ParticleS2CPacket> event) {
            if (event.getPacket().getCount() > 10000) {
               ClientLogger.getLogger().log(String.valueOf(Formatting.RED) + "Potential Particle Crash detected");
               event.setCanceled(true);
            }
         }
      });

      this.listeners.add(new Listener<PacketEvent.Receive<PlaySoundS2CPacket>>(
          PacketEvent.Receive.class, 
          Integer.MAX_VALUE,        
          PlaySoundS2CPacket.class 
      ) {
         @Override
         public void call(PacketEvent.Receive<PlaySoundS2CPacket> event) {
            if (event.getPacket().getPitch() > 10000.0F || event.getPacket().getVolume() > 10000.0F) {
               ClientLogger.getLogger().log(String.valueOf(Formatting.RED) + "Potential Sound Crash detected");
               event.setCanceled(true);
            }
         }
      });

      this.listeners.add(new Listener<PacketEvent.Receive<ExplosionS2CPacket>>(
          PacketEvent.Receive.class,
          Integer.MAX_VALUE,       
          ExplosionS2CPacket.class  
      ) {
         @Override 
         public void call(PacketEvent.Receive<ExplosionS2CPacket> event) {
            if (event.getPacket().getRadius() > 10000.0F) {
               ClientLogger.getLogger().log(String.valueOf(Formatting.RED) + "Potential Explosion Crash detected");
               event.setCanceled(true);
            }
         }
      });
   }

   private boolean isIllegalYaw(float yaw) {
      return yaw > 180.0F || yaw <= -180.0F;
   }

   private boolean isIllegalPitch(float pitch) {
      return pitch > 90.0F || pitch < -90.0F;
   }

   private boolean isIllegalDouble(double value) {
      return value > Double.MAX_VALUE || value < Double.MIN_VALUE;
   }
}