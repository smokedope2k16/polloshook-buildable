package me.pollos.polloshook.impl.module.misc.autoreconnect;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.text.Text;

public class ListenerTick extends ModuleListener<AutoReconnect, TickEvent> {
   public ListenerTick(AutoReconnect module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if (((AutoReconnect)this.module).widget != null) {
         if (mc.currentScreen instanceof DisconnectedScreen) {
            Text reconnecting = Text.literal("Reconnecting in (%s)".formatted(new Object[]{((AutoReconnect)this.module).getTimeString()}));
            if (!((AutoReconnect)this.module).timer.isPaused()) {
               ((AutoReconnect)this.module).widget.setMessage(reconnecting);
            } else {
               ((AutoReconnect)this.module).widget.setMessage(Text.literal("Click to resume"));
            }

            ((AutoReconnect)this.module).slider.active = ((AutoReconnect)this.module).timer.isPaused();
            if (((AutoReconnect)this.module).timer.passed((double)((Float)((AutoReconnect)this.module).delay.getValue() * 1000.0F)) && !((AutoReconnect)this.module).timer.isPaused()) {
               Managers.getServerManager().reconnectToLastServer();
            }
         }

      }
   }
}
