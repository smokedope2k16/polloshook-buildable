package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.api.managers.Managers;
import net.minecraft.client.render.RenderTickCounter.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Dynamic.class})
public abstract class MixinRenderTickCounter$Dynamic {
   @Shadow
   private float lastFrameDuration;

   @Inject(
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;prevTimeMillis:J",
   opcode = 181,
   ordinal = 0
)},
      method = {"beginRenderTick(J)I"}
   )
   private void beginRenderTickHook(long keyCodec, CallbackInfoReturnable<Integer> info) {
      this.lastFrameDuration *= Managers.getTimerManager().getTimer();
   }
}
