package me.pollos.polloshook.asm.mixins.gui;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.render.betterchat.BetterChat;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Visible.class})
public class MixinChatHudLineVisible {
   @Inject(
      method = {"indicator"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void indicatorHook(CallbackInfoReturnable<MessageIndicator> cir) {
      if (((BetterChat)Managers.getModuleManager().get(BetterChat.class)).getNoIndicator()) {
         cir.setReturnValue((MessageIndicator)null);
      }

   }
}
