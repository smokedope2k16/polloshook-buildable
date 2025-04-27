package me.pollos.polloshook.asm.mixins.gui;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.arraylist.Arraylist;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.arraylist.mode.EffectHUDMode;
import me.pollos.polloshook.impl.module.render.betterchat.BetterChat;
import me.pollos.polloshook.impl.module.render.crosshair.Crosshair;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({InGameHud.class})
public abstract class MixinInGameHud {
   @ModifyArgs(
      method = {"renderMiscOverlays"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V",
   ordinal = 0
)
   )
   private void renderHook(Args args) {
      NoRender.PumpkinOverlayEvent event = NoRender.PumpkinOverlayEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         args.set(2, 0.0F);
      }

   }

   @Inject(
      method = {"renderCrosshair"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderCrosshairHook(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      Crosshair.RenderCrosshairEvent event = Crosshair.RenderCrosshairEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"renderHeldItemTooltip"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderHeldItemTooltipHook(DrawContext context, CallbackInfo ci) {
      NoRender.HeldItemTooltipEvent event = NoRender.HeldItemTooltipEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"renderPortalOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderPortalOverlayHook(DrawContext context, float nauseaStrength, CallbackInfo ci) {
      NoRender.PortalOverlayEvent event = NoRender.PortalOverlayEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"renderStatusEffectOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderStatusEffectOverlayHook(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      Arraylist ARRAY_LIST_HUD = (Arraylist)Managers.getModuleManager().getHUD(Arraylist.class);
      NoRender.EffectTooltipEvent event = NoRender.EffectTooltipEvent.create();
      event.dispatch();
      if (ARRAY_LIST_HUD.isEnabled() && ARRAY_LIST_HUD.getEffectHUD().getValue() == EffectHUDMode.HIDE || event.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"clear"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/hud/ChatHud;clear(Z)V"
)},
      cancellable = true
   )
   private void clearHook(CallbackInfo ci) {
      if ((Boolean)((BetterChat)Managers.getModuleManager().get(BetterChat.class)).getAntiScroll().getValue() && ((BetterChat)Managers.getModuleManager().get(BetterChat.class)).isEnabled()) {
         ci.cancel();
      }

   }
}
