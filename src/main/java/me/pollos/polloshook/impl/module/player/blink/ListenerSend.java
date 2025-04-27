package me.pollos.polloshook.impl.module.player.blink;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.player.blink.mode.BlinkMode;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class ListenerSend extends SafeModuleListener<Blink, PacketEvent.Send<?>> {
   public ListenerSend(Blink module) {
      super(module, PacketEvent.Send.class);
   }

   public void safeCall(PacketEvent.Send<?> event) {
      if (mc.player != null && !mc.player.isDead() && !mc.isInSingleplayer()) {
         Packet<?> packet = event.getPacket();
         boolean blinkPacket = ((Blink)this.module).fakeLagQueue.containsKey(packet) || ((Blink)this.module).mode.getValue() != BlinkMode.FAKE_LAG && ((Blink)this.module).queue.contains(packet);
         if (!Blink.isBadPacket(packet) && !blinkPacket && ((Boolean)((Blink)this.module).allPackets.getValue() || packet instanceof PlayerMoveC2SPacket)) {
            switch((BlinkMode)((Blink)this.module).mode.getValue()) {
            case CONSTANT:
            case PULSE:
               ((Blink)this.module).queue.add(packet);
               break;
            case FAKE_LAG:
               ((Blink)this.module).fakeLagQueue.put(packet, System.currentTimeMillis());
            }

            event.setCanceled(true);
         }
      }
   }
}
