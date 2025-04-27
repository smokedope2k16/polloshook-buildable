package me.pollos.polloshook.api.minecraft.render.shader.uniform;

import org.joml.Vector4f;

public interface Uniform4f {
   void set(float var1, float var2, float var3, float var4);

   void set(Vector4f var1);
}
