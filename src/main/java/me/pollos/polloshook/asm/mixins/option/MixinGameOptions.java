package me.pollos.polloshook.asm.mixins.option;

import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.ValidatingIntSliderCallbacks;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({GameOptions.class})
public abstract class MixinGameOptions {
   @Final
   @Shadow
   private final SimpleOption<Integer> fov;

   public MixinGameOptions() {
      this.fov = new SimpleOption("options.fov", SimpleOption.emptyTooltip(), (optionText, value) -> {
         Text var10000;
         int intValue = (Integer) value;
         switch(intValue) {
            case 70:
               var10000 = GameOptions.getGenericValueText(optionText, Text.translatable("options.fov.min"));
               break;
            case 110:
               var10000 = GameOptions.getGenericValueText(optionText, Text.translatable("options.fov.max"));
               break;
            default:
               var10000 = GameOptions.getGenericValueText(optionText, (Text) value);
         }

         return var10000;
      }, new ValidatingIntSliderCallbacks(30, 180), Codec.DOUBLE.xmap((value) -> {
         return (int)(value * 40.0D + 70.0D);
      }, (value) -> {
         return ((double)value - 70.0D) / 40.0D;
      }), 70, (value) -> {
         MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
      });
   }
}
