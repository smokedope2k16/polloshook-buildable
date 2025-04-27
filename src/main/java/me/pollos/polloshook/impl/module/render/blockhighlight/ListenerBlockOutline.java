package me.pollos.polloshook.impl.module.render.blockhighlight;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.BlockOutlineEvent;

public class ListenerBlockOutline extends ModuleListener<BlockHighlight, BlockOutlineEvent> {
   public ListenerBlockOutline(BlockHighlight module) {
      super(module, BlockOutlineEvent.class);
   }

   public void call(BlockOutlineEvent event) {
      event.setCanceled(true);
   }
}
