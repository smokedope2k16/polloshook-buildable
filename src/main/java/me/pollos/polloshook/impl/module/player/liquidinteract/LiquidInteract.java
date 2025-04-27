package me.pollos.polloshook.impl.module.player.liquidinteract;

import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;

public class LiquidInteract extends ToggleableModule {
   public LiquidInteract() {
      super(new String[]{"LiquidInteract", "liquidinteraction", "liquids"}, Category.PLAYER);
   }
}
