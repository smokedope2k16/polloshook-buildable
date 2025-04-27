package me.pollos.polloshook.impl.module.misc.announcer;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.block.PlaceBlockEvent;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerAction;
import net.minecraft.block.AirBlock;

public class ListenerPlaceBlock extends ModuleListener<Announcer, PlaceBlockEvent> {
   public ListenerPlaceBlock(Announcer module) {
      super(module, PlaceBlockEvent.class);
   }

   public void call(PlaceBlockEvent event) {
      if ((Boolean)((Announcer)this.module).place.getValue()) {
         if (!(event.getBlock() instanceof AirBlock)) {
            if (event.getBlock() != ((Announcer)this.module).placeBlock) {
               ((Announcer)this.module).placeBlock = event.getBlock();
               ((Announcer)this.module).queued.replace(AnnouncerAction.PLACE, 1.0F);
            } else {
               ((Announcer)this.module).placeBlock = event.getBlock();
               ((Announcer)this.module).addEvent(AnnouncerAction.PLACE);
            }
         }
      }
   }
}
