package me.pollos.polloshook.impl.module.other.clickgui;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.gui.ScreenEvent;
import me.pollos.polloshook.impl.gui.click.ClickGuiScreen;

public class ListenerScreen extends ModuleListener<ClickGUI, ScreenEvent> {
   public ListenerScreen(ClickGUI module) {
      super(module, ScreenEvent.class);
   }

   public void call(ScreenEvent event) {
      if (!(event.getScreen() instanceof ClickGuiScreen)) {
         ((ClickGUI)this.module).toggle();
      }

   }
}
