package me.pollos.polloshook.impl.module.misc.nosoundlag;

import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.module.misc.nosoundlag.util.CountedSound;
import me.pollos.polloshook.impl.module.misc.nosoundlag.util.NoSoundMode;
import net.minecraft.client.sound.SoundInstance;

public class ListenerSound extends ModuleListener<NoSoundLag, NoSoundLag.PlaySoundEvent> {
   public ListenerSound(NoSoundLag module) {
      super(module, NoSoundLag.PlaySoundEvent.class);
   }

   public void call(NoSoundLag.PlaySoundEvent event) {
      if (((NoSoundLag)this.module).mode.getValue() != NoSoundMode.PACKET) {
         SoundInstance instance = event.getInstance();
         CountedSound counting = null;
         Iterator var4 = ((NoSoundLag)this.module).sounds.iterator();

         CountedSound cs;
         while(var4.hasNext()) {
            cs = (CountedSound)var4.next();
            if (cs != null && cs.isEqualSound(instance.getId())) {
               counting = cs;
               break;
            }
         }

         if (counting == null) {
            counting = new CountedSound(instance.getId());
            ((NoSoundLag)this.module).sounds.add(counting);
         }

         var4 = ((NoSoundLag)this.module).sounds.iterator();

         while(var4.hasNext()) {
            cs = (CountedSound)var4.next();
            if (cs != null && cs != counting && cs.isEqualSound(instance.getId())) {
               counting.merge(cs);
               ((NoSoundLag)this.module).sounds.remove(cs);
               break;
            }
         }

         counting.increase();
         if (counting.getCount() > (Integer)((NoSoundLag)this.module).threshold.getValue()) {
            event.setCanceled(true);
         } else {
            counting.setLastPlayed(System.currentTimeMillis());
         }
      }
   }
}
