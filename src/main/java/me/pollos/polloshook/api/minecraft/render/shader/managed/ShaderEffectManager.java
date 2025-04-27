package me.pollos.polloshook.api.minecraft.render.shader.managed;

import java.util.function.Consumer;
import me.pollos.polloshook.api.minecraft.render.shader.ReloadableShaderEffectManager;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;

public interface ShaderEffectManager {
   static ShaderEffectManager getInstance() {
      return ReloadableShaderEffectManager.INSTANCE;
   }

   ManagedShaderEffect manage(Identifier var1);

   ManagedShaderEffect manage(Identifier var1, Consumer<ManagedShaderEffect> var2);

   ManagedCoreShader manageCoreShader(Identifier var1);

   ManagedCoreShader manageCoreShader(Identifier var1, VertexFormat var2);

   ManagedCoreShader manageCoreShader(Identifier var1, VertexFormat var2, Consumer<ManagedCoreShader> var3);
}
