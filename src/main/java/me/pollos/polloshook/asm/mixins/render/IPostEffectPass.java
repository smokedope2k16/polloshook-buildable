package me.pollos.polloshook.asm.mixins.render;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PostEffectPass.class})
public interface IPostEffectPass {
   @Mutable
   @Accessor("input")
   void setInput(Framebuffer var1);

   @Mutable
   @Accessor("output")
   void setOutput(Framebuffer var1);
}
