package me.pollos.polloshook.asm.mixins.gui;

import java.util.Comparator;
import java.util.List;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.impl.events.misc.SortTabEvent;
import me.pollos.polloshook.impl.events.render.tablist.TabLimitEvent;
import me.pollos.polloshook.impl.events.render.tablist.TabNameEvent;
import me.pollos.polloshook.impl.module.render.extratab.ExtraTab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerListHud.class})
public class MixinPlayerListHud {
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   @Final
   private static Comparator<PlayerListEntry> ENTRY_ORDERING;
   @Shadow
   @Nullable
   private Text footer;
   @Shadow
   @Nullable
   private Text header;

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void renderHook(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
      ExtraTab.RenderTabElementEvent event = ExtraTab.RenderTabElementEvent.create(ExtraTab.RenderTabElementEvent.Element.FOOTER);
      event.dispatch();
      if (event.isCanceled()) {
         this.footer = null;
      }

      ExtraTab.RenderTabElementEvent eventHeader = ExtraTab.RenderTabElementEvent.create(ExtraTab.RenderTabElementEvent.Element.HEADER);
      eventHeader.dispatch();
      if (eventHeader.isCanceled()) {
         this.header = null;
      }

   }

   @Redirect(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
)
   )
   private void renderHook(DrawContext instance, int x1, int y1, int x2, int y2, int color) {
      ExtraTab.RenderTabElementEvent event = ExtraTab.RenderTabElementEvent.create(ExtraTab.RenderTabElementEvent.Element.BACKGROUND);
      event.dispatch();
      if (!event.isCanceled()) {
         instance.fill(x1, y1, x2, y2, color);
      }

   }

   @Inject(
      method = {"getPlayerName"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getPlayerNameHook(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
      TabNameEvent tabNameEvent = new TabNameEvent(entry.getProfile().getName());
      PollosHook.getEventBus().dispatch(tabNameEvent);
      if (tabNameEvent.isCanceled()) {
         cir.setReturnValue(tabNameEvent.getText());
      }

   }

   @Inject(
      method = {"collectPlayerEntries"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void collectPLayersEntriesHook(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
      SortTabEvent event = new SortTabEvent(ENTRY_ORDERING);
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(this.client.player.networkHandler.getListedPlayerListEntries().stream().sorted(event.getComparator()).limit(80L).toList());
      }

   }

   @ModifyConstant(
      method = {"collectPlayerEntries"},
      constant = {@Constant(
   longValue = 80L
)}
   )
   private long collcetPlayerEntriesHook(long constant) {
      TabLimitEvent tabLimitEvent = new TabLimitEvent();
      PollosHook.getEventBus().dispatch(tabLimitEvent);
      return tabLimitEvent.isCanceled() ? 10000L : constant;
   }
}
