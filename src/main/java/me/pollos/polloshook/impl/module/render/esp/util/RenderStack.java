package me.pollos.polloshook.impl.module.render.esp.util;


import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class RenderStack {
   private final ItemStack stack;
   private final Entity entity;
   private int count;

   public void increase(int amount) {
      this.count += amount;
   }

   
   public ItemStack getStack() {
      return this.stack;
   }

   
   public Entity getEntity() {
      return this.entity;
   }

   
   public int getCount() {
      return this.count;
   }

   
   public RenderStack(ItemStack stack, Entity entity, int count) {
      this.stack = stack;
      this.entity = entity;
      this.count = count;
   }
}
