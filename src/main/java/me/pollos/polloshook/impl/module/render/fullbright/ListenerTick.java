package me.pollos.polloshook.impl.module.render.fullbright;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.render.fullbright.mode.FullbrightMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ListenerTick extends SafeModuleListener<Fullbright, TickEvent> {
   public ListenerTick(Fullbright module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      if (((Fullbright)this.module).mode.getValue() != FullbrightMode.NIGHT_VISION && 
          MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.NIGHT_VISION) != null) {
          MinecraftClient.getInstance().player.removeStatusEffect(StatusEffects.NIGHT_VISION);
      }
  
      switch ((FullbrightMode) ((Fullbright) this.module).mode.getValue()) {
          case NIGHT_VISION:
              MinecraftClient.getInstance().player.addStatusEffect(
                  new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false));
              break;
          case GAMMA:
              try {
                  java.lang.reflect.Field gammaField = GameOptions.class.getDeclaredField("gamma");
                  gammaField.setAccessible(true);
                  SimpleOption<Double> gammaOption = (SimpleOption<Double>) gammaField.get(MinecraftClient.getInstance().options);
                  gammaOption.setValue(1000.0D);
              } catch (Exception e) {}
              break;
         default:
            break;
      }
  }
}
