package me.pollos.polloshook.api.minecraft.render.shader.uniform;

import org.joml.Matrix4f;

public interface UniformMat4 {
   void set(Matrix4f var1);

   void setFromArray(float[] var1);
}
