package me.pollos.polloshook.api.minecraft.render.shader;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import me.pollos.polloshook.api.minecraft.render.shader.managed.ManagedCoreShader;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.SamplerUniform;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

public final class ResettableManagedCoreShader extends ResettableManagedShaderBase<ShaderProgram> implements ManagedCoreShader {
   private final Consumer<ManagedCoreShader> initCallback;
   private final VertexFormat vertexFormat;
   private final Map<String, ManagedSamplerUniformV1> managedSamplers = new HashMap();

   public ResettableManagedCoreShader(Identifier location, VertexFormat vertexFormat, Consumer<ManagedCoreShader> initCallback) {
      super(location);
      this.vertexFormat = vertexFormat;
      this.initCallback = initCallback;
   }

   protected ShaderProgram parseShader(ResourceFactory resourceManager, MinecraftClient mc, Identifier location) throws IOException {
      return new FabricShaderProgram(resourceManager, this.getLocation(), this.vertexFormat);
   }

   public void setup(int newWidth, int newHeight) {
      Preconditions.checkNotNull((ShaderProgram)this.shader);
      Iterator var3 = this.getManagedUniforms().iterator();

      while(var3.hasNext()) {
         ManagedUniformBase uniform = (ManagedUniformBase)var3.next();
         this.setupUniform(uniform, (ShaderProgram)this.shader);
      }

      this.initCallback.accept(this);
   }

   public ShaderProgram getProgram() {
      return (ShaderProgram)this.shader;
   }

   protected boolean setupUniform(ManagedUniformBase uniform, ShaderProgram shader) {
      return uniform.findUniformTarget(shader);
   }

   public SamplerUniform findSampler(String samplerName) {
      return (SamplerUniform)this.manageUniform(this.managedSamplers, ManagedSamplerUniformV1::new, samplerName, "sampler");
   }

   protected void logInitError(IOException e) {
      LogUtils.getLogger().error("Could not create shader program {}", this.getLocation(), e);
   }
}
