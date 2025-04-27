package me.pollos.polloshook.impl.module.render.chams.util;

import net.minecraft.entity.Entity;

public record EntityRenderRunnable(Entity entity, Runnable runnable) {
   public EntityRenderRunnable(Entity entity, Runnable runnable) {
      this.entity = entity;
      this.runnable = runnable;
   }

   public Entity entity() {
      return this.entity;
   }

   public Runnable runnable() {
      return this.runnable;
   }
}
