package me.pollos.polloshook.impl.manager.minecraft.server;

import java.util.ArrayDeque;
import java.util.Iterator;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class TpsManager extends SubscriberImpl implements Minecraftable {
   private final ArrayDeque<Float> queue = new ArrayDeque(20);
   private float currentTps;
   private float tps;
   private long time;

   public TpsManager() {
      this.listeners.add(new Listener<PacketEvent.Receive<WorldTimeUpdateS2CPacket>>(PacketEvent.Receive.class, WorldTimeUpdateS2CPacket.class) {
         public void call(PacketEvent.Receive<WorldTimeUpdateS2CPacket> event) {
            if (TpsManager.this.time != 0L) {
               if (TpsManager.this.queue.size() > 20) {
                  TpsManager.this.queue.poll();
               }

               TpsManager.this.currentTps = Math.max(0.0F, Math.min(20.0F, 20.0F * (1000.0F / (float)(System.currentTimeMillis() - TpsManager.this.time))));
               TpsManager.this.queue.add(TpsManager.this.currentTps);
               float factor = 0.0F;

               Float qTime;
               for(Iterator var3 = TpsManager.this.queue.iterator(); var3.hasNext(); factor += Math.max(0.0F, Math.min(20.0F, qTime))) {
                  qTime = (Float)var3.next();
               }

               if (!TpsManager.this.queue.isEmpty()) {
                  factor /= (float)TpsManager.this.queue.size();
               }

               TpsManager.this.tps = factor;
            }

            TpsManager.this.time = System.currentTimeMillis();
         }
      });
   }

   public float getServerWorldTPS() {
      return mc.getServer() != null && mc.getServer().getWorld(mc.world.getRegistryKey()) != null && mc.getServer().getWorld(mc.world.getRegistryKey()).getTickManager() != null ? mc.getServer().getWorld(mc.world.getRegistryKey()).getTickManager().getTickRate() : Managers.getTpsManager().getTps();
   }

   
   public float getCurrentTps() {
      return this.currentTps;
   }

   
   public float getTps() {
      return this.tps;
   }
}
