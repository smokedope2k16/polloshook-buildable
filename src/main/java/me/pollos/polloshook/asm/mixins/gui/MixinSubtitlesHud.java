package me.pollos.polloshook.asm.mixins.gui;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.impl.events.render.Render2DEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SubtitlesHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SubtitlesHud.class})
public abstract class MixinSubtitlesHud {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   public void renderHook(DrawContext context, CallbackInfo info) {
      Render2DEvent event = new Render2DEvent(context);
      PollosHook.getEventBus().dispatch(event);
   }
}
