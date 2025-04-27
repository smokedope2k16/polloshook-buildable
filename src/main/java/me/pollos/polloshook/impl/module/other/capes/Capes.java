package me.pollos.polloshook.impl.module.other.capes;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.other.capes.mode.CapeMode;
import me.pollos.polloshook.impl.module.other.capes.mode.SelfCapeMode;

public class Capes extends ToggleableModule {
   protected final EnumValue<CapeMode> capes;
   protected final EnumValue<SelfCapeMode> self;

   public Capes() {
      super(new String[]{"Capes", "cape"}, Category.OTHER);
      this.capes = new EnumValue(CapeMode.CUSTOM, new String[]{"Capes", "c"});
      this.self = (new EnumValue(SelfCapeMode.AUTO, new String[]{"Self", "s"})).setParent(this.capes, CapeMode.CUSTOM);
      this.offerValues(new Value[]{this.capes, this.self});
      this.offerListeners(new Listener[]{new ListenerCape(this)});
   }
}
