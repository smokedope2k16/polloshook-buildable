package me.pollos.polloshook.api.minecraft.render.shader;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.texture.AbstractTexture;

public final class ManagedSamplerUniformV1 extends ManagedSamplerUniformBase {
   public ManagedSamplerUniformV1(String name) {
      super(name);
   }

   public void set(AbstractTexture texture) {
      this.set((Object)texture);
   }

   public void set(Framebuffer textureFbo) {
      this.set((Object)textureFbo);
   }

   public void set(int textureName) {
      this.set((Object)textureName);
   }

   protected void set(Object value) {
      SamplerAccess[] targets = this.targets;
      if (targets.length > 0 && this.cachedValue != value) {
         SamplerAccess[] var3 = targets;
         int var4 = targets.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            SamplerAccess target = var3[var5];
            ((ShaderProgram)target).addSampler(this.name, value);
         }

         this.cachedValue = value;
      }

   }
}
