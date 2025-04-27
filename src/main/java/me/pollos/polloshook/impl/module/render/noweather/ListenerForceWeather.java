package me.pollos.polloshook.impl.module.render.noweather;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.module.render.noweather.mode.WeatherMode;
import net.minecraft.world.biome.Biome.Precipitation;

public class ListenerForceWeather extends ModuleListener<NoWeather, NoWeather.ForceWeatherEvent> {
   public ListenerForceWeather(NoWeather module) {
      super(module, NoWeather.ForceWeatherEvent.class);
   }

   public void call(NoWeather.ForceWeatherEvent event) {
      if (((NoWeather)this.module).opacity.getParent().isVisible()) {
         event.setPrecipitation(((NoWeather)this.module).weather.getValue() == WeatherMode.RAIN ? Precipitation.RAIN : Precipitation.SNOW);
         event.setCanceled(true);
      }
   }
}
