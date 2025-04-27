package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.brackets;

import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.impl.BracketedPreset;
import net.minecraft.util.Formatting;

public class BeefPreset extends BracketedPreset {
   protected Formatting getBracketFormatting() {
      return Formatting.BOLD;
   }

   protected String getLeftBracket() {
      return "[";
   }

   protected String getRightBracket() {
      return "]";
   }

   protected Formatting getTimeColor() {
      return Formatting.GRAY;
   }

   protected String getTimeString() {
      String var10000 = String.valueOf(Formatting.BOLD);
      return "<amp>" + var10000 + "|" + String.valueOf(Formatting.GRAY) + "<time>";
   }
}