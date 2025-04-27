package me.pollos.polloshook.asm.ducks.gui;

import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public interface IHandledScreen {
   Slot slotAt(double var1, double var3);

   void onClicked(Slot var1, int var2, int var3, SlotActionType var4);

   boolean isMouseClicked();
}
