package me.pollos.polloshook.impl.module.render.skeleton.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

public class CacheConsumerProvider implements VertexConsumerProvider {
   private final CacheConsumer cacheConsumer = new CacheConsumer();

   public CacheConsumer getCacheConsumer() {
      return this.cacheConsumer;
   }

   public VertexConsumer getBuffer(RenderLayer layer) {
      return this.cacheConsumer;
   }
}
