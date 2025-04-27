package me.pollos.polloshook.asm.mixins.util;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.util.binds.mouse.MouseUtil;
import me.pollos.polloshook.impl.events.keyboard.MouseClickEvent;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Mouse.class})
public class MixinMouse {
   @Inject(
      method = {"onMouseButton"},
      at = {@At("HEAD")}
   )
   private void onMouseButtonHook(long window, int button, int action, int mods, CallbackInfo ci) {
      MouseClickEvent event = new MouseClickEvent(MouseUtil.getMouseButtonByInt(button), MouseUtil.getActionByInt(action));
      PollosHook.getEventBus().dispatch(event);
   }

   @Redirect(
      method = {"updateMouse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
)
   )
   private void updateMouseHook(ClientPlayerEntity instance, double v_, double v) {
      Freecam.EntityTurnHeadEvent event = Freecam.EntityTurnHeadEvent.of(instance, false);
      event.dispatch();
      if (event.isCanceled()) {
         if (event.isLockYaw()) {
            event.getEntity().changeLookDirection(0.0D, v);
            return;
         }

         event.getEntity().changeLookDirection(v_, v);
      } else {
         instance.changeLookDirection(v_, v);
      }

   }
}
