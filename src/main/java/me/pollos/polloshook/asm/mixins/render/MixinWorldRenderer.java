package me.pollos.polloshook.asm.mixins.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.asm.ducks.render.IWorldRenderer;
import me.pollos.polloshook.asm.ducks.util.ICamera;
import me.pollos.polloshook.impl.events.render.BlockOutlineEvent;
import me.pollos.polloshook.impl.events.render.CaveCullingEvent;
import me.pollos.polloshook.impl.events.render.RenderEntityEvent;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.events.render.WeatherEvent;
import me.pollos.polloshook.impl.module.render.customsky.CustomSky;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import me.pollos.polloshook.impl.module.render.noweather.NoWeather;
import me.pollos.polloshook.impl.module.render.shader.Shader;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.DimensionEffects.SkyType;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Precipitation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({ WorldRenderer.class })
public abstract class MixinWorldRenderer implements IWorldRenderer {
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   @Nullable
   private ClientWorld world;
   @Shadow
   @Nullable
   private VertexBuffer lightSkyBuffer;
   @Shadow
   @Final
   private static Identifier SUN;
   @Shadow
   @Final
   private static Identifier MOON_PHASES;
   @Shadow
   @Nullable
   private VertexBuffer starsBuffer;
   @Shadow
   @Nullable
   private VertexBuffer darkSkyBuffer;
   @Unique
   private NoWeather.ForceWeatherEvent event;

   @Shadow
   protected abstract void renderEndSky(MatrixStack var1);

   @Shadow
   protected abstract boolean hasBlindnessOrDarkness(Camera var1);

   @Shadow
   public abstract void render(RenderTickCounter var1, boolean var2, Camera var3, GameRenderer var4,
         LightmapTextureManager var5, Matrix4f var6, Matrix4f var7);

   @Shadow
   protected abstract void renderEntity(Entity var1, double var2, double var4, double var6, float var8,
         MatrixStack var9, VertexConsumerProvider var10);

   @Inject(method = { "renderStars" }, at = { @At("HEAD") }, cancellable = true)
   private void renderStarsHook(CallbackInfo ci) {
      CustomSky.RenderStarsEvent event = CustomSky.RenderStarsEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(method = { "renderWeather" }, at = { @At("HEAD") }, cancellable = true)
   private void renderWeatherHook(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY,
         double cameraZ, CallbackInfo ci) {
      WeatherEvent weatherEvent = new WeatherEvent();
      PollosHook.getEventBus().dispatch(weatherEvent);
      if (weatherEvent.isCanceled()) {
         ci.cancel();
      }

   }

   @Redirect(method = {
         "renderWeather" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"))
   private float renderWeatherHook(ClientWorld instance, float v) {
      this.event = new NoWeather.ForceWeatherEvent(
            ((Biome) instance.getBiome(BlockPos.ofFloored(Interpolation.getCameraPos())).value())
                  .getPrecipitation(BlockPos.ofFloored(Interpolation.getCameraPos())));
      this.event.dispatch();
      return this.event.isCanceled() ? 1.0E-5F : v;
   }

   @Redirect(method = {
         "renderWeather" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"))
   private Precipitation renderWeatherHook(Biome instance, BlockPos pos) {
      Precipitation precipitation = instance.getPrecipitation(pos);
      this.event.setPrecipitation(precipitation);
      this.event.dispatch();
      return this.event.isCanceled() ? this.event.getPrecipitation() : precipitation;
   }

   @ModifyArgs(method = {
         "renderWeather" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"))
   private void renderWeatherHook(Args args) {
      NoWeather.WeatherAlphaEvent event = new NoWeather.WeatherAlphaEvent();
      event.dispatch();
      if (event.isCanceled()) {
         args.set(3, Math.max(0.0F, Math.min(1.0F, event.getAlpha())));
      }

   }

   @Inject(method = { "tickRainSplashing" }, at = { @At("HEAD") }, cancellable = true)
   public void tickRainSplashingHook(Camera camera, CallbackInfo ci) {
      if (((NoWeather) Managers.getModuleManager().get(NoWeather.class)).isEnabled()) {
         ci.cancel();
      }

   }

   @Redirect(method = {
         "render" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(F)V", ordinal = 0))
   private void renderHook(PostEffectProcessor instance, float tickDelta) {
      Shader SHADER = (Shader) Managers.getModuleManager().get(Shader.class);
      if (SHADER.isEnabled()) {
         if (Managers.getShaderManager().nullCheck()) {
            return;
         }

         Managers.getShaderManager().setupShader(SHADER.isRainbow(),
               Managers.getShaderManager().getShaderOutline(SHADER.isRainbow()));
      } else {
         instance.render(tickDelta);
      }

   }

   @Inject(method = { "render" }, at = {
         @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;", ordinal = 3) })
   private void render_cameraHook(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera,
         GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f,
         Matrix4f matrix4f2, CallbackInfo ci) {
      ICamera access = (ICamera) camera;
      Entity lastEntity = camera.getFocusedEntity();
      if (((Freecam) Managers.getModuleManager().get(Freecam.class)).isEnabled()) {
         access.setFocusedEntity(this.client.player);
      } else if (camera.getFocusedEntity() == this.client.player) {
         access.setFocusedEntity(lastEntity);
      }

   }

   @ModifyArg(method = {
         "render" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;ZZ)V"), index = 3)
   private boolean renderHook(boolean spectate) {
      CaveCullingEvent event = new CaveCullingEvent();
      event.dispatch();
      return event.isCanceled() ? true : spectate;
   }

   @Inject(method = { "render" }, at = { @At("RETURN") })
   private void renderHook(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera,
         GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f,
         Matrix4f matrix4f2, CallbackInfo ci, @Local MatrixStack matrixStack) {
      this.client.getProfiler().push("polloshook-Render");
      RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
      matrixStack.multiplyPositionMatrix(matrix4f);
      RenderEvent event = new RenderEvent(tickCounter.getTickDelta(true), matrixStack, camera, matrix4f2, matrix4f);
      PollosHook.getEventBus().dispatch(event);
      this.client.getProfiler().pop();
   }

   @Inject(method = { "renderWorldBorder" }, at = { @At("HEAD") }, cancellable = true)
   private void renderWorldBorderHook(Camera camera, CallbackInfo ci) {
      NoRender.WorldBorderEvent event = NoRender.WorldBorderEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(method = { "renderEntity" }, at = { @At("HEAD") }, cancellable = true)
   private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta,
         MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
      RenderEntityEvent event = new RenderEntityEvent(entity);
      PollosHook.getEventBus().dispatch(event);
      if (event.isCanceled()) {
         info.cancel();
      }

   }

   @Inject(method = { "drawBlockOutline" }, at = { @At("HEAD") }, cancellable = true)
   private void drawBlockOutlineHook(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d,
         double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
      BlockOutlineEvent blockOutlineEvent = new BlockOutlineEvent();
      PollosHook.getEventBus().dispatch(blockOutlineEvent);
      if (blockOutlineEvent.isCanceled()) {
         ci.cancel();
      }

   }

   @Redirect(method = {
         "render" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V"))
   private void renderHook(WorldRenderer instance, Matrix4f matrix4f, Matrix4f projectionMatrix, float tickDelta,
         Camera camera, boolean thickFog, Runnable fogCallback) {
      this.renderCustomSky(matrix4f, projectionMatrix, tickDelta, camera, thickFog, fogCallback);
   }

   @Unique
   private void renderCustomSky(Matrix4f matrix4f, Matrix4f projectionMatrix, float tickDelta, Camera camera,
         boolean thickFog, Runnable fogCallback) {
      fogCallback.run();
      if (!thickFog) {
         CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
         if (cameraSubmersionType != CameraSubmersionType.POWDER_SNOW
               && cameraSubmersionType != CameraSubmersionType.LAVA && !this.hasBlindnessOrDarkness(camera)) {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.multiplyPositionMatrix(matrix4f);
            if (this.client.world.getDimensionEffects().getSkyType() == SkyType.END) {
               this.renderEndSky(matrixStack);
            } else if (this.client.world.getDimensionEffects().getSkyType() == SkyType.NORMAL) {
               Vec3d vec3d = this.world.getSkyColor(this.client.gameRenderer.getCamera().getPos(), tickDelta);
               float f = (float) vec3d.x;
               float g = (float) vec3d.y;
               float h = (float) vec3d.z;
               BackgroundRenderer.applyFogColor();
               Tessellator tessellator = Tessellator.getInstance();
               RenderSystem.depthMask(false);
               RenderSystem.setShaderColor(f, g, h, 1.0F);
               ShaderProgram shaderProgram = RenderSystem.getShader();
               this.lightSkyBuffer.bind();
               this.lightSkyBuffer.draw(matrixStack.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
               VertexBuffer.unbind();
               RenderSystem.enableBlend();
               float[] fs = this.world.getDimensionEffects().getFogColorOverride(this.world.getSkyAngle(tickDelta),
                     tickDelta);
               float q;
               float p;
               float o;
               float k;
               float i;
               int n;
               if (fs != null) {
                  RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  matrixStack.push();
                  matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                  i = MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                  matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));
                  matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
                  float j = fs[0];
                  k = fs[1];
                  float l = fs[2];
                  Matrix4f matrix4f2 = matrixStack.peek().getPositionMatrix();
                  BufferBuilder bufferBuilder = tessellator.begin(DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
                  bufferBuilder.vertex(matrix4f2, 0.0F, 100.0F, 0.0F).color(j, k, l, fs[3]);
                  int m = 1;

                  for (n = 0; n <= 16; ++n) {
                     o = (float) n * 6.2831855F / 16.0F;
                     p = MathHelper.sin(o);
                     q = MathHelper.cos(o);
                     bufferBuilder.vertex(matrix4f2, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).color(fs[0], fs[1],
                           fs[2], 0.0F);
                  }

                  BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                  matrixStack.pop();
               }

               RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.ONE, SrcFactor.ONE, DstFactor.ZERO);
               matrixStack.push();
               i = 1.0F - this.world.getRainGradient(tickDelta);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, i);
               matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
               matrixStack
                     .multiply(RotationAxis.POSITIVE_X.rotationDegrees(this.world.getSkyAngle(tickDelta) * 360.0F));
               Matrix4f matrix4f3 = matrixStack.peek().getPositionMatrix();
               CustomSky.RenderSunOrMoonEvent sunEvent = CustomSky.RenderSunOrMoonEvent.create();
               sunEvent.dispatch();
               if (!sunEvent.isCanceled()) {
                  k = 30.0F;
                  RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                  RenderSystem.setShaderTexture(0, SUN);
                  BufferBuilder bufferBuilder2 = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                  bufferBuilder2.vertex(matrix4f3, -k, 100.0F, -k).texture(0.0F, 0.0F);
                  bufferBuilder2.vertex(matrix4f3, k, 100.0F, -k).texture(1.0F, 0.0F);
                  bufferBuilder2.vertex(matrix4f3, k, 100.0F, k).texture(1.0F, 1.0F);
                  bufferBuilder2.vertex(matrix4f3, -k, 100.0F, k).texture(0.0F, 1.0F);
                  BufferRenderer.drawWithGlobalProgram(bufferBuilder2.end());
                  k = 20.0F;
                  RenderSystem.setShaderTexture(0, MOON_PHASES);
                  int r = this.world.getMoonPhase();
                  n = r % 4;
                  int m = r / 4 % 2;
                  float t = (float) n / 4.0F;
                  o = (float) m / 2.0F;
                  p = (float) (n + 1) / 4.0F;
                  q = (float) (m + 1) / 2.0F;
                  bufferBuilder2 = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
                  bufferBuilder2.vertex(matrix4f3, -k, -100.0F, k).texture(p, q);
                  bufferBuilder2.vertex(matrix4f3, k, -100.0F, k).texture(t, q);
                  bufferBuilder2.vertex(matrix4f3, k, -100.0F, -k).texture(t, o);
                  bufferBuilder2.vertex(matrix4f3, -k, -100.0F, -k).texture(p, o);
                  BufferRenderer.drawWithGlobalProgram(bufferBuilder2.end());
               }

               float u = this.world.getStarBrightness(tickDelta) * i;
               CustomSky.RenderStarsEvent starEvent = CustomSky.RenderStarsEvent.create();
               starEvent.dispatch();
               if (!starEvent.isCanceled() && u > 0.0F) {
                  RenderSystem.setShaderColor(u, u, u, u);
                  BackgroundRenderer.clearFog();
                  this.starsBuffer.bind();
                  this.starsBuffer.draw(matrixStack.peek().getPositionMatrix(), projectionMatrix,
                        GameRenderer.getPositionProgram());
                  VertexBuffer.unbind();
                  fogCallback.run();
               }

               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.disableBlend();
               RenderSystem.defaultBlendFunc();
               matrixStack.pop();
               RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
               double d = this.client.player.getCameraPosVec(tickDelta).y
                     - this.world.getLevelProperties().getSkyDarknessHeight(this.world);
               if (d < 0.0D) {
                  matrixStack.push();
                  matrixStack.translate(0.0F, 12.0F, 0.0F);
                  this.darkSkyBuffer.bind();
                  this.darkSkyBuffer.draw(matrixStack.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
                  VertexBuffer.unbind();
                  matrixStack.pop();
               }

               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.depthMask(true);
            }
         }
      }
   }
}
