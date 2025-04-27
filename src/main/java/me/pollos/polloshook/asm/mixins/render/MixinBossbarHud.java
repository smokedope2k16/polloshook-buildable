package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BossBarHud.class})
public class MixinBossbarHud {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderHook(DrawContext context, CallbackInfo ci) {
      NoRender.BossbarEvent event = NoRender.BossbarEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }
}
