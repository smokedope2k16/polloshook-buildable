package me.pollos.polloshook.asm.mixins.item;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.impl.events.block.PlaceBlockEvent;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockItem.class})
public class MixinBlockItem {
   @Inject(
      method = {"place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z"},
      at = {@At("HEAD")}
   )
   private void placeHook(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
      if (context.getPlayer() == MinecraftClient.getInstance().player) {
         PlaceBlockEvent blockEvent = new PlaceBlockEvent(state.getBlock(), context.getBlockPos());
         PollosHook.getEventBus().dispatch(blockEvent);
      }

   }
}
