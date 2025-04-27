package me.pollos.polloshook.asm.ducks.world;

import net.minecraft.client.render.Camera;
import org.joml.Matrix4f;

public interface IGameRenderer {
   void getRenderHand(Camera var1, float var2, Matrix4f var3);

   void renderHandFast(Camera var1, float var2, Matrix4f var3);

   boolean isRenderingHand();
}
