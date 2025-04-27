package me.pollos.polloshook.asm.mixins.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.pollos.polloshook.asm.ducks.shader.IShaderEffect;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PostEffectProcessor.class})
public abstract class MixinPostEffectProcessor implements IShaderEffect {
   @Unique
   private final List<String> fakedBufferNames = new ArrayList();
   @Final
   @Shadow
   private Map<String, Framebuffer> targetsByName;
   @Final
   @Shadow
   private List<PostEffectPass> passes;

   @Accessor
   public abstract List<PostEffectPass> getPasses();

   public void addHook(String name, Framebuffer buffer) {
      Framebuffer previousFramebuffer = (Framebuffer)this.targetsByName.get(name);
      if (previousFramebuffer != buffer) {
         if (previousFramebuffer != null) {
            Iterator var4 = this.passes.iterator();

            while(var4.hasNext()) {
               PostEffectPass pass = (PostEffectPass)var4.next();
               if (pass.input == previousFramebuffer) {
                  ((IPostEffectPass)pass).setInput(buffer);
               }

               if (pass.output == previousFramebuffer) {
                  ((IPostEffectPass)pass).setOutput(buffer);
               }
            }

            this.targetsByName.remove(name);
            this.fakedBufferNames.remove(name);
         }

         this.targetsByName.put(name, buffer);
         this.fakedBufferNames.add(name);
      }
   }

   @Inject(
      method = {"close"},
      at = {@At("HEAD")}
   )
   void deleteFakeBuffersHook(CallbackInfo ci) {
      Iterator var2 = this.fakedBufferNames.iterator();

      while(var2.hasNext()) {
         String fakedBufferName = (String)var2.next();
         this.targetsByName.remove(fakedBufferName);
      }

   }
}
