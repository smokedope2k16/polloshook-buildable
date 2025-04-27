package me.pollos.polloshook.impl.module.render.esp;

import java.util.Iterator;
import java.util.Map.Entry;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.render.esp.util.ChorusPos;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class ListenerSound extends ModuleListener<ESP, PacketEvent.Receive<PlaySoundS2CPacket>> {
   public ListenerSound(ESP module) {
      super(module, PacketEvent.Receive.class, PlaySoundS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlaySoundS2CPacket> event) {
      PlaySoundS2CPacket packet = (PlaySoundS2CPacket)event.getPacket();
      SoundEvent value = (SoundEvent)packet.getSound().value();
      Vec3d vec = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
      if (((ESP)this.module).ignoreVec == null || !(((ESP)this.module).ignoreVec[0].squaredDistanceTo(vec) < 1.0D) && !(((ESP)this.module).ignoreVec[1].squaredDistanceTo(vec) < 1.0D)) {
         Iterator var5 = ((ESP)this.module).ignoredPlayers.entrySet().iterator();

         Entry entry;
         do {
            if (!var5.hasNext()) {
               if (value == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT && packet.getCategory() == SoundCategory.PLAYERS) {
                  ((ESP)this.module).chorusFruits.add(new ChorusPos("Player Teleports", new Vec3d(packet.getX(), packet.getY(), packet.getZ()), System.currentTimeMillis()));
               }

               return;
            }

            entry = (Entry)var5.next();
         } while(((Vec3d)entry.getValue()).squaredDistanceTo(vec) > 1.0D);

      }
   }
}