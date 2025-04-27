package me.pollos.polloshook.asm.mixins.entity;

import me.pollos.polloshook.impl.module.movement.boatfly.BoatFly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BoatEntity.class})
public class MixinBoatEntity {
   @Inject(
      method = {"getGravity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getGravityHook(CallbackInfoReturnable<Double> cir) {
      BoatFly.GetGravityEvent event = new BoatFly.GetGravityEvent((Entity)BoatEntity.class.cast(this));
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(0.0D);
      }

   }
}
