package me.pollos.polloshook.impl.module.render.oldpotions;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.render.oldpotions.mode.OldPotionsMode;

public class OldPotions extends ToggleableModule {
   protected final EnumValue<OldPotionsMode> mode;

   public OldPotions() {
      super(new String[]{"OldPotions", "oldpots"}, Category.RENDER);
      this.mode = new EnumValue(OldPotionsMode.ALL, new String[]{"Mode", "m", "type", "t"});
      this.offerValues(new Value[]{this.mode});
      this.offerListeners(new Listener[]{new ListenerOldColor(this), new ListenerOldGlint(this)});
   }

   public static class OldGlintEvent extends Event {
      
      private OldGlintEvent() {
      }

      
      public static OldPotions.OldGlintEvent create() {
         return new OldPotions.OldGlintEvent();
      }
   }

   public static class OldColorEvent extends Event {
      
      private OldColorEvent() {
      }

      
      public static OldPotions.OldColorEvent create() {
         return new OldPotions.OldColorEvent();
      }
   }
}
