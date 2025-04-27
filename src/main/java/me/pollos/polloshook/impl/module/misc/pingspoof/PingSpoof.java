package me.pollos.polloshook.impl.module.misc.pingspoof;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.thread.ThreadUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.network.packet.Packet;

public class PingSpoof extends ToggleableModule {
   protected final NumberValue<Integer> delay = (new NumberValue(250, 1, 1000, new String[]{"Delay", "del", "d"})).withTag("ms");
   protected final List<Packet<?>> packets = new ArrayList();
   private final ScheduledExecutorService SERVICE;

   public PingSpoof() {
      super(new String[]{"PingSpoof", "pingspoofer"}, Category.MISC);
      this.offerValues(new Value[]{this.delay});
      this.offerListeners(new Listener[]{new ListenerPacket(this), new ListenerLogout(this)});
      this.SERVICE = ThreadUtil.newDaemonScheduledExecutor("PingSpoof");
   }

   public void onShutdown() {
      this.SERVICE.shutdown();
   }

   protected void onDisable() {
      this.clearPackets(true);
   }

   protected void clearPackets(boolean send) {
      if (send) {
         Iterator var2 = this.packets.iterator();

         while(var2.hasNext()) {
            Packet<?> packet = (Packet)var2.next();
            PacketUtil.sendNoEvent(packet);
         }
      }

      this.packets.clear();
   }

   protected void onPacket(Packet<?> packet) {
      this.packets.add(packet);
      this.SERVICE.schedule(() -> {
         if (mc.player != null) {
            Packet<?> p = (Packet)this.packets.get(0);
            PacketUtil.sendNoEvent(p);
         }

      }, (long)(Integer)this.delay.getValue(), TimeUnit.MILLISECONDS);
   }
}
