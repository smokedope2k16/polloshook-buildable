package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class RenderLivingEntityEvent extends Event {
   private final LivingEntity livingEntity;
   private final MatrixStack matrix;
   private final VertexConsumerProvider vertexProvider;
   private final int light;
   private final Identifier texture;
   private final EntityModel<?> model;

   
   public LivingEntity getLivingEntity() {
      return this.livingEntity;
   }

   
   public MatrixStack getMatrix() {
      return this.matrix;
   }

   
   public VertexConsumerProvider getVertexProvider() {
      return this.vertexProvider;
   }

   
   public int getLight() {
      return this.light;
   }

   
   public Identifier getTexture() {
      return this.texture;
   }

   
   public EntityModel<?> getModel() {
      return this.model;
   }

   
   public RenderLivingEntityEvent(LivingEntity livingEntity, MatrixStack matrix, VertexConsumerProvider vertexProvider, int light, Identifier texture, EntityModel<?> model) {
      this.livingEntity = livingEntity;
      this.matrix = matrix;
      this.vertexProvider = vertexProvider;
      this.light = light;
      this.texture = texture;
      this.model = model;
   }

   public static class Post extends RenderLivingEntityEvent {
      public Post(LivingEntity livingEntity, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int i, Identifier texture, EntityModel<?> model) {
         super(livingEntity, matrix, vertexConsumerProvider, i, texture, model);
      }
   }

   public static class Pre extends RenderLivingEntityEvent {
      public Pre(LivingEntity livingEntity, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int i, Identifier texture, EntityModel<?> model) {
         super(livingEntity, matrix, vertexConsumerProvider, i, texture, model);
      }
   }
}
