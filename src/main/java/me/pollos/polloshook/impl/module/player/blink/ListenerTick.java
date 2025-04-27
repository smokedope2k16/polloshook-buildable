package me.pollos.polloshook.impl.module.player.blink;

import java.util.Iterator;
import java.util.Map.Entry;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.player.blink.mode.BlinkMode;
import me.pollos.polloshook.impl.module.player.blink.mode.PulseMode;
import net.minecraft.network.packet.Packet;

public class ListenerTick extends ModuleListener<Blink, TickEvent> {
   public ListenerTick(Blink module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if (mc.player == null) {
         ((Blink)this.module).setEnabled(false);
      } else if (EntityUtil.isDead(mc.player)) {
         ((Blink)this.module).toggle();
      } else {
         switch((BlinkMode)((Blink)this.module).mode.getValue()) {
         case PULSE:
            switch((PulseMode)((Blink)this.module).pulse.getValue()) {
            case TIME:
               if (((Blink)this.module).timer.passed((long)(Integer)((Blink)this.module).delay.getValue())) {
                  ((Blink)this.module).clear(true);
                  ((Blink)this.module).timer.reset();
               }

               return;
            case DISTANCE:
               if (((Blink)this.module).lastVec3d == null) {
                  ((Blink)this.module).lastVec3d = mc.player.getPos();
                  return;
               }

               if (mc.player.squaredDistanceTo(((Blink)this.module).lastVec3d) > (double)MathUtil.square((Float)((Blink)this.module).distance.getValue())) {
                  ((Blink)this.module).clear(true);
                  ((Blink)this.module).lastVec3d = mc.player.getPos();
               }

               return;
            case PACKETS:
               if (((Blink)this.module).queue.size() > (Integer)((Blink)this.module).packets.getValue()) {
                  ((Blink)this.module).clear(true);
               }

               return;
            default:
               return;
            }
         case FAKE_LAG:
            Iterator iterator = ((Blink)this.module).fakeLagQueue.entrySet().iterator();

            while(iterator.hasNext()) {
               Entry<Packet<?>, Long> entry = (Entry)iterator.next();
               if (System.currentTimeMillis() - (Long)entry.getValue() > (long)(Integer)((Blink)this.module).delay.getValue()) {
                  PacketUtil.send((Packet)entry.getKey());
                  iterator.remove();
               }
            }
         }

      }
   }
}
