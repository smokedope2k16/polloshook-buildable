package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.brackets;

import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.impl.BracketedPreset;
import net.minecraft.util.Formatting;

public class OGPreset extends BracketedPreset {
   protected Formatting getBracketFormatting() {
      return Formatting.RESET;
   }

   protected String getLeftBracket() {
      return "(";
   }

   protected String getRightBracket() {
      return ")";
   }

   protected Formatting getTimeColor() {
      return Formatting.RESET;
   }

   protected String getTimeString() {
      return "<amp> / <time>";
   }
}