package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.impl.module.render.chams.Chams;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({OverlayTexture.class})
public class MixinOverlayTexture {
   @Shadow
   @Final
   private NativeImageBackedTexture texture;

   @ModifyArg(
      method = {"<init>"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V",
   ordinal = 0
),
      index = 2
   )
   private int initHook(int x) {
      Chams.cancer = this.texture;
      return x;
   }
}
