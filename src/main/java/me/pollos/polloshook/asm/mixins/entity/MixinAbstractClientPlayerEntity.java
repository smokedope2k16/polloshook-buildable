package me.pollos.polloshook.asm.mixins.entity;

import com.mojang.authlib.GameProfile;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.impl.module.misc.nameprotect.NameProtect;
import me.pollos.polloshook.impl.module.misc.nameprotect.mode.SpoofSkinMode;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerEntity;
import me.pollos.polloshook.impl.module.render.fovmodifier.FOVModifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.SkinTextures.Model;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractClientPlayerEntity.class})
public abstract class MixinAbstractClientPlayerEntity extends PlayerEntity implements Minecraftable {
   @Shadow
   public abstract SkinTextures getSkinTextures();

   @Shadow
   @Nullable
   protected abstract PlayerListEntry getPlayerListEntry();

   public MixinAbstractClientPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
      super(world, pos, yaw, gameProfile);
   }

   @Inject(
      method = {"getFovMultiplier"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getFovMultiplierHook(CallbackInfoReturnable<Float> cir) {
      FOVModifier FOV_MODULE = (FOVModifier)Managers.getModuleManager().get(FOVModifier.class);
      if (FOV_MODULE.isEnabled()) {
         float f = 1.0F;
         if (FOV_MODULE.getStaticFOV()) {
            cir.setReturnValue(1.0F);
            return;
         }

         if (this.getAbilities().flying && FOV_MODULE.getFlying() != 0.0F) {
            f *= 1.1F * FOV_MODULE.getFlying();
         }

         double movementSpeed;
         if (mc.player.isSprinting() && FOV_MODULE.getSprinting() != 0.0F) {
            movementSpeed = 0.13000000312924387D * (double)FOV_MODULE.getSprinting();
         } else {
            movementSpeed = 0.10000000149011612D;
         }

         float walkSpeed = 0.1F;
         f = (float)((double)f * ((movementSpeed / (double)walkSpeed + 1.0D) / 2.0D));
         StatusEffectInstance speed = this.getStatusEffect(StatusEffects.SPEED);
         StatusEffectInstance slowness = this.getStatusEffect(StatusEffects.SLOWNESS);
         if (speed != null && FOV_MODULE.getSwiftness() != 0.0F) {
            if (!FOV_MODULE.getCalculateLevel()) {
               f *= 1.1F * FOV_MODULE.getSwiftness();
            } else {
               f *= (1.1F + (float)speed.getAmplifier() / 10.0F) * FOV_MODULE.getSwiftness();
            }
         }

         if (slowness != null && FOV_MODULE.getSlowness() != 0.0F) {
            if (!FOV_MODULE.getCalculateLevel()) {
               f -= 0.075F * FOV_MODULE.getSlowness();
            } else {
               f = (f - 0.075F - (float)slowness.getAmplifier() / 10.0F) * FOV_MODULE.getSlowness();
            }
         }

         ItemStack itemStack = this.getActiveItem();
         if (this.isUsingItem()) {
            if (itemStack.isOf(Items.BOW)) {
               int i = this.getItemUseTime();
               float g = (float)i / 20.0F;
               g = g > 1.0F ? 1.0F : g * g;
               f *= (1.0F - g * 0.15F) * FOV_MODULE.getAiming();
            } else if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && this.isUsingSpyglass()) {
               f = 0.1F * FOV_MODULE.getSpy();
            }
         }

         cir.setReturnValue(MathHelper.lerp(((Double)MinecraftClient.getInstance().options.getFovEffectScale().getValue()).floatValue(), 1.0F, f));
      }

   }

   @Inject(
      method = {"getSkinTextures"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getSkinTexturesHook(CallbackInfoReturnable<SkinTextures> cir) {
      if (!PlayerUtil.isNull() && this.getGameProfile() != null) {
         Object thisObj = this;

         if (thisObj instanceof FakePlayerEntity) {
            cir.setReturnValue(mc.getSkinProvider().getSkinTextures(mc.player.getGameProfile()));
         }

         if (thisObj instanceof ClientPlayerEntity) {
            NameProtect NAME_PROTECT = (NameProtect)Managers.getModuleManager().get(NameProtect.class);
            if (NAME_PROTECT.getSpoofSkin().getValue() == SpoofSkinMode.SELF) {
               return;
            }

            if (NAME_PROTECT.isEnabled() && (Boolean)NAME_PROTECT.getCustomSkin().getValue()) {
               Identifier skinLocation = Identifier.of("textures/polloshook/%s.png".formatted(new Object[]{NAME_PROTECT.getSkinName().getValue()}));
               SkinTextures skinTextures = this.createSkinTextureSafe();
               SkinTextures newTexture = new SkinTextures(skinLocation, skinTextures.textureUrl(), skinTextures.capeTexture(), skinTextures.elytraTexture(), (Model)NAME_PROTECT.getModel().getValue(), skinTextures.secure());
               cir.setReturnValue(newTexture);
            }
         }

      }
   }

   @Unique
   private SkinTextures createSkinTextureSafe() {
      PlayerListEntry playerListEntry = this.getPlayerListEntry();
      SkinTextures firstTexture = playerListEntry == null ? DefaultSkinHelper.getSkinTextures(this.getUuid()) : playerListEntry.getSkinTextures();
      return firstTexture != null ? firstTexture : new SkinTextures(Identifier.of("textures/entity/player/wide/sunny.png"), (String)null, (Identifier)null, (Identifier)null, Model.SLIM, true);
   }
}