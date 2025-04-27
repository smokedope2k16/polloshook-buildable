package me.pollos.polloshook.asm.mixins.util;

import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NarratorManager.class})
public class MixinNarratorManager {
   @Inject(
      method = {"narrate(Ljava/lang/String;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void narrateHook(String text, CallbackInfo ci) {
      if (Manager.get().getNoNarrator()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"narrateChatMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void narrateChatMessageHook(Text text, CallbackInfo ci) {
      if (Manager.get().getNoNarrator()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"narrateSystemMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void narrateSystemMessage(Text text, CallbackInfo ci) {
      if (Manager.get().getNoNarrator()) {
         ci.cancel();
      }

   }
}
