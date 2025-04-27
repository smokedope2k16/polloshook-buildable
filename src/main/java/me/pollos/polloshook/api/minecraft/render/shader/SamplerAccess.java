package me.pollos.polloshook.api.minecraft.render.shader;

import java.util.List;

public interface SamplerAccess {
   boolean hasSampler(String var1);

   List<String> getSamplerNames();

   List<Integer> getSamplerShaderLocs();
}
