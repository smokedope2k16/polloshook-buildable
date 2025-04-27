package me.pollos.polloshook.asm.mixins.option;

import me.pollos.polloshook.asm.ducks.util.IKeybinding;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({KeyBinding.class})
public abstract class MixinKeybinding implements IKeybinding {
   @Shadow
   private int timesPressed;

   public void setWasPressed(boolean bool) {
      if (bool) {
         this.timesPressed = 0;
      } else {
         this.timesPressed = 1;
      }

   }
}
