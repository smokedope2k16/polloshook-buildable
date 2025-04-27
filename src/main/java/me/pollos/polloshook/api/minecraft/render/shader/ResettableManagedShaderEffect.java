package me.pollos.polloshook.api.minecraft.render.shader;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import me.pollos.polloshook.api.minecraft.render.shader.managed.ManagedFramebuffer;
import me.pollos.polloshook.api.minecraft.render.shader.managed.ManagedShaderEffect;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.SamplerUniformV2;
import me.pollos.polloshook.asm.ducks.shader.IShaderEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

public final class ResettableManagedShaderEffect extends ResettableManagedShaderBase<PostEffectProcessor> implements ManagedShaderEffect {
   private final Consumer<ManagedShaderEffect> initCallback;
   private final Map<String, FramebufferWrapper> managedTargets;
   private final Map<String, ManagedSamplerUniformV2> managedSamplers = new HashMap();

   public ResettableManagedShaderEffect(Identifier location, Consumer<ManagedShaderEffect> initCallback) {
      super(location);
      this.initCallback = initCallback;
      this.managedTargets = new HashMap();
   }

   public PostEffectProcessor getShaderEffect() {
      return this.getShaderOrLog();
   }

   protected PostEffectProcessor parseShader(ResourceFactory resourceFactory, MinecraftClient mc, Identifier location) throws IOException {
      return new PostEffectProcessor(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), location);
   }

   public void setup(int windowWidth, int windowHeight) {
      Preconditions.checkNotNull((PostEffectProcessor)this.shader);
      ((PostEffectProcessor)this.shader).setupDimensions(windowWidth, windowHeight);
      Iterator var3 = this.getManagedUniforms().iterator();

      while(var3.hasNext()) {
         ManagedUniformBase uniform = (ManagedUniformBase)var3.next();
         this.setupUniform(uniform, (PostEffectProcessor)this.shader);
      }

      var3 = this.managedTargets.values().iterator();

      while(var3.hasNext()) {
         FramebufferWrapper buf = (FramebufferWrapper)var3.next();
         buf.findTarget((PostEffectProcessor)this.shader);
      }

      this.initCallback.accept(this);
   }

   public void render(float tickDelta) {
      PostEffectProcessor sg = this.getShaderEffect();
      if (sg != null) {
         RenderSystem.disableBlend();
         RenderSystem.disableDepthTest();
         RenderSystem.resetTextureMatrix();
         sg.render(tickDelta);
         MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
         RenderSystem.disableBlend();
         RenderSystem.blendFunc(770, 771);
         RenderSystem.enableDepthTest();
      }

   }

   public ManagedFramebuffer getTarget(String name) {
      return (ManagedFramebuffer)this.managedTargets.computeIfAbsent(name, (n) -> {
         FramebufferWrapper ret = new FramebufferWrapper(n);
         if (this.shader != null) {
            ret.findTarget((PostEffectProcessor)this.shader);
         }

         return ret;
      });
   }

   public void setUniformValue(String uniformName, int value) {
      this.findUniform1i(uniformName).set(value);
   }

   public void setUniformValue(String uniformName, float value) {
      this.findUniform1f(uniformName).set(value);
   }

   public void setUniformValue(String uniformName, float value0, float value1) {
      this.findUniform2f(uniformName).set(value0, value1);
   }

   public void setUniformValue(String uniformName, float value0, float value1, float value2) {
      this.findUniform3f(uniformName).set(value0, value1, value2);
   }

   public void setUniformValue(String uniformName, float value0, float value1, float value2, float value3) {
      this.findUniform4f(uniformName).set(value0, value1, value2, value3);
   }

   public SamplerUniformV2 findSampler(String samplerName) {
      return (SamplerUniformV2)this.manageUniform(this.managedSamplers, ManagedSamplerUniformV2::new, samplerName, "sampler");
   }

   protected boolean setupUniform(ManagedUniformBase uniform, PostEffectProcessor shader) {
      return uniform.findUniformTargets(((IShaderEffect)shader).getPasses());
   }

   protected void logInitError(IOException e) {
      LogUtils.getLogger().error("Could not create screen shader {}", this.getLocation(), e);
   }

   private PostEffectProcessor getShaderOrLog() {
      if (!this.isInitialized() && !this.isErrored()) {
         this.initializeOrLog(MinecraftClient.getInstance().getResourceManager());
      }

      return (PostEffectProcessor)this.shader;
   }
}
