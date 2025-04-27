package me.pollos.polloshook.asm.mixins.gui;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import me.pollos.polloshook.impl.module.render.chams.Chams;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InventoryScreen.class})
public abstract class MixinInventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> {
   public MixinInventoryScreen(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
      super(screenHandler, playerInventory, text);
   }

   @Inject(
      method = {"drawBackground"},
      at = {@At("HEAD")}
   )
   protected void drawBackgroundHookPre(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo info) {
      ((Chams)Managers.getModuleManager().get(Chams.class)).setStop(true);
   }

   @Inject(
      method = {"drawBackground"},
      at = {@At("RETURN")}
   )
   protected void drawBackgroundHookPost(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo info) {
      ((Chams)Managers.getModuleManager().get(Chams.class)).setStop(false);
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void renderHook(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if ((Boolean)Manager.get().getKeepInvCentered().getValue()) {
         this.x = (this.width - this.backgroundWidth) / 2;
         this.y = (this.height - this.backgroundHeight) / 2;
      }

   }

   @Inject(
      method = {"drawEntity"},
      at = {@At("HEAD")}
   )
   private static void drawEntityHead(DrawContext context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity, CallbackInfo info) {
      Managers.getRotationManager().setInv(true);
   }

   @Inject(
      method = {"drawEntity"},
      at = {@At("TAIL")}
   )
   private static void drawEntityPost(DrawContext context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity, CallbackInfo info) {
      Managers.getRotationManager().setInv(false);
   }
}
