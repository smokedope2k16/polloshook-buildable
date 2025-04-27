package me.pollos.polloshook.impl.events.item;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class FinishUsingItemEvent extends Event {
   private final ItemStack stack;
   private final LivingEntity entity;

   
   public ItemStack getStack() {
      return this.stack;
   }

   
   public LivingEntity getEntity() {
      return this.entity;
   }

   
   public FinishUsingItemEvent(ItemStack stack, LivingEntity entity) {
      this.stack = stack;
      this.entity = entity;
   }
}
