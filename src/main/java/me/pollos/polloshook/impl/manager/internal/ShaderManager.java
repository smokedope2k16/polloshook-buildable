package me.pollos.polloshook.impl.manager.internal;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.shader.managed.ManagedShaderEffect;
import me.pollos.polloshook.api.minecraft.render.shader.managed.ShaderEffectManager;
import me.pollos.polloshook.asm.ducks.shader.IShaderEffect;
import me.pollos.polloshook.impl.module.render.shader.Shader;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.util.Identifier;

public class ShaderManager implements Minecraftable {
   private ShaderManager.MyFramebuffer shaderBuffer;
   public float time = 0.0F;
   public static ManagedShaderEffect DEFAULT;
   public static ManagedShaderEffect DEFAULT_OUTLINE;
   public static ManagedShaderEffect RAINBOW;
   public static ManagedShaderEffect RAINBOW_OUTLINE;

   public void applyShader(Runnable runnable, boolean rainbow) {
      if (!this.nullCheck()) {
         Framebuffer MCBuffer = mc.getFramebuffer();
         RenderSystem.assertOnRenderThreadOrInit();
         if (this.shaderBuffer.textureWidth != MCBuffer.textureWidth || this.shaderBuffer.textureHeight != MCBuffer.textureHeight) {
            this.shaderBuffer.resize(MCBuffer.textureWidth, MCBuffer.textureHeight, false);
         }

         GlStateManager._glBindFramebuffer(36009, this.shaderBuffer.fbo);
         this.shaderBuffer.beginWrite(true);
         runnable.run();
         this.shaderBuffer.endWrite();
         GlStateManager._glBindFramebuffer(36009, MCBuffer.fbo);
         MCBuffer.beginWrite(false);
         ManagedShaderEffect shader = this.getShader(rainbow);
         Framebuffer mainBuffer = mc.getFramebuffer();
         PostEffectProcessor effect = shader.getShaderEffect();
         if (effect != null) {
            ((IShaderEffect)effect).addHook("bufIn", this.shaderBuffer);
         }

         Framebuffer outBuffer = shader.getShaderEffect().getSecondaryTarget("bufOut");
         if (outBuffer != null) {
            this.setupShader(rainbow, shader);
            this.shaderBuffer.clear(false);
            mainBuffer.beginWrite(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA, SrcFactor.ZERO, DstFactor.ONE);
            RenderSystem.backupProjectionMatrix();
            outBuffer.draw(outBuffer.textureWidth, outBuffer.textureHeight, false);
            RenderSystem.restoreProjectionMatrix();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
         }

      }
   }

   public void setupShader(boolean rainbow, ManagedShaderEffect effect) {
      if (this.shaderBuffer != null) {
         Shader shaderChams = (Shader)Managers.getModuleManager().get(Shader.class);
         Color color = shaderChams.getColor().getColor();
         if (rainbow) {
            effect.setUniformValue("color", (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)shaderChams.getColor().getColor().getAlpha() / 255.0F);
            effect.setUniformValue("fillrainbow", (Boolean)shaderChams.getFillRainbow().getValue() ? 1.0F : 0.0F);
            effect.setUniformValue("time", this.time);
            effect.setUniformValue("saturation", (Float)shaderChams.getSaturation().getValue() / 100.0F);
            effect.setUniformValue("lightness", (Float)shaderChams.getLightness().getValue() / 100.0F);
            effect.setUniformValue("factor", (Float)shaderChams.getFactor().getValue());
            effect.setUniformValue("lineWidth", ((Float)shaderChams.getLineWith().getValue()).intValue());
            effect.setUniformValue("resolution", (float)mc.getWindow().getScaledWidth(), (float)mc.getWindow().getScaledHeight());
            effect.render(mc.getRenderTickCounter().getTickDelta(false));
            this.time += (Float)shaderChams.getSpeed().getValue() * 0.002F;
         } else {
            effect.setUniformValue("color", (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)shaderChams.getColor().getColor().getAlpha() / 255.0F);
            effect.setUniformValue("lineWidth", ((Float)shaderChams.getLineWith().getValue()).intValue());
            effect.setUniformValue("resolution", (float)mc.getWindow().getScaledWidth(), (float)mc.getWindow().getScaledHeight());
            effect.render(mc.getRenderTickCounter().getTickDelta(false));
         }

      }
   }

   public void reloadShaders() {
      DEFAULT = ShaderEffectManager.getInstance().manage(Identifier.of("shaders/post/outline.json"));
      DEFAULT_OUTLINE = ShaderEffectManager.getInstance().manage(Identifier.of("shaders/post/outline.json"), (managedShaderEffect) -> {
         PostEffectProcessor effect = managedShaderEffect.getShaderEffect();
         if (effect != null) {
            ((IShaderEffect)effect).addHook("bufIn", mc.worldRenderer.getEntityOutlinesFramebuffer());
            ((IShaderEffect)effect).addHook("bufOut", mc.worldRenderer.getEntityOutlinesFramebuffer());
         }
      });
      RAINBOW = ShaderEffectManager.getInstance().manage(Identifier.of("shaders/post/rainbow.json"));
      RAINBOW_OUTLINE = ShaderEffectManager.getInstance().manage(Identifier.of("shaders/post/rainbow.json"), (managedShaderEffect) -> {
         PostEffectProcessor effect = managedShaderEffect.getShaderEffect();
         if (effect != null) {
            ((IShaderEffect)effect).addHook("bufIn", mc.worldRenderer.getEntityOutlinesFramebuffer());
            ((IShaderEffect)effect).addHook("bufOut", mc.worldRenderer.getEntityOutlinesFramebuffer());
         }
      });
   }

   public ManagedShaderEffect getShader(boolean rainbow) {
      return rainbow ? RAINBOW : DEFAULT;
   }

   public ManagedShaderEffect getShaderOutline(boolean rainbow) {
      return rainbow ? RAINBOW_OUTLINE : DEFAULT_OUTLINE;
   }

   public boolean nullCheck() {
      if (mc.getFramebuffer() == null) {
         return true;
      } else if (DEFAULT != null && DEFAULT_OUTLINE != null && RAINBOW != null && RAINBOW_OUTLINE != null && this.shaderBuffer != null) {
         return false;
      } else {
         this.shaderBuffer = new ShaderManager.MyFramebuffer(mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight);
         this.reloadShaders();
         return true;
      }
   }

   
   public ShaderManager.MyFramebuffer getShaderBuffer() {
      return this.shaderBuffer;
   }

   public static class MyFramebuffer extends Framebuffer {
      public MyFramebuffer(int width, int height) {
         super(false);
         RenderSystem.assertOnRenderThreadOrInit();
         this.resize(width, height, true);
         this.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      }
   }
}
