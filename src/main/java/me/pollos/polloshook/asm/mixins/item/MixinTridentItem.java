package me.pollos.polloshook.asm.mixins.item;

import me.pollos.polloshook.impl.module.movement.tridentfly.TridentFly;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({TridentItem.class})
public abstract class MixinTridentItem {
   @Shadow
   public abstract int getMaxUseTime(ItemStack var1, LivingEntity var2);

   @Redirect(
      method = {"use"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"
)
   )
   private boolean useHook(PlayerEntity instance) {
      TridentFly.TryUseTridentNoRainEvent event = TridentFly.TryUseTridentNoRainEvent.create();
      event.dispatch();
      return event.isCanceled() ? true : instance.isTouchingWaterOrRain();
   }

   @Inject(
      method = {"use"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/TypedActionResult;consume(Ljava/lang/Object;)Lnet/minecraft/util/TypedActionResult;"
)}
   )
   private void useHook(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
      TridentFly.UseTridentEvent event = TridentFly.UseTridentEvent.create();
      event.dispatch();
   }

   @Redirect(
      method = {"onStoppedUsing"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"
)
   )
   private boolean onStoppedUsingHook(PlayerEntity instance) {
      TridentFly.TryUseTridentNoRainEvent event = TridentFly.TryUseTridentNoRainEvent.create();
      event.dispatch();
      return event.isCanceled() ? true : instance.isTouchingWaterOrRain();
   }

   @Redirect(
      method = {"onStoppedUsing"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/item/TridentItem;getMaxUseTime(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)I"
)
   )
   private int onStoppedUsingHook(TridentItem instance, ItemStack stack, LivingEntity user) {
      TridentFly.MaxTridentTicksEvent event = TridentFly.MaxTridentTicksEvent.create();
      event.dispatch();
      return event.isCanceled() ? Integer.MAX_VALUE : this.getMaxUseTime(stack, user);
   }
}
