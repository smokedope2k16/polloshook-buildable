package me.pollos.polloshook.asm.mixins.entity;

import me.pollos.polloshook.asm.ducks.entity.IFireworkRocketEntity;
import me.pollos.polloshook.impl.module.player.rocketextend.FireworkExtend;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FireworkRocketEntity.class})
public abstract class MixinFireworkEntity extends Entity implements IFireworkRocketEntity {
   @Shadow
   private int lifeTime;
   @Unique
   FireworkExtend.TickFireworkEvent event;

   @Shadow
   protected abstract void explodeAndRemove();

   public MixinFireworkEntity(EntityType<?> type, World world) {
      super(type, world);
   }

   @Inject(
      method = {"tick"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/projectile/FireworkRocketEntity;updateRotation()V",
   shift = Shift.AFTER
)},
      cancellable = true
   )
   private void tickHook_updateRotation(CallbackInfo ci) {
      FireworkRocketEntity instance = (FireworkRocketEntity) (Object) this;
      this.event = FireworkExtend.TickFireworkEvent.of(instance, this.lifeTime);
      this.event.dispatch();
   }

   @Inject(
      method = {"tick"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/projectile/FireworkRocketEntity;explodeAndRemove()V"
)},
      cancellable = true
   )
   private void tickHook_explodeAndRemove(CallbackInfo ci) {
      if (this.event != null && this.event.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"explodeAndRemove"},
      at = {@At("RETURN")}
   )
   private void explodeAndRemoveHook(CallbackInfo ci) {
      FireworkExtend.ExplodeAndRemoveEvent event = FireworkExtend.ExplodeAndRemoveEvent.create();
      event.dispatch();
   }

   public void remove() {
      this.explodeAndRemove();
   }
}
