package me.pollos.polloshook.impl.module.render.noweather;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.render.noweather.mode.WeatherMode;
import net.minecraft.world.biome.Biome.Precipitation;

public class NoWeather extends ToggleableModule {
   protected final EnumValue<WeatherMode> weather;
   protected final NumberValue<Integer> opacity;

   public NoWeather() {
      super(new String[]{"NoWeather", "weather"}, Category.RENDER);
      this.weather = new EnumValue(WeatherMode.CANCEL, new String[]{"Weather", "weth"});
      this.opacity = (new NumberValue(100, 1, 100, new String[]{"Opacity", "opac"})).withTag("%").setParent(this.weather, WeatherMode.CANCEL, true);
      this.offerValues(new Value[]{this.weather, this.opacity});
      this.offerListeners(new Listener[]{new ListenerWeather(this), new ListenerForceWeather(this), new ListenerAlpha(this)});
   }

   public static class WeatherAlphaEvent extends Event {
      private float alpha;

      
      public float getAlpha() {
         return this.alpha;
      }

      
      public void setAlpha(float alpha) {
         this.alpha = alpha;
      }
   }

   public static class ForceWeatherEvent extends Event {
      private Precipitation precipitation;

      
      public Precipitation getPrecipitation() {
         return this.precipitation;
      }

      
      public void setPrecipitation(Precipitation precipitation) {
         this.precipitation = precipitation;
      }

      
      public ForceWeatherEvent(Precipitation precipitation) {
         this.precipitation = precipitation;
      }
   }
}
