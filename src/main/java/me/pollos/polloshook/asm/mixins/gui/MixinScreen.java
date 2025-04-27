package me.pollos.polloshook.asm.mixins.gui;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.asm.ducks.gui.IScreen;
import me.pollos.polloshook.impl.module.misc.deathcoordslog.DeathCoordsLog;
import me.pollos.polloshook.impl.module.other.irc.util.RunnableClickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Screen.class})
public class MixinScreen implements IScreen {
   @Shadow
   @Final
   private List<Drawable> drawables;
   @Shadow
   @Final
   private static Logger LOGGER;
   @Shadow
   @Nullable
   protected MinecraftClient client;
   @Unique
   private int mouseX;
   @Unique
   private int mouseY;

   @Inject(
      method = {"close"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hooktuah(CallbackInfo ci) {
      if (this.client == null) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void renderHook(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      this.mouseX = mouseX;
      this.mouseY = mouseY;
   }

   @Redirect(
      method = {"handleTextClick"},
      at = @At(
   value = "INVOKE",
   target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V",
   remap = false
)
   )
   private void handleTextClickHookLog(Logger instance, String string, Object o, @Local ClickEvent clickEvent) {
      if (clickEvent instanceof RunnableClickEvent) {
         RunnableClickEvent runnableClickEvent = (RunnableClickEvent)clickEvent;
         runnableClickEvent.getRunnable().run();
      } else {
         LOGGER.error("Don't know how to handle {}", clickEvent);
      }

   }

   @Inject(
      method = {"handleTextClick"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/Keyboard;setClipboard(Ljava/lang/String;)V"
)}
   )
   private void handleTextClickHook(Style style, CallbackInfoReturnable<Boolean> cir) {
      DeathCoordsLog DEATH_COORDS_LOG_MODULE = (DeathCoordsLog)Managers.getModuleManager().get(DeathCoordsLog.class);
      if (DEATH_COORDS_LOG_MODULE.isEnabled() && style.getColor().getName().equalsIgnoreCase("#FF5555") && style.getClickEvent().getAction() == Action.COPY_TO_CLIPBOARD && style.getHoverEvent().getAction() == net.minecraft.text.HoverEvent.Action.SHOW_TEXT) {
         ClientLogger.getLogger().log((String)"%sCopied Coordinates to clipboard".formatted(new Object[]{Formatting.GREEN}), -1);
      }

   }

   public List<Drawable> drawables() {
      return this.drawables;
   }

   public int getXMouse() {
      return this.mouseX;
   }

   public int getYMouse() {
      return this.mouseY;
   }
}