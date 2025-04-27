package me.pollos.polloshook.impl.module.misc.middleclick;

import java.util.ArrayList;
import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.util.binds.mouse.MouseButton;
import me.pollos.polloshook.impl.events.keyboard.MouseClickEvent;
import me.pollos.polloshook.impl.module.misc.middleclick.action.core.MiddleClickAction;

public class ListenerMouse extends SafeModuleListener<MiddleClick, MouseClickEvent> {
   private boolean pressed;

   public ListenerMouse(MiddleClick module) {
      super(module, MouseClickEvent.class);
   }

   public void safeCall(MouseClickEvent event) {
      if (this.pressed && event.getKey() == MouseButton.MIDDLE) {
         this.pressed = false;
      } else {
         ArrayList<MiddleClickAction> queue = new ArrayList();
         if (event.getKey() == MouseButton.MIDDLE && mc.currentScreen == null) {
            Iterator var3 = ((MiddleClick)this.module).actions.iterator();

            MiddleClickAction action;
            while(var3.hasNext()) {
               action = (MiddleClickAction)var3.next();
               if (action.check() && (Boolean)action.getParent().getValue()) {
                  queue.add(action);
               }
            }

            var3 = queue.iterator();

            while(true) {
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  action = (MiddleClickAction)var3.next();
               } while(queue.size() > 1 && action.getType() != ((MiddleClick)this.module).priority.getValue());

               this.pressed = true;
               action.run();
            }
         }
      }
   }
}
