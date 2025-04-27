package me.pollos.polloshook.api.minecraft.render.shader.managed;

import me.pollos.polloshook.api.minecraft.render.shader.uniform.UniformFinder;
import net.minecraft.client.gl.ShaderProgram;

public interface ManagedCoreShader extends UniformFinder {
   ShaderProgram getProgram();

   void release();
}
