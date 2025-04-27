package me.pollos.polloshook.impl.module.misc.announcer;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.item.FinishUsingItemEvent;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerAction;
import net.minecraft.item.ItemStack;

public class ListenerFinishUse extends ModuleListener<Announcer, FinishUsingItemEvent> {
   public ListenerFinishUse(Announcer module) {
      super(module, FinishUsingItemEvent.class);
   }

   public void call(FinishUsingItemEvent event) {
      if (event.getEntity().equals(mc.player) && (Boolean)((Announcer)this.module).eat.getValue()) {
         ItemStack stack = event.getStack();
         if (((Announcer)this.module).isValid(stack)) {
            if (((Announcer)this.module).foodStack != null && ((Announcer)this.module).foodStack.getItem() != event.getStack().getItem()) {
               ((Announcer)this.module).foodStack = event.getStack();
               ((Announcer)this.module).queued.replace(AnnouncerAction.EAT, 1.0F);
               return;
            }

            ((Announcer)this.module).foodStack = event.getStack();
            ((Announcer)this.module).addEvent(AnnouncerAction.EAT);
         }
      }

   }
}
