package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.mode;


import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.GrayPreset;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.WhitePreset;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.brackets.BeefPreset;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.brackets.OGPreset;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.brackets.RomanPreset;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.brackets.WhiteBracketPreset;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.impl.StatusEffectPreset;

public enum StatusEffectMode {
   GRAY(new GrayPreset()),
   WHITE(new WhitePreset()),
   COLORLESS(new RomanPreset()),
   OG((new OGPreset()).setNoAmpFlag(true)),
   BEEF((new BeefPreset()).setNoAmpFlag(true)),
   BRACKET(new WhiteBracketPreset());

   private final StatusEffectPreset preset;

   
   public StatusEffectPreset getPreset() {
      return this.preset;
   }

   
   private StatusEffectMode(final StatusEffectPreset preset) {
      this.preset = preset;
   }

   // $FF: synthetic method
   private static StatusEffectMode[] $values() {
      return new StatusEffectMode[]{GRAY, WHITE, COLORLESS, OG, BEEF, BRACKET};
   }
}
