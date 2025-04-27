package me.pollos.polloshook.asm.ducks.shader;

import java.util.List;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;

public interface IShaderEffect {
   void addHook(String var1, Framebuffer var2);

   List<PostEffectPass> getPasses();
}
