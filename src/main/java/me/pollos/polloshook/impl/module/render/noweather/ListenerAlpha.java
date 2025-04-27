package me.pollos.polloshook.impl.module.render.noweather;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerAlpha extends ModuleListener<NoWeather, NoWeather.WeatherAlphaEvent> {
   public ListenerAlpha(NoWeather module) {
      super(module, NoWeather.WeatherAlphaEvent.class);
   }

   public void call(NoWeather.WeatherAlphaEvent event) {
      if (((NoWeather)this.module).opacity.getParent().isVisible()) {
         event.setAlpha((float)(Integer)((NoWeather)this.module).opacity.getValue() / 100.0F);
         event.setCanceled(true);
      }
   }
}
