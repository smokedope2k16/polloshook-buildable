package me.pollos.polloshook.api.minecraft.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30;

public class MSAAFramebuffer extends Framebuffer {
   public static final int MIN_SAMPLES = 2;
   public static final int MAX_SAMPLES = GL30.glGetInteger(36183);
   private static final Map<Integer, MSAAFramebuffer> INSTANCES = new HashMap();
   private static final List<MSAAFramebuffer> ACTIVE_INSTANCES = new ArrayList();
   private final int samples;
   private int rboColor;
   private int rboDepth;
   private boolean inUse;

   private MSAAFramebuffer(int samples) {
      super(true);
      if (samples >= 2 && samples <= MAX_SAMPLES) {
         if ((samples & samples - 1) != 0) {
            throw new IllegalArgumentException("The number of samples must be keyCodec power of two.");
         } else {
            this.samples = samples;
            this.setClearColor(1.0F, 1.0F, 1.0F, 0.0F);
         }
      } else {
         throw new IllegalArgumentException(String.format("The number of samples should be >= %s and <= %s.", 2, MAX_SAMPLES));
      }
   }

   public static boolean framebufferInUse() {
      return !ACTIVE_INSTANCES.isEmpty();
   }

   public static MSAAFramebuffer getInstance(int samples) {
      return (MSAAFramebuffer)INSTANCES.computeIfAbsent(samples, (x) -> {
         return new MSAAFramebuffer(samples);
      });
   }

   public static void start(MSAAFramebuffer msaaBuffer, Framebuffer mainBuffer) {
      RenderSystem.assertOnRenderThreadOrInit();
      msaaBuffer.resize(mainBuffer.textureWidth, mainBuffer.textureHeight, true);
      GlStateManager._glBindFramebuffer(36008, mainBuffer.fbo);
      GlStateManager._glBindFramebuffer(36009, msaaBuffer.fbo);
      GlStateManager._glBlitFrameBuffer(0, 0, msaaBuffer.textureWidth, msaaBuffer.textureHeight, 0, 0, msaaBuffer.textureWidth, msaaBuffer.textureHeight, 16384, 9729);
      msaaBuffer.beginWrite(true);
   }

   public static void end(MSAAFramebuffer msaaBuffer, Framebuffer mainBuffer) {
      msaaBuffer.endWrite();
      GlStateManager._glBindFramebuffer(36008, msaaBuffer.fbo);
      GlStateManager._glBindFramebuffer(36009, mainBuffer.fbo);
      GlStateManager._glBlitFrameBuffer(0, 0, msaaBuffer.textureWidth, msaaBuffer.textureHeight, 0, 0, msaaBuffer.textureWidth, msaaBuffer.textureHeight, 16384, 9729);
      msaaBuffer.clear(true);
      mainBuffer.beginWrite(false);
   }

   public void resize(int width, int height, boolean getError) {
      if (this.textureWidth != width || this.textureHeight != height) {
         super.resize(width, height, getError);
      }

   }

   public void initFbo(int width, int height, boolean getError) {
      RenderSystem.assertOnRenderThreadOrInit();
      int maxSize = RenderSystem.maxSupportedTextureSize();
      if (width > 0 && width <= maxSize && height > 0 && height <= maxSize) {
         this.viewportWidth = width;
         this.viewportHeight = height;
         this.textureWidth = width;
         this.textureHeight = height;
         this.fbo = GlStateManager.glGenFramebuffers();
         GlStateManager._glBindFramebuffer(36160, this.fbo);
         this.rboColor = GlStateManager.glGenRenderbuffers();
         GlStateManager._glBindRenderbuffer(36161, this.rboColor);
         GL30.glRenderbufferStorageMultisample(36161, this.samples, 32856, width, height);
         GlStateManager._glBindRenderbuffer(36161, 0);
         this.rboDepth = GlStateManager.glGenRenderbuffers();
         GlStateManager._glBindRenderbuffer(36161, this.rboDepth);
         GL30.glRenderbufferStorageMultisample(36161, this.samples, 6402, width, height);
         GlStateManager._glBindRenderbuffer(36161, 0);
         GL30.glFramebufferRenderbuffer(36160, 36064, 36161, this.rboColor);
         GL30.glFramebufferRenderbuffer(36160, 36096, 36161, this.rboDepth);
         this.colorAttachment = MinecraftClient.getInstance().getFramebuffer().getColorAttachment();
         this.depthAttachment = MinecraftClient.getInstance().getFramebuffer().getDepthAttachment();
         this.checkFramebufferStatus();
         this.clear(getError);
         this.endRead();
      } else {
         throw new IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + maxSize + ")");
      }
   }

   public void delete() {
      RenderSystem.assertOnRenderThreadOrInit();
      this.endRead();
      this.endWrite();
      if (this.fbo > -1) {
         GlStateManager._glBindFramebuffer(36160, 0);
         GlStateManager._glDeleteFramebuffers(this.fbo);
         this.fbo = -1;
      }

      if (this.rboColor > -1) {
         GlStateManager._glDeleteRenderbuffers(this.rboColor);
         this.rboColor = -1;
      }

      if (this.rboDepth > -1) {
         GlStateManager._glDeleteRenderbuffers(this.rboDepth);
         this.rboDepth = -1;
      }

      this.colorAttachment = -1;
      this.depthAttachment = -1;
      this.textureWidth = -1;
      this.textureHeight = -1;
   }

   public void beginWrite(boolean setViewport) {
      super.beginWrite(setViewport);
      if (!this.inUse) {
         ACTIVE_INSTANCES.add(this);
         this.inUse = true;
      }

   }

   public void endWrite() {
      super.endWrite();
      if (this.inUse) {
         this.inUse = false;
         ACTIVE_INSTANCES.remove(this);
      }

   }
}
