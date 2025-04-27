package me.pollos.polloshook.asm.mixins.option;

import java.util.function.Consumer;
import me.pollos.polloshook.asm.ducks.util.ISimpleOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({SimpleOption.class})
public class MixinSimpleOption<T> implements ISimpleOption<T> {
   @Shadow
   T value;
   @Shadow
   @Final
   private Consumer<T> changeCallback;

   public void setValue(T newValue) {
      if (!MinecraftClient.getInstance().isRunning()) {
         this.value = newValue;
      } else {
         if (!this.value.equals(newValue)) {
            this.value = newValue;
            this.changeCallback.accept(this.value);
         }

      }
   }
}
