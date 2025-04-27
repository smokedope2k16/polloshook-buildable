package me.pollos.polloshook.asm.mixins.block;

import java.util.Iterator;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.movement.velocity.Velocity;
import net.minecraft.fluid.FlowableFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({FlowableFluid.class})
public class MixinFlowableLiquid {
   @Redirect(
      method = {"getVelocity"},
      at = @At(
   value = "INVOKE",
   target = "Ljava/util/Iterator;hasNext()Z",
   ordinal = 0
)
   )
   private boolean getVelocityHook(Iterator<?> instance) {
      return ((Velocity)Managers.getModuleManager().get(Velocity.class)).liquidPush() ? false : instance.hasNext();
   }
}
