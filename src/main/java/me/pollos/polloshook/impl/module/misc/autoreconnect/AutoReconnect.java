package me.pollos.polloshook.impl.module.misc.autoreconnect;

import java.util.ArrayList;
import java.util.List;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.misc.autoreconnect.mode.CensorMode;
import me.pollos.polloshook.impl.module.misc.autoreconnect.util.AutoReconnectSliderWidget;
import me.pollos.polloshook.impl.module.misc.autoreconnect.util.PauseableStopWatch;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.network.ServerInfo;

public class AutoReconnect extends ToggleableModule {
   protected final NumberValue<Float> delay = (new NumberValue(1.0F, 0.0F, 100.0F, 0.1F, new String[]{"Delay", "del", "d"})).setNoLimit(true).withTag("second");
   protected final EnumValue<CensorMode> censor;
   protected ButtonWidget widget;
   protected AutoReconnectSliderWidget slider;
   protected final PauseableStopWatch timer;

   public AutoReconnect() {
      super(new String[]{"AutoReconnect", "reconnect", "autoconnect"}, Category.MISC);
      this.censor = new EnumValue(CensorMode.REMOVE, new String[]{"Censor", "cen", "s"});
      this.timer = new PauseableStopWatch();
      this.offerValues(new Value[]{this.delay, this.censor});
      this.offerListeners(new Listener[]{new ListenerDisconnect(this), new ListenerTick(this)});
   }

   public void onGameJoin() {
      this.timer.reset();
   }

   protected String getTimeString() {
      float time = ((Float)this.delay.getValue() * 1000.0F - (float)this.timer.getTime()) / 1000.0F;
      return time < 0.0F ? "Queued" : String.format("%.1fs", time);
   }

   public static class InitDisconnectScreenEvent extends Event {
      private final ServerInfo info;
      private final List<Widget> widgetList = new ArrayList();

      
      public ServerInfo getInfo() {
         return this.info;
      }

      
      public List<Widget> getWidgetList() {
         return this.widgetList;
      }

      
      public InitDisconnectScreenEvent(ServerInfo info) {
         this.info = info;
      }
   }
}
