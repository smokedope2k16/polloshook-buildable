package me.pollos.polloshook.impl.module.misc.nobreakanim;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;

public class NoBreakAnim extends ToggleableModule {
   public NoBreakAnim() {
      super(new String[]{"NoBreakAnim", "nobreakanimation", "nomineanim", "mineanimation"}, Category.MISC);
      this.offerListeners(new Listener[]{new ListenerBreak(this)});
   }
}
