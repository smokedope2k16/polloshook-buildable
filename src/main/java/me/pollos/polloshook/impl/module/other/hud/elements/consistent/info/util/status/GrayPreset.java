package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status;

import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.impl.StatusEffectPreset;
import net.minecraft.util.Formatting;

public class GrayPreset extends StatusEffectPreset {
   protected Formatting getTimeColor() {
      return Formatting.BOLD;
   }
}