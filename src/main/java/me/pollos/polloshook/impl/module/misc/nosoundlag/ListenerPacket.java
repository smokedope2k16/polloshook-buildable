package me.pollos.polloshook.impl.module.misc.nosoundlag;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.misc.nosoundlag.util.NoSoundMode;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

public class ListenerPacket extends ModuleListener<NoSoundLag, PacketEvent.Receive<PlaySoundS2CPacket>> {
   public ListenerPacket(NoSoundLag module) {
      super(module, PacketEvent.Receive.class, PlaySoundS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlaySoundS2CPacket> event) {
      if (((NoSoundLag)this.module).mode.getValue() != NoSoundMode.SPAM) {
         PlaySoundS2CPacket packet = (PlaySoundS2CPacket)event.getPacket();
         if (((NoSoundLag)this.module).packetSounds.contains(packet.getSound())) {
            event.setCanceled(true);
         }

      }
   }
}
