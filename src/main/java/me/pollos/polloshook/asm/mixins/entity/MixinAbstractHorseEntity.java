package me.pollos.polloshook.asm.mixins.entity;

import me.pollos.polloshook.impl.module.movement.entitycontrol.EntityControl;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractHorseEntity.class})
public abstract class MixinAbstractHorseEntity {
   @Shadow
   protected abstract void jump(float var1, Vec3d var2);

   @Inject(
      method = {"isSaddled"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isSaddledHook(CallbackInfoReturnable<Boolean> cir) {
      EntityControl.SaddleEvent event = new EntityControl.SaddleEvent();
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(true);
      }

   }

   @Redirect(
      method = {"tickControlled"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/passive/AbstractHorseEntity;jump(FLnet/minecraft/util/math/Vec3d;)V"
)
   )
   private void tickControlledHook(AbstractHorseEntity instance, float strength, Vec3d movementInput) {
      EntityControl.HorseJumpStrengthEvent event = new EntityControl.HorseJumpStrengthEvent(strength);
      event.dispatch();
      if (event.isCanceled()) {
         this.jump(event.getStrength(), movementInput);
      } else {
         this.jump(strength, movementInput);
      }
   }
}
