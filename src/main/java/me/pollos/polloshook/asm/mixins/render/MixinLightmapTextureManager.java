package me.pollos.polloshook.asm.mixins.render;

import java.awt.Color;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.render.LightTextureEvent;
import me.pollos.polloshook.impl.module.render.fullbright.Fullbright;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LightmapTextureManager.class})
public abstract class MixinLightmapTextureManager {
   @Final
   @Shadow
   private NativeImageBackedTexture texture;
   @Final
   @Shadow
   private NativeImage image;
   @Shadow
   private boolean dirty;
   @Shadow
   private float flickerIntensity;
   @Final
   @Shadow
   private GameRenderer renderer;
   @Final
   @Shadow
   private MinecraftClient client;

   @Shadow
   public abstract float getDarknessFactor(float var1);

   @Shadow
   public abstract float getDarkness(LivingEntity var1, float var2, float var3);

   @Shadow
   public abstract float easeOutQuart(float var1);

   @Shadow
   protected static void clamp(Vector3f vec) {
   }

   @Shadow
   public static float getBrightness(DimensionType type, int lightLevel) {
      return 0.0F;
   }

   @ModifyArg(
      method = {"update"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"
),
      index = 2
   )
   private int updateHook(int color) {
      LightTextureEvent lightTextureEvent = new LightTextureEvent(color);
      PollosHook.getEventBus().dispatch(lightTextureEvent);
      return lightTextureEvent.getColor();
   }

   @Inject(
      method = {"update"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateHook(float delta, CallbackInfo ci) {
      Fullbright FULL_BRIGHT = (Fullbright)Managers.getModuleManager().get(Fullbright.class);
      if (FULL_BRIGHT.isEnabled() && (Boolean)FULL_BRIGHT.getColor().getValue()) {
         ci.cancel();
         if (this.dirty) {
            this.dirty = false;
            this.client.getProfiler().push("lightTex");
            ClientWorld clientWorld = this.client.world;
            if (clientWorld != null) {
               float f = clientWorld.getSkyBrightness(1.0F);
               float g;
               if (clientWorld.getLightningTicksLeft() > 0) {
                  g = 1.0F;
               } else {
                  g = f * 0.95F + 0.05F;
               }

               float h = ((Double)this.client.options.getDarknessEffectScale().getValue()).floatValue();
               float i = this.getDarknessFactor(delta) * h;
               float j = this.getDarkness(this.client.player, i, delta) * h;
               Vector3f vector3f = (new Vector3f(f, f, 1.0F)).lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
               float m = this.flickerIntensity + 1.5F;
               Vector3f vector3f2 = new Vector3f();

               for(int n = 0; n < 16; ++n) {
                  for(int o = 0; o < 16; ++o) {
                     float p = getBrightness(clientWorld.getDimension(), n) * g;
                     float q = getBrightness(clientWorld.getDimension(), o) * m;
                     float s = q * ((q * 0.6F + 0.4F) * 0.6F + 0.4F);
                     float t = q * (q * q * 0.6F + 0.4F);
                     vector3f2.set(q, s, t);
                     boolean bl = clientWorld.getDimensionEffects().shouldBrightenLighting();
                     float v1;
                     Vector3f vector3f5;
                     if (bl) {
                        vector3f2.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                        clamp(vector3f2);
                     } else {
                        Vector3f vector3f3 = (new Vector3f(vector3f)).mul(p);
                        vector3f2.add(vector3f3);
                        vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                        if (this.renderer.getSkyDarkness(delta) > 0.0F) {
                           v1 = this.renderer.getSkyDarkness(delta);
                           vector3f5 = (new Vector3f(vector3f2)).mul(0.7F, 0.6F, 0.6F);
                           vector3f2.lerp(vector3f5, v1);
                        }
                     }

                     float v = Math.max(vector3f2.x(), Math.max(vector3f2.y(), vector3f2.z()));
                     if (v < 2.0F) {
                        Color color = FULL_BRIGHT.getLightColor().getColor();
                        this.image.setColor(o, n, (new Color(color.getBlue(), color.getGreen(), color.getRed(), 255)).getRGB());
                     } else {
                        if (!bl) {
                           if (j > 0.0F) {
                              vector3f2.add(-j, -j, -j);
                           }

                           clamp(vector3f2);
                        }

                        v1 = ((Double)this.client.options.getGamma().getValue()).floatValue();
                        vector3f5 = new Vector3f(this.easeOutQuart(vector3f2.x), this.easeOutQuart(vector3f2.y), this.easeOutQuart(vector3f2.z));
                        vector3f2.lerp(vector3f5, Math.max(0.0F, v1 - i));
                        vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                        clamp(vector3f2);
                        vector3f2.mul(255.0F);
                        int x = (int)vector3f2.x();
                        int y = (int)vector3f2.y();
                        int z = (int)vector3f2.z();
                        LightTextureEvent lightTextureEvent = new LightTextureEvent(-16777216 | z << 16 | y << 8 | x);
                        PollosHook.getEventBus().dispatch(lightTextureEvent);
                        this.image.setColor(o, n, lightTextureEvent.getColor());
                     }
                  }
               }

               this.texture.upload();
               this.client.getProfiler().pop();
            }
         }
      }

   }
}
