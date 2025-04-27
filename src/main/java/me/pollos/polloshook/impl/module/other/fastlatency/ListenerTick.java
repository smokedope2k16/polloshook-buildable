package me.pollos.polloshook.impl.module.other.fastlatency;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.client.network.PingMeasurer;

public class ListenerTick extends ModuleListener<FastLatency, TickEvent> {
   public ListenerTick(FastLatency module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if (mc.player != null && mc.player.networkHandler != null) {
         PingMeasurer measurer = ((FastLatency)this.module).measurer == null ? (((FastLatency)this.module).measurer = ((FastLatency)this.module).newPingMeasurer()) : ((FastLatency)this.module).measurer;
         if (((FastLatency)this.module).timer.passed((double)((Float)((FastLatency)this.module).delay.getValue() * 1000.0F)) || (Float)((FastLatency)this.module).delay.getValue() <= (Float)((FastLatency)this.module).delay.getMinimum()) {
            measurer.ping();
            ((FastLatency)this.module).timer.reset();
         }

      } else {
         ((FastLatency)this.module).killMeasurer();
      }
   }
}
