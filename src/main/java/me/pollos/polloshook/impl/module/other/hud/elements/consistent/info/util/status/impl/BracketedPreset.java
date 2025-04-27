package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.impl;

import net.minecraft.util.Formatting;

public abstract class BracketedPreset extends StatusEffectPreset {
   protected abstract Formatting getBracketFormatting();

   protected abstract String getLeftBracket();

   protected abstract String getRightBracket();
}
