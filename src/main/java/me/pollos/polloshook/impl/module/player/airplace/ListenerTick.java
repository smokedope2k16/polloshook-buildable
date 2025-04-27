package me.pollos.polloshook.impl.module.player.airplace;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;

public class ListenerTick extends SafeModuleListener<AirPlace, TickEvent> {
   public ListenerTick(AirPlace module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      if (mc.crosshairTarget != null) {
         ((AirPlace)this.module).pos = null;
         ((AirPlace)this.module).cancel = false;
         double range = (Boolean)((AirPlace)this.module).customRange.getValue() ? (double)(Float)((AirPlace)this.module).maxRange.getValue() : PlayerUtil.getReach();
         HitResult hitResult = mc.getCameraEntity().raycast(range, mc.getRenderTickCounter().getTickDelta(true), false);
         if (!(Boolean)((AirPlace)this.module).onlyInLiquid.getValue() || mc.player.isInFluid()) {
            if (mc.crosshairTarget.getType() == Type.MISS) {
               if (hitResult instanceof BlockHitResult) {
                  BlockHitResult blockHitResult = (BlockHitResult)hitResult;
                  if (mc.player.getMainHandStack().getItem() instanceof BlockItem || mc.player.getMainHandStack().getItem() instanceof SpawnEggItem) {
                     ((AirPlace)this.module).pos = blockHitResult.getBlockPos();
                     if (mc.options.useKey.isPressed() && ((AirPlace)this.module).timer.passed((double)((Float)((AirPlace)this.module).delay.getValue() * 100.0F))) {
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        ((AirPlace)this.module).cancel = true;
                        ((AirPlace)this.module).timer.reset();
                     }

                     return;
                  }
               }

            }
         }
      }
   }
}
