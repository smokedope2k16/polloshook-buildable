package me.pollos.polloshook.impl.module.misc.noquitdesync;

import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;

public class NoQuitDesync extends ToggleableModule {
   public NoQuitDesync() {
      super(new String[]{"NoQuitDesync", "nodesync"}, Category.MISC);
   }
}
