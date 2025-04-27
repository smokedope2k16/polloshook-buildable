package me.pollos.polloshook.asm.mixins;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.system.SystemCheck;
import me.pollos.polloshook.api.util.system.SystemStatus;
import me.pollos.polloshook.api.util.system.WindowUtil;
import me.pollos.polloshook.asm.ducks.IMinecraftClient;
import me.pollos.polloshook.impl.events.gui.ScreenEvent;
import me.pollos.polloshook.impl.events.misc.GameLoopEvent;
import me.pollos.polloshook.impl.events.misc.LimitFPSEvent;
import me.pollos.polloshook.impl.events.misc.ResizeWindowEvent;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;
import me.pollos.polloshook.impl.events.render.EntityOutlineEvent;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.events.world.PickBlockEvent;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import me.pollos.polloshook.impl.module.player.mutlitask.MultiTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.session.Session;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {MinecraftClient.class},
   priority = 2147483547
)
public abstract class MixinMinecraftClient implements IMinecraftClient {
   @Shadow
   @Nullable
   public ClientWorld world;
   @Shadow
   @Nullable
   public ClientPlayerInteractionManager interactionManager;
   @Shadow
   @Final
   private Window window;
   @Shadow
   @Nullable
   public Screen currentScreen;
   @Shadow
   @Nullable
   private Overlay overlay;
   @Shadow
   public int attackCooldown;

   @Accessor("itemUseCooldown")
   public abstract void setItemUseCooldown(int var1);

   @Accessor("itemUseCooldown")
   public abstract int getItemUseCooldown();

   @Shadow
   protected abstract String getWindowTitle();

   @Shadow
   public abstract void updateWindowTitle();

   @Shadow
   protected abstract void doItemUse();

   @Mutable
   @Accessor("session")
   public abstract void setSession(Session var1);

   @Accessor("disconnecting")
   public abstract boolean isDisconnecting();

   @Inject(
      method = {"<init>"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/MinecraftClient;instance:Lnet/minecraft/client/MinecraftClient;",
   shift = Shift.AFTER
)}
   )
   private void initHook(CallbackInfo info) {
      PollosHook.setSystemStatus(SystemCheck.checkSystem());
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void initHook_Return(CallbackInfo info) {
      PollosHook.init();
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void initHook_Tail(CallbackInfo ci) {
      if (PollosHook.getSystemStatus() == SystemStatus.SUITABLE) {
         WindowUtil.applyChanges(MinecraftClient.getInstance().getWindow(), 0, 0, -1, 1000);
      } else {
         ClientLogger.getLogger().warn("Your OS is not suitable for custom title bar");
      }

   }

   @Inject(
      method = {"onResolutionChanged"},
      at = {@At("TAIL")}
   )
   private void onResolutionChangedHook(CallbackInfo ci) {
      ResizeWindowEvent event = new ResizeWindowEvent(this.window);
      event.dispatch();
   }

   @Inject(
      method = {"close"},
      at = {@At("RETURN")}
   )
   private void closeHook_Return(CallbackInfo info) {
      PollosHook.shutdown();
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void preTickHook(CallbackInfo info) {
      PollosHook.getEventBus().dispatch(new TickEvent());
   }

   @Inject(
      method = {"tick"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/world/ClientWorld;tick(Ljava/util/function/BooleanSupplier;)V",
   shift = Shift.AFTER
)}
   )
   private void postWorldTickHook(CallbackInfo info) {
      TickEvent.PostWorldTick postWorld = new TickEvent.PostWorldTick();
      PollosHook.getEventBus().dispatch(postWorld);
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void tickHook(CallbackInfo info) {
      TickEvent post = new TickEvent.Post();
      PollosHook.getEventBus().dispatch(post);
   }

   @Inject(
      method = {"setScreen"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void setScreenHook(Screen screen, CallbackInfo info) {
      ScreenEvent event = new ScreenEvent(screen);
      PollosHook.getEventBus().dispatch(event);
      if (event.isCanceled()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"doItemPick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void doItemPickHook(CallbackInfo ci) {
      PickBlockEvent pickBlockEvent = new PickBlockEvent();
      PollosHook.getEventBus().dispatch(pickBlockEvent);
      if (pickBlockEvent.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void renderHook(boolean bl, CallbackInfo info) {
      GameLoopEvent event = new GameLoopEvent();
      PollosHook.getEventBus().dispatch(event);
   }

   @Inject(
      method = {"disconnect(Lnet/minecraft/client/gui/screen/Screen;)V"},
      at = {@At("HEAD")}
   )
   private void disconnectHook(Screen disconnectionScreen, CallbackInfo ci) {
      if (this.world != null) {
         LeaveGameEvent leaveGameEvent = new LeaveGameEvent();
         PollosHook.getEventBus().dispatch(leaveGameEvent);
      }

   }

   @Inject(
      method = {"hasOutline"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hasOutlineHook(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      EntityOutlineEvent entityOutlineEvent = new EntityOutlineEvent(entity);
      PollosHook.getEventBus().dispatch(entityOutlineEvent);
      if (entityOutlineEvent.isCanceled()) {
         cir.setReturnValue(true);
      }

   }

   @Inject(
      method = {"getFramerateLimit"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getFramerateLimitHook(CallbackInfoReturnable<Integer> cir) {
      LimitFPSEvent limitFPSEvent = new LimitFPSEvent();
      PollosHook.getEventBus().dispatch(limitFPSEvent);
      if (limitFPSEvent.isCanceled()) {
         cir.setReturnValue(limitFPSEvent.getFps());
      }

   }

   @Redirect(
      method = {"handleBlockBreaking"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
)
   )
   private boolean handleBlockBreakingHook(ClientPlayerEntity instance) {
      MultiTask.MultiTaskEvent event = new MultiTask.MultiTaskEvent();
      event.dispatch();
      return !event.isCanceled() && instance.isUsingItem();
   }

   @Redirect(
      method = {"doItemUse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"
)
   )
   private boolean doItemUseHook(ClientPlayerInteractionManager instance) {
      MultiTask.MultiTaskEvent event = new MultiTask.MultiTaskEvent();
      event.dispatch();
      return !event.isCanceled() && instance.isBreakingBlock();
   }

   @Inject(
      method = {"printCrashReport(Lnet/minecraft/client/MinecraftClient;Ljava/io/File;Lnet/minecraft/util/crash/CrashReport;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/Bootstrap;println(Ljava/lang/String;)V"
)}
   )
   private static void hook(MinecraftClient client, File runDirectory, CrashReport crashReport, CallbackInfo ci) {
      StringBuilder crashReportBuilder = new StringBuilder();
      if (Managers.getModuleManager() != null) {
         crashReportBuilder.append("---- START POLLOSHOOK INFO");
         crashReportBuilder.append("polloshook | v2.8.5 | 421997cc5c6d96ff46cd700602c8565d0de2ca04\n");
         List<Module> toggledModules = new ArrayList();
         Iterator var6 = Managers.getModuleManager().getAllModules().iterator();

         Module module;
         while(var6.hasNext()) {
            boolean var10000;
            label32: {
               module = (Module)var6.next();
               if (module instanceof ToggleableModule) {
                  ToggleableModule tog = (ToggleableModule)module;
                  if (!tog.isEnabled()) {
                     var10000 = false;
                     break label32;
                  }
               }

               var10000 = true;
            }

            boolean toggled = var10000;
            if (toggled) {
               toggledModules.add(module);
            }
         }

         var6 = toggledModules.iterator();

         while(var6.hasNext()) {
            module = (Module)var6.next();
            crashReportBuilder.append("Active Modules - ").append(String.join(", ", module.getLabel()));
         }

         crashReportBuilder.append("---- END POLLOSHOOK INFO");
         crashReport.addDetails(crashReportBuilder);
      }
   }

   @Redirect(
      method = {"updateWindowTitle"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/Window;setTitle(Ljava/lang/String;)V"
)
   )
   private void updateWindowTitleHook(Window instance, String title) {
      if ((Boolean)Manager.get().getCustomDisplay().getValue()) {
         instance.setTitle(Manager.get().getDisplayText());
      } else {
         if (!(Boolean)Manager.get().getFpsInWindow().getValue() && !(Boolean)Manager.get().getMemoryInWindow().getValue()) {
            instance.setTitle(title);
         } else {
            instance.setTitle(title + Manager.get().getWindowAppendText());
         }

      }
   }

   public void setAttackTicks(int attackTicks) {
      this.attackCooldown = attackTicks;
   }

   public void $doItemUse() {
      this.doItemUse();
   }

   public boolean is60FPSLimit() {
      return this.world == null && (this.currentScreen != null || this.overlay != null);
   }

   public void $updateWindowTitle() {
      this.updateWindowTitle();
   }

   public String $getWindowTitle() {
      return this.getWindowTitle();
   }
}
