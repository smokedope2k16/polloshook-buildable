package me.pollos.polloshook.asm.mixins.world;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.asm.ducks.world.IClientWorldProperties;
import me.pollos.polloshook.impl.module.render.customsky.CustomSky;
import net.minecraft.client.world.ClientWorld.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({Properties.class})
public class MixinClientWorldProperties implements IClientWorldProperties {
   @Shadow
   private long timeOfDay;

   @ModifyConstant(
      method = {"getSkyDarknessHeight"},
      constant = {@Constant(
   doubleValue = 63.0D
)}
   )
   private double getSkyDarknessHeightHook(double constant) {
      return ((CustomSky)Managers.getModuleManager().get(CustomSky.class)).isAnyMode() ? 0.0D : constant;
   }

   @ModifyConstant(
      method = {"getHorizonShadingRatio"},
      constant = {@Constant(
   floatValue = 0.03125F
)}
   )
   private float getHorizonShadingRatioHook(float constant) {
      return ((CustomSky)Managers.getModuleManager().get(CustomSky.class)).isAnyMode() ? 1.0F : constant;
   }

   public long getRealTime() {
      return this.timeOfDay;
   }
}
