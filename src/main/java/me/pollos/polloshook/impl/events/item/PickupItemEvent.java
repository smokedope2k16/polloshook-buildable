package me.pollos.polloshook.impl.events.item;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PickupItemEvent extends Event {
   private final PlayerEntity player;
   private final ItemStack item;

   
   public PickupItemEvent(PlayerEntity player, ItemStack item) {
      this.player = player;
      this.item = item;
   }

   
   public PlayerEntity getPlayer() {
      return this.player;
   }

   
   public ItemStack getItem() {
      return this.item;
   }
}
