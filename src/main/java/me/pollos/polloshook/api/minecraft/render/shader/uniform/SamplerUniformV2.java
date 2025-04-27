package me.pollos.polloshook.api.minecraft.render.shader.uniform;

import java.util.function.IntSupplier;

public interface SamplerUniformV2 extends SamplerUniform {
   void set(IntSupplier var1);
}
