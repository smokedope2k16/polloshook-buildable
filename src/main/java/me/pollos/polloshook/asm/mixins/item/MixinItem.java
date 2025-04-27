package me.pollos.polloshook.asm.mixins.item;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.impl.events.item.FinishUsingItemEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Item.class})
public class MixinItem {
   @Inject(
      method = {"finishUsing"},
      at = {@At("HEAD")}
   )
   private void finishUsingHook(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
      FinishUsingItemEvent finishUsingItemEvent = new FinishUsingItemEvent(stack, user);
      PollosHook.getEventBus().dispatch(finishUsingItemEvent);
   }
}
