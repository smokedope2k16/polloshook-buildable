package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.impl;


import net.minecraft.util.Formatting;

public abstract class StatusEffectPreset {
   protected boolean noAmpFlag = false;

   protected abstract Formatting getTimeColor();

   protected String getTimeString() {
      return "<time>";
   }

   public String build() {
      String var10000;
      if (this instanceof BracketedPreset) {
         BracketedPreset bracketedPreset = (BracketedPreset)this;
         var10000 = String.valueOf(bracketedPreset.getBracketFormatting());
         return " " + var10000 + bracketedPreset.getLeftBracket() + String.valueOf(this.getTimeColor()) + this.getTimeString() + String.valueOf(bracketedPreset.getBracketFormatting()) + bracketedPreset.getRightBracket();
      } else {
         var10000 = String.valueOf(this.getTimeColor());
         return ": " + var10000 + this.getTimeString();
      }
   }

   
   public boolean isNoAmpFlag() {
      return this.noAmpFlag;
   }

   
   public StatusEffectPreset setNoAmpFlag(boolean noAmpFlag) {
      this.noAmpFlag = noAmpFlag;
      return this;
   }
}
