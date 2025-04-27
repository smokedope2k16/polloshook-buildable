package me.pollos.polloshook.impl.module.misc.payloadspoof;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;

public class PayloadSpoof extends ToggleableModule {
   public PayloadSpoof() {
      super(new String[]{"PayloadSpoof", "serverspoof", "nohandshake"}, Category.MISC);
      this.offerListeners(new Listener[]{new ListenerCustomPayload(this)});
      this.setEnabled(true);
   }
}
