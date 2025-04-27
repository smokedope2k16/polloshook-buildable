package me.pollos.polloshook.api.minecraft.render.shader;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.render.shader.managed.ManagedCoreShader;
import me.pollos.polloshook.api.minecraft.render.shader.managed.ManagedShaderEffect;
import me.pollos.polloshook.api.minecraft.render.shader.managed.ShaderEffectManager;
import me.pollos.polloshook.impl.events.misc.ResizeWindowEvent;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;

public final class ReloadableShaderEffectManager implements ShaderEffectManager {
   public static final ReloadableShaderEffectManager INSTANCE = new ReloadableShaderEffectManager();
   private final Set<ResettableManagedShaderBase<?>> managedShaders = new ReferenceOpenHashSet();

   public ReloadableShaderEffectManager() {
      PollosHook.getEventBus().register(new Listener<ResizeWindowEvent>(ResizeWindowEvent.class) {
         public void call(ResizeWindowEvent event) {
            Window window = event.getWindow();
            ReloadableShaderEffectManager.this.onResolutionChanged(window.getFramebufferWidth(), window.getFramebufferHeight());
         }
      });
   }

   public ManagedShaderEffect manage(Identifier location) {
      return this.manage(location, (s) -> {
      });
   }

   public ManagedShaderEffect manage(Identifier location, Consumer<ManagedShaderEffect> initCallback) {
      ResettableManagedShaderEffect ret = new ResettableManagedShaderEffect(location, initCallback);
      this.managedShaders.add(ret);
      return ret;
   }

   public ManagedCoreShader manageCoreShader(Identifier location) {
      return this.manageCoreShader(location, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
   }

   public ManagedCoreShader manageCoreShader(Identifier location, VertexFormat vertexFormat) {
      return this.manageCoreShader(location, vertexFormat, (s) -> {
      });
   }

   public ManagedCoreShader manageCoreShader(Identifier location, VertexFormat vertexFormat, Consumer<ManagedCoreShader> initCallback) {
      ResettableManagedCoreShader ret = new ResettableManagedCoreShader(location, vertexFormat, initCallback);
      this.managedShaders.add(ret);
      return ret;
   }

   public void reload(ResourceFactory shaderResources) {
      Iterator var2 = this.managedShaders.iterator();

      while(var2.hasNext()) {
         ResettableManagedShaderBase<?> ss = (ResettableManagedShaderBase)var2.next();
         ss.initializeOrLog(shaderResources);
      }

   }

   public void onResolutionChanged(int newWidth, int newHeight) {
      this.runShaderSetup(newWidth, newHeight);
   }

   private void runShaderSetup(int newWidth, int newHeight) {
      if (!this.managedShaders.isEmpty()) {
         Iterator var3 = this.managedShaders.iterator();

         while(var3.hasNext()) {
            ResettableManagedShaderBase<?> ss = (ResettableManagedShaderBase)var3.next();
            if (ss.isInitialized()) {
               ss.setup(newWidth, newHeight);
            }
         }
      }

   }
}
