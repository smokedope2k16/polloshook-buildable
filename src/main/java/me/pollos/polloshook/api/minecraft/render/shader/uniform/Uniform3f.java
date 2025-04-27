package me.pollos.polloshook.api.minecraft.render.shader.uniform;

import org.joml.Vector3f;

public interface Uniform3f {
   void set(float var1, float var2, float var3);

   void set(Vector3f var1);
}
