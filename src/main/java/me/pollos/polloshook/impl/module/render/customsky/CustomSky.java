package me.pollos.polloshook.impl.module.render.customsky;

import java.awt.Color;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.preset.Preset;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.render.customsky.mode.CustomSkyMode;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.util.Formatting;

public class CustomSky extends ToggleableModule {
   private final EnumValue<CustomSkyMode> render;
   private final ColorValue skyColor;
   private final ColorValue fogColor;
   private final Value<Boolean> timeChanger;
   private final NumberValue<Integer> time;
   private final Value<Boolean> noSunMoon;
   private final Value<Boolean> noStars;
   private final Value<Boolean> customFogRange;
   private final NumberValue<Float> range;

   public CustomSky() {
      super(new String[]{"CustomSky", "timechanger"}, Category.RENDER);
      this.render = new EnumValue(CustomSkyMode.ONLY_FOG, new String[]{"Mode", "type", "custommode"});
      this.skyColor = (new ColorValue(new Color(-1), true, new String[]{"SkyColor", "skycolour"})).setParent(() -> {
         return this.render.getValue() == CustomSkyMode.ONLY_SKY || this.render.getValue() == CustomSkyMode.BOTH;
      });
      this.fogColor = (new ColorValue(new Color(-1), true, new String[]{"FogColor", "fogcolor"})).setParent(() -> {
         return this.render.getValue() == CustomSkyMode.ONLY_FOG || this.render.getValue() == CustomSkyMode.BOTH;
      });
      this.timeChanger = new Value(false, new String[]{"TimeChanger", "time"});
      this.time = (new NumberValue(12000, 0, 24000, new String[]{"Time", "worldtime"})).setParent(this.timeChanger).withTag("timechanger");
      this.noSunMoon = new Value(false, new String[]{"NoSun/Moon", "nosunandmoon", "nosun", "nomoon"});
      this.noStars = new Value(false, new String[]{"NoStars", "stars", "removestars"});
      this.customFogRange = new Value(false, new String[]{"CustomFogRange", "cfogrange", "fogrange"});
      this.range = (new NumberValue(1.0F, 1.0F, 10.0F, 0.25F, new String[]{"Range", "rang", "r", "rango"})).setParent(this.customFogRange).withTag("range");
      this.offerValues(new Value[]{this.render, this.skyColor, this.fogColor, this.timeChanger, this.time, this.noSunMoon, this.noStars, this.customFogRange, this.range});
      this.offerListeners(new Listener[]{new ListenerStars(this), new ListenerSun(this), new ListenerTick(this)});
      this.offerPresets(new Preset[]{new Preset(new String[]{"Day"}) {
         public void execute() {
            CustomSky.this.time.setValue((int)1000);
         }
      }, new Preset(new String[]{"Night"}) {
         public void execute() {
            CustomSky.this.time.setValue((int)17500);
         }
      }, new Preset(new String[]{"Sunset"}) {
         public void execute() {
            CustomSky.this.time.setValue((int)13500);
         }
      }, new Preset(new String[]{"Sunrise"}) {
         public void execute() {
            CustomSky.this.time.setValue((int)23500);
         }
      }});
      this.customFogRange.addObserver((o) -> {
         NoRender NO_RENDER = (NoRender)Managers.getModuleManager().get(NoRender.class);
         if (NO_RENDER != null && (Boolean)NO_RENDER.getFog().getValue() && (Boolean)o.getValue()) {
            ClientLogger.getLogger().log(String.valueOf(Formatting.RED) + "Disable: NoRender - Fog");
            o.setCanceled(true);
         }

      });
   }

   public boolean isAnyMode() {
      return this.isEnabled() && this.render.getValue() != CustomSkyMode.OFF;
   }

   public boolean isSkyMode() {
      boolean isSky = this.render.getValue() == CustomSkyMode.ONLY_SKY || this.render.getValue() == CustomSkyMode.BOTH;
      return this.isEnabled() && isSky;
   }

   public boolean isFogMode() {
      boolean isFog = this.render.getValue() == CustomSkyMode.ONLY_FOG || this.render.getValue() == CustomSkyMode.BOTH;
      return this.isEnabled() && isFog;
   }

   
   public EnumValue<CustomSkyMode> getRender() {
      return this.render;
   }

   
   public ColorValue getSkyColor() {
      return this.skyColor;
   }

   
   public ColorValue getFogColor() {
      return this.fogColor;
   }

   
   public Value<Boolean> getTimeChanger() {
      return this.timeChanger;
   }

   
   public NumberValue<Integer> getTime() {
      return this.time;
   }

   
   public Value<Boolean> getNoSunMoon() {
      return this.noSunMoon;
   }

   
   public Value<Boolean> getNoStars() {
      return this.noStars;
   }

   
   public Value<Boolean> getCustomFogRange() {
      return this.customFogRange;
   }

   
   public NumberValue<Float> getRange() {
      return this.range;
   }

   public static class RenderStarsEvent extends Event {
      
      private RenderStarsEvent() {
      }

      
      public static CustomSky.RenderStarsEvent create() {
         return new CustomSky.RenderStarsEvent();
      }
   }

   public static class RenderSunOrMoonEvent extends Event {
      
      private RenderSunOrMoonEvent() {
      }

      
      public static CustomSky.RenderSunOrMoonEvent create() {
         return new CustomSky.RenderSunOrMoonEvent();
      }
   }
}
