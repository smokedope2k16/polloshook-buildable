package me.pollos.polloshook.impl.module.render.freecam;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import net.minecraft.client.input.KeyboardInput;

public class ListenerTickInput extends ModuleListener<Freecam, Freecam.TickInputEvent> {
   public ListenerTickInput(Freecam module) {
      super(module, Freecam.TickInputEvent.class);
   }

   public void call(Freecam.TickInputEvent event) {
      if (mc.player.input.getClass() == KeyboardInput.class) {
         mc.player.input = ((Freecam)this.module).input;
      }

   }
}
