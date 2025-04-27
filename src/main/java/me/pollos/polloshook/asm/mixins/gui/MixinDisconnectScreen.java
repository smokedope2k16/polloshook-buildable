package me.pollos.polloshook.asm.mixins.gui;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.misc.autoreconnect.AutoReconnect;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DisconnectedScreen.class})
public abstract class MixinDisconnectScreen extends Screen {
   @Shadow
   @Final
   private DirectionalLayoutWidget grid;

   protected MixinDisconnectScreen(Text title) {
      super(title);
   }

   @Inject(
      method = {"init"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/widget/DirectionalLayoutWidget;refreshPositions()V",
   shift = Shift.BEFORE
)}
   )
   private void initHook(CallbackInfo ci) {
      AutoReconnect.InitDisconnectScreenEvent event = new AutoReconnect.InitDisconnectScreenEvent(Managers.getServerManager().getLastServer());
      event.dispatch();
      event.getWidgetList().forEach((w) -> {
         this.grid.add(w);
      });
   }

   public void tick() {
      super.tick();
   }
}
