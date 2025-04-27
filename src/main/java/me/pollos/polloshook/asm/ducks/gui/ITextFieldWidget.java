package me.pollos.polloshook.asm.ducks.gui;

import net.minecraft.client.font.TextRenderer;

public interface ITextFieldWidget {
   TextRenderer textRenderer();

   boolean canEdit();

   void hook$erase(int var1);
}
