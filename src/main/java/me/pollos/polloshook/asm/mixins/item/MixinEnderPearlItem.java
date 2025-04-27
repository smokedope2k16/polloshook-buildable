package me.pollos.polloshook.asm.mixins.item;

import me.pollos.polloshook.impl.events.item.SetPearlCooldownEvent;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({EnderPearlItem.class})
public class MixinEnderPearlItem {
   @Redirect(
      method = {"use"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/ItemCooldownManager;set(Lnet/minecraft/item/Item;I)V"
)
   )
   private void useHook(ItemCooldownManager instance, Item item, int duration) {
      SetPearlCooldownEvent event = new SetPearlCooldownEvent(duration);
      event.dispatch();
      if (event.isCanceled()) {
         instance.set(item, event.getTicks());
      } else {
         instance.set(item, duration);
      }

   }
}
