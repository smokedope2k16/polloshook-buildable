package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;

public class RenderEndCrystalEvent extends Event {
   private final EndCrystalEntity endCrystal;
   private final MatrixStack matrixStack;
   private final VertexConsumerProvider vertexConsumerProvider;
   private final float tickDelta;
   private final int light;
   private ModelPart core;
   private ModelPart frame;
   private ModelPart bottom;

   
   public EndCrystalEntity getEndCrystal() {
      return this.endCrystal;
   }

   
   public MatrixStack getMatrixStack() {
      return this.matrixStack;
   }

   
   public VertexConsumerProvider getVertexConsumerProvider() {
      return this.vertexConsumerProvider;
   }

   
   public float getTickDelta() {
      return this.tickDelta;
   }

   
   public int getLight() {
      return this.light;
   }

   
   public ModelPart getCore() {
      return this.core;
   }

   
   public ModelPart getFrame() {
      return this.frame;
   }

   
   public ModelPart getBottom() {
      return this.bottom;
   }

   
   public RenderEndCrystalEvent(EndCrystalEntity endCrystal, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, float tickDelta, int light, ModelPart core, ModelPart frame, ModelPart bottom) {
      this.endCrystal = endCrystal;
      this.matrixStack = matrixStack;
      this.vertexConsumerProvider = vertexConsumerProvider;
      this.tickDelta = tickDelta;
      this.light = light;
      this.core = core;
      this.frame = frame;
      this.bottom = bottom;
   }

   public static class Post extends RenderEndCrystalEvent {
      public Post(EndCrystalEntity endCrystal, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, float tickDelta, int i, ModelPart core, ModelPart frame, ModelPart bottom) {
         super(endCrystal, matrix, vertexConsumerProvider, tickDelta, i, core, frame, bottom);
      }
   }

   public static class Pre extends RenderEndCrystalEvent {
      public Pre(EndCrystalEntity endCrystal, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, float tickDelta, int i, ModelPart core, ModelPart frame, ModelPart bottom) {
         super(endCrystal, matrix, vertexConsumerProvider, tickDelta, i, core, frame, bottom);
      }
   }
}
