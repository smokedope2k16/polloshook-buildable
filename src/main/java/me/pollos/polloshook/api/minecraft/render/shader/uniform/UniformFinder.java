package me.pollos.polloshook.api.minecraft.render.shader.uniform;

public interface UniformFinder {
   Uniform1i findUniform1i(String var1);

   Uniform2i findUniform2i(String var1);

   Uniform3i findUniform3i(String var1);

   Uniform4i findUniform4i(String var1);

   Uniform1f findUniform1f(String var1);

   Uniform2f findUniform2f(String var1);

   Uniform3f findUniform3f(String var1);

   Uniform4f findUniform4f(String var1);

   UniformMat4 findUniformMat4(String var1);

   SamplerUniform findSampler(String var1);
}
