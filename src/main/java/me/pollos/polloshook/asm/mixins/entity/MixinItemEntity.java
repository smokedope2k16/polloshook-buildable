package me.pollos.polloshook.asm.mixins.entity;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.item.PickupItemEvent;
import me.pollos.polloshook.impl.module.movement.icespeed.IceSpeed;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemEntity.class})
public abstract class MixinItemEntity extends Entity {
   public MixinItemEntity(EntityType<?> type, World world) {
      super(type, world);
   }

   @Shadow
   public abstract ItemStack getStack();

   @Inject(
      method = {"onPlayerCollision"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"
)},
      cancellable = true
   )
   private void onPlayerCollisionHook(PlayerEntity player, CallbackInfo ci) {
      ItemStack stack = this.getStack();
      PickupItemEvent pickupItemEvent = new PickupItemEvent(player, stack);
      PollosHook.getEventBus().dispatch(pickupItemEvent);
      if (pickupItemEvent.isCanceled()) {
         ci.cancel();
      }

   }

   @Redirect(
      method = {"tick"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/Block;getSlipperiness()F"
)
   )
   private float tickHook(Block instance) {
      IceSpeed ICE_SPEED = (IceSpeed)Managers.getModuleManager().get(IceSpeed.class);
      if (IceSpeed.ICE_BLOCKS.contains(instance) && ICE_SPEED.isEnabled()) {
         float factor = instance.equals(Blocks.BLUE_ICE) ? 0.009F : 0.0F;
         return 0.98F + factor;
      } else {
         return instance.getSlipperiness();
      }
   }
}