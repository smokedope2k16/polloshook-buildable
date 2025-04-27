package me.pollos.polloshook.asm.mixins.world;

import java.util.Iterator;
import java.util.function.Supplier;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.asm.ducks.world.IClientWorld;
import me.pollos.polloshook.impl.events.block.BlockBreakingProgressEvent;
import me.pollos.polloshook.impl.events.entity.EntityWorldEvent;
import me.pollos.polloshook.impl.events.world.WorldLoadEvent;
import me.pollos.polloshook.impl.module.render.customsky.CustomSky;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.ClientWorld.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientWorld.class})
public abstract class MixinClientWorld implements IClientWorld {
   @Final
   @Shadow
   private PendingUpdateManager pendingUpdateManager;

   @Shadow
   @Nullable
   public abstract Entity getEntityById(int var1);

   public PendingUpdateManager getPendingUpdateManager() {
      return this.pendingUpdateManager;
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void initHook(ClientPlayNetworkHandler networkHandler, Properties properties, RegistryKey registryRef, RegistryEntry dimensionTypeEntry, int loadDistance, int simulationDistance, Supplier profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
      WorldLoadEvent worldLoadEvent = new WorldLoadEvent();
      PollosHook.getEventBus().dispatch(worldLoadEvent);
      Iterator var14 = Managers.getModuleManager().getAllModules().iterator();

      while(true) {
         ToggleableModule toggleableModule;
         Module module;
         do {
            if (!var14.hasNext()) {
               return;
            }

            module = (Module)var14.next();
            if (!(module instanceof ToggleableModule)) {
               break;
            }

            toggleableModule = (ToggleableModule)module;
         } while(!toggleableModule.isEnabled());

         module.onWorldLoad();
      }
   }

   @Inject(
      method = {"addEntity"},
      at = {@At("TAIL")}
   )
   private void addEntityHook(Entity entity, CallbackInfo info) {
      EntityWorldEvent.Add entityWorldEvent = new EntityWorldEvent.Add(entity);
      PollosHook.getEventBus().dispatch(entityWorldEvent);
   }

   @Inject(
      method = {"removeEntity"},
      at = {@At("HEAD")}
   )
   private void removeEntityHook(int entityId, RemovalReason removalReason, CallbackInfo info) {
      EntityWorldEvent.Remove entityWorldEvent = new EntityWorldEvent.Remove(this.getEntityById(entityId), removalReason);
      PollosHook.getEventBus().dispatch(entityWorldEvent);
   }

   @Inject(
      method = {"setBlockBreakingInfo"},
      at = {@At("HEAD")}
   )
   private void setBlockBreakingInfoHook(int entityId, BlockPos pos, int progress, CallbackInfo ci) {
      BlockBreakingProgressEvent blockBreakingProgressEvent = new BlockBreakingProgressEvent(pos);
      PollosHook.getEventBus().dispatch(blockBreakingProgressEvent);
   }

   @Inject(
      method = {"getSkyColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getSkyColorHook(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
      CustomSky CUSTOM_SKY_MODULE = (CustomSky)Managers.getModuleManager().get(CustomSky.class);
      if (CUSTOM_SKY_MODULE.isSkyMode()) {
         cir.setReturnValue(new Vec3d((double)((float)CUSTOM_SKY_MODULE.getSkyColor().getColor().getRed() / 255.0F), (double)((float)CUSTOM_SKY_MODULE.getSkyColor().getColor().getGreen() / 255.0F), (double)((float)CUSTOM_SKY_MODULE.getSkyColor().getColor().getBlue() / 255.0F)));
      }

   }
}
