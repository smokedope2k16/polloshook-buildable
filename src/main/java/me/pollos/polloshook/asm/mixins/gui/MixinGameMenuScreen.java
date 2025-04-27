package me.pollos.polloshook.asm.mixins.gui;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.module.misc.noquitdesync.NoQuitDesync;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameMenuScreen.class})
public class MixinGameMenuScreen {
   @Inject(
      method = {"disconnect"},
      at = {@At("HEAD")}
   )
   private void disconnectHook(CallbackInfo info) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (!client.isInSingleplayer()) {
         if (((NoQuitDesync)Managers.getModuleManager().get(NoQuitDesync.class)).isEnabled()) {
            PacketUtil.send(new UpdateSelectedSlotC2SPacket(-1));
         }

      }
   }
}
