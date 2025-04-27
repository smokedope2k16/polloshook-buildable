package me.pollos.polloshook.impl.module.misc.autoreconnect;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.misc.autoreconnect.mode.CensorMode;
import me.pollos.polloshook.impl.module.misc.autoreconnect.util.AutoReconnectSliderWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;

public class ListenerDisconnect extends ModuleListener<AutoReconnect, AutoReconnect.InitDisconnectScreenEvent> {
   public ListenerDisconnect(AutoReconnect module) {
      super(module, AutoReconnect.InitDisconnectScreenEvent.class);
   }

   public void call(AutoReconnect.InitDisconnectScreenEvent event) {
      ((AutoReconnect)this.module).timer.reset();
      ServerInfo info = event.getInfo();
      if (info != null) {
         String var10000;
         switch((CensorMode)((AutoReconnect)this.module).censor.getValue()) {
         case OFF:
            var10000 = "(" + info.address + ")";
            break;
         case ASTERISKS:
            var10000 = "(" + info.address.replaceAll("[keyCodec-zA-Z0-9]", "*") + ")";
            break;
         default:
            var10000 = "";
         }

         String serverName = var10000;
         ButtonWidget buttonWidget = ButtonWidget.builder(Text.literal("Reconnect %s".formatted(new Object[]{serverName})), (button) -> {
            Managers.getServerManager().reconnectToLastServer();
         }).width(200).build();
         event.getWidgetList().add(buttonWidget);
         Text reconnecting = Text.literal("Reconnecting in (%s)".formatted(new Object[]{((AutoReconnect)this.module).getTimeString()}));
         Text stopped = Text.literal("Click to resume");
         ((AutoReconnect)this.module).widget = ButtonWidget.builder(((AutoReconnect)this.module).timer.isPaused() ? stopped : reconnecting, (button) -> {
            ((AutoReconnect)this.module).timer.togglePause();
         }).width(200).build();
         event.getWidgetList().add(((AutoReconnect)this.module).widget);
         ((AutoReconnect)this.module).slider = new AutoReconnectSliderWidget(((AutoReconnect)this.module).delay);
         event.getWidgetList().add(((AutoReconnect)this.module).slider);
      }
   }
}
