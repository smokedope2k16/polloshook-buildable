package me.pollos.polloshook.impl.module.render.noweather;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.WeatherEvent;
import me.pollos.polloshook.impl.module.render.noweather.mode.WeatherMode;

public class ListenerWeather extends ModuleListener<NoWeather, WeatherEvent> {
   public ListenerWeather(NoWeather module) {
      super(module, WeatherEvent.class);
   }

   public void call(WeatherEvent event) {
      if (((NoWeather)this.module).weather.getValue() == WeatherMode.CANCEL) {
         event.setCanceled(true);
      }

   }
}
