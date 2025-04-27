package me.pollos.polloshook.api.minecraft.render.shader;

import java.util.Objects;
import java.util.function.IntSupplier;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.SamplerUniformV2;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.texture.AbstractTexture;

public final class ManagedSamplerUniformV2 extends ManagedSamplerUniformBase implements SamplerUniformV2 {
   public ManagedSamplerUniformV2(String name) {
      super(name);
   }

   public void set(AbstractTexture texture) {
      Objects.requireNonNull(texture);
      this.set(texture::getGlId);
   }

   public void set(Framebuffer textureFbo) {
      Objects.requireNonNull(textureFbo);
      this.set(textureFbo::getColorAttachment);
   }

   public void set(int textureName) {
      this.set(() -> {
         return textureName;
      });
   }

   protected void set(Object value) {
      this.set((IntSupplier)value);
   }

   public void set(IntSupplier value) {
      SamplerAccess[] targets = this.targets;
      if (targets.length > 0 && this.cachedValue != value) {
         SamplerAccess[] var3 = targets;
         int var4 = targets.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            SamplerAccess target = var3[var5];
            ((JsonEffectShaderProgram)target).bindSampler(this.name, value);
         }

         this.cachedValue = value;
      }

   }
}
