package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class RenderEvent extends Event {
   private final float tickDelta;
   private final MatrixStack matrixStack;
   private final Camera camera;
   private final Matrix4f projectionMatrix;
   private final Matrix4f positionMatrix;

   
   public float getTickDelta() {
      return this.tickDelta;
   }

   
   public MatrixStack getMatrixStack() {
      return this.matrixStack;
   }

   
   public Camera getCamera() {
      return this.camera;
   }

   
   public Matrix4f getProjectionMatrix() {
      return this.projectionMatrix;
   }

   
   public Matrix4f getPositionMatrix() {
      return this.positionMatrix;
   }

   
   public RenderEvent(float tickDelta, MatrixStack matrixStack, Camera camera, Matrix4f projectionMatrix, Matrix4f positionMatrix) {
      this.tickDelta = tickDelta;
      this.matrixStack = matrixStack;
      this.camera = camera;
      this.projectionMatrix = projectionMatrix;
      this.positionMatrix = positionMatrix;
   }
}
