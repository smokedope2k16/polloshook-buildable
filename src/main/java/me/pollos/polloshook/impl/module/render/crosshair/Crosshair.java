package me.pollos.polloshook.impl.module.render.crosshair;

import java.awt.Color;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class Crosshair extends ToggleableModule {
   protected final Value<Boolean> thirdPerson = new Value(false, new String[]{"3rdPerson", "thirdperson"});
   protected final Value<Boolean> movementError = new Value(false, new String[]{"MovementError", "moveerror"});
   protected final NumberValue<Float> factor;
   protected final Value<Boolean> outline;
   protected final NumberValue<Float> outlineWidth;
   protected final ColorValue outlineColor;
   protected final NumberValue<Float> width;
   protected final NumberValue<Float> gap;
   protected final NumberValue<Float> length;
   protected final ColorValue color;

   public Crosshair() {
      super(new String[]{"Crosshair", "chair", "crosshairmodify"}, Category.RENDER);
      this.factor = (new NumberValue(3.0F, 1.0F, 10.0F, 0.1F, new String[]{"Factor", "fac", "factoiders"})).setParent(this.movementError);
      this.outline = new Value(false, new String[]{"Outline", "line"});
      this.outlineWidth = (new NumberValue(0.5F, 0.5F, 1.0F, 0.1F, new String[]{"LineWidth", "width"})).setParent(this.outline);
      this.outlineColor = (new ColorValue(Color.BLACK, false, new String[]{"OutlineColor", "linecolor"})).setParent(this.outline);
      this.width = new NumberValue(1.0F, 1.0F, 5.0F, 0.1F, new String[]{"Width", "w"});
      this.gap = new NumberValue(3.0F, 0.5F, 10.0F, 0.5F, new String[]{"Gap", "g", "gapple"});
      this.length = new NumberValue(7.0F, 0.5F, 15.0F, 0.5F, new String[]{"Length", "girth", "veinyahhdih"});
      this.color = new ColorValue(Color.WHITE, false, new String[]{"Color", "c"});
      this.offerValues(new Value[]{this.thirdPerson, this.movementError, this.factor, this.outline, this.outlineWidth, this.outlineColor, this.width, this.gap, this.length, this.color});
      this.offerListeners(new Listener[]{new ListenerRender(this), new ListenerRenderCrosshair(this)});
   }

   public static class RenderCrosshairEvent extends Event {
      
      private RenderCrosshairEvent() {
      }

      
      public static Crosshair.RenderCrosshairEvent create() {
         return new Crosshair.RenderCrosshairEvent();
      }
   }
}
