package me.pollos.polloshook.impl.module.misc.announcer;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.block.BreakBlockEvent;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerAction;
import net.minecraft.block.AirBlock;

public class ListenerBreakBlock extends ModuleListener<Announcer, BreakBlockEvent> {
   public ListenerBreakBlock(Announcer module) {
      super(module, BreakBlockEvent.class);
   }

   public void call(BreakBlockEvent event) {
      if ((Boolean)((Announcer)this.module).blocks.getValue()) {
         if (!(event.getBlock() instanceof AirBlock)) {
            if (event.getBlock() != ((Announcer)this.module).brokenBlock) {
               ((Announcer)this.module).brokenBlock = event.getBlock();
               ((Announcer)this.module).queued.replace(AnnouncerAction.BREAK, 1.0F);
            } else {
               ((Announcer)this.module).brokenBlock = event.getBlock();
               ((Announcer)this.module).addEvent(AnnouncerAction.BREAK);
            }
         }
      }
   }
}
