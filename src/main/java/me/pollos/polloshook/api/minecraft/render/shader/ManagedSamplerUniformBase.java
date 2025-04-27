package me.pollos.polloshook.api.minecraft.render.shader;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.SamplerUniform;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.ShaderProgram;

public abstract class ManagedSamplerUniformBase extends ManagedUniformBase implements SamplerUniform {
   protected SamplerAccess[] targets = new SamplerAccess[0];
   protected int[] locations = new int[0];
   protected Object cachedValue;

   public ManagedSamplerUniformBase(String name) {
      super(name);
   }

   public boolean findUniformTargets(List<PostEffectPass> shaders) {
      List<SamplerAccess> targets = new ArrayList(shaders.size());
      IntList rawTargets = new IntArrayList(shaders.size());
      Iterator var4 = shaders.iterator();

      while(var4.hasNext()) {
         PostEffectPass shader = (PostEffectPass)var4.next();
         JsonEffectShaderProgram program = shader.getProgram();
         SamplerAccess access = (SamplerAccess)program;
         if (access.hasSampler(this.name)) {
            targets.add(access);
            rawTargets.add(this.getSamplerLoc(access));
         }
      }

      this.targets = (SamplerAccess[])targets.toArray(new SamplerAccess[0]);
      this.locations = rawTargets.toArray(new int[0]);
      this.syncCurrentValues();
      return this.targets.length > 0;
   }

   private int getSamplerLoc(SamplerAccess access) {
      return (Integer)access.getSamplerShaderLocs().get(access.getSamplerNames().indexOf(this.name));
   }

   public boolean findUniformTarget(ShaderProgram shader) {
      LogUtils.getLogger().warn(shader.getName());
      return this.findUniformTarget1((SamplerAccess)shader);
   }

   private boolean findUniformTarget1(SamplerAccess access) {
      if (access.hasSampler(this.name)) {
         this.targets = new SamplerAccess[]{access};
         this.locations = new int[]{this.getSamplerLoc(access)};
         this.syncCurrentValues();
         return true;
      } else {
         return false;
      }
   }

   private void syncCurrentValues() {
      Object value = this.cachedValue;
      if (value != null) {
         this.cachedValue = null;
         this.set(value);
      }

   }

   protected abstract void set(Object var1);
}
