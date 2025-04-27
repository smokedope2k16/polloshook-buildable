package me.pollos.polloshook.asm.mixins.util;

import me.pollos.polloshook.impl.module.misc.nosoundlag.NoSoundLag;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SoundSystem.class})
public abstract class MixinSoundSystem {
   @Shadow
   public abstract void stop(SoundInstance var1);

   @Inject(
      method = {"play(Lnet/minecraft/client/sound/SoundInstance;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void playHook(SoundInstance sound, CallbackInfo ci) {
      NoSoundLag.PlaySoundEvent event = NoSoundLag.PlaySoundEvent.of(sound);
      event.dispatch();
      if (event.isCanceled()) {
         this.stop(sound);
         ci.cancel();
      }

      if (Manager.get().getUnfocusedSound() && !MinecraftClient.getInstance().isWindowFocused()) {
         ci.cancel();
      }

   }
}
