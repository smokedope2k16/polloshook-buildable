package me.pollos.polloshook.impl.module.misc.nosoundlag;

import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.misc.nosoundlag.util.CountedSound;

public class ListenerUpdate extends ModuleListener<NoSoundLag, UpdateEvent> {
   public ListenerUpdate(NoSoundLag module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      Iterator var2 = ((NoSoundLag)this.module).sounds.iterator();

      while(var2.hasNext()) {
         CountedSound countedSound = (CountedSound)var2.next();
         if ((float)(System.currentTimeMillis() - countedSound.getLastPlayed()) > ((NoSoundLag)this.module).getSoundDuration(countedSound.getIdentifier()) + 25.0F * (Float)((NoSoundLag)this.module).factor.getValue()) {
            countedSound.decrease();
         }
      }

   }
}
