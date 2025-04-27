package me.pollos.polloshook.impl.module.movement.speed;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.holesnap.HoleSnap;
import me.pollos.polloshook.impl.module.movement.longjump.LongJump;
import me.pollos.polloshook.impl.module.movement.speed.type.SpeedTypeEnum;

public class ListenerMove extends ModuleListener<Speed, MoveEvent> {
   public ListenerMove(Speed module) {
      super(module, MoveEvent.class);
   }

   public void call(MoveEvent event) {
      if ((Boolean)((Speed)this.module).liquid.getValue() && (EntityUtil.isInWater(Managers.getPositionManager().getBB()) || PlayerUtil.isInLiquid())) {
         ((SpeedTypeEnum)((Speed)this.module).mode.getValue()).getType().reset();
      } else if (((Speed)this.module).timer.passed(100L) && !mc.player.isFallFlying() && !mc.player.isClimbing() && !mc.player.noClip && !mc.player.isClimbing()) {
         if (!((HoleSnap)Managers.getModuleManager().get(HoleSnap.class)).isEnabled() && !((LongJump)Managers.getModuleManager().get(LongJump.class)).isEnabled()) {
            if (MovementUtil.isMoving()) {
               if ((Boolean)((Speed)this.module).useTimer.getValue()) {
                  Managers.getTimerManager().setYieldTimer(true);
                  Managers.getTimerManager().set(1.088F);
                  Managers.getTimerManager().setYieldTimer(false);
               }

               ((SpeedTypeEnum)((Speed)this.module).mode.getValue()).getType().move(event);
            } else {
               event.setVec(0.0D, event.getY(), 0.0D);
               mc.player.setVelocity(0.0D, event.getY(), 0.0D);
               ((SpeedTypeEnum)((Speed)this.module).mode.getValue()).getType().reset();
            }

         } else {
            ((SpeedTypeEnum)((Speed)this.module).mode.getValue()).getType().reset();
         }
      } else {
         ((SpeedTypeEnum)((Speed)this.module).mode.getValue()).getType().reset();
      }
   }
}
