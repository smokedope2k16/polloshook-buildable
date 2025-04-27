package me.pollos.polloshook.impl.module.player.xcarry;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;

public class XCarry extends ToggleableModule {
   protected final Value<Boolean> forceCancel = new Value(false, new String[]{"ForceCancel", "force"});

   public XCarry() {
      super(new String[]{"XCarry", "extracarry", "carry"}, Category.PLAYER);
      this.offerValues(new Value[]{this.forceCancel});
      this.offerListeners(new Listener[]{new ListenerWindow(this)});
   }
}
