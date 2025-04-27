package me.pollos.polloshook.impl.module.misc.middleclick;

import java.util.ArrayList;
import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.world.PickBlockEvent;
import me.pollos.polloshook.impl.module.misc.middleclick.action.core.MiddleClickAction;

public class ListenerPickBlock extends ModuleListener<MiddleClick, PickBlockEvent> {
   public ListenerPickBlock(MiddleClick module) {
      super(module, PickBlockEvent.class);
   }

   public void call(PickBlockEvent event) {
      if ((Boolean)((MiddleClick)this.module).noPickBlock.getValue()) {
         ArrayList<MiddleClickAction> queue = new ArrayList();
         if (mc.currentScreen == null) {
            Iterator var3 = ((MiddleClick)this.module).actions.iterator();

            while(var3.hasNext()) {
               MiddleClickAction middleClickAction = (MiddleClickAction)var3.next();
               if (middleClickAction.check() && (Boolean)middleClickAction.getParent().getValue()) {
                  queue.add(middleClickAction);
               }
            }

            if (queue.isEmpty()) {
               return;
            }

            event.setCanceled(true);
         }

      }
   }
}
