package me.pollos.polloshook.asm.ducks.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IDrawContext {
   void drawItemInSlotCFont(TextRenderer var1, ItemStack var2, int var3, int var4, @Nullable String var5);
}
