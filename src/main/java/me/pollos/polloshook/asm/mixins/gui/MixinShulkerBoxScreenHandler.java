package me.pollos.polloshook.asm.mixins.gui;

import me.pollos.polloshook.asm.ducks.gui.shulkerbox.IShulkerBoxScreenHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ShulkerBoxScreenHandler.class})
public class MixinShulkerBoxScreenHandler implements IShulkerBoxScreenHandler {
   @Shadow
   @Final
   private Inventory inventory;

   public Inventory getInventory() {
      return this.inventory;
   }
}
