package me.pollos.polloshook.api.minecraft.render.shader;

import com.mojang.logging.LogUtils;
import me.pollos.polloshook.api.minecraft.render.shader.managed.ManagedFramebuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.util.Window;

public final class FramebufferWrapper implements ManagedFramebuffer {
   private final String name;
   private Framebuffer wrapped;

   FramebufferWrapper(String name) {
      this.name = name;
   }

   void findTarget(PostEffectProcessor shaderEffect) {
      if (shaderEffect == null) {
         this.wrapped = null;
      } else {
         this.wrapped = shaderEffect.getSecondaryTarget(this.name);
         if (this.wrapped == null) {
            LogUtils.getLogger().warn("No target framebuffer found with name {} in shader {}", this.name, shaderEffect.getName());
         }
      }

   }

   public String getName() {
      return this.name;
   }

   public Framebuffer getFramebuffer() {
      return this.wrapped;
   }

   public void beginWrite(boolean updateViewport) {
      if (this.wrapped != null) {
         this.wrapped.beginWrite(updateViewport);
      }

   }

   public void draw() {
      Window window = MinecraftClient.getInstance().getWindow();
      this.draw(window.getFramebufferWidth(), window.getFramebufferHeight(), true);
   }

   public void draw(int width, int height, boolean disableBlend) {
      if (this.wrapped != null) {
         this.wrapped.draw(width, height, disableBlend);
      }

   }

   public void clear() {
      this.clear(MinecraftClient.IS_SYSTEM_MAC);
   }

   public void clear(boolean swallowErrors) {
      if (this.wrapped != null) {
         this.wrapped.clear(swallowErrors);
      }

   }
}
