package me.pollos.polloshook.impl.module.movement.reversestep;

import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.combat.selffill.SelfFill;
import me.pollos.polloshook.impl.module.movement.jesus.Jesus;
import me.pollos.polloshook.impl.module.movement.longjump.LongJump;
import me.pollos.polloshook.impl.module.movement.speed.Speed;

public class ListenerMotion extends SafeModuleListener<ReverseStep, MotionUpdateEvent> {
   public ListenerMotion(ReverseStep module) {
      super(module, MotionUpdateEvent.class);
   }

   public void safeCall(MotionUpdateEvent event) {
      if (event.getStage() == Stage.POST) {
         if (!mc.player.isRiding() && !mc.player.isFallFlying() && !mc.player.isHoldingOntoLadder() && !mc.player.getAbilities().flying && !PlayerUtil.isInLiquid() && !mc.player.input.sneaking && !mc.player.input.jumping) {
            if (!((Speed)Managers.getModuleManager().get(Speed.class)).isEnabled() && !((LongJump)Managers.getModuleManager().get(LongJump.class)).isEnabled() && !((Jesus)Managers.getModuleManager().get(Jesus.class)).isGoingCrazyAF() && !((SelfFill)Managers.getModuleManager().get(SelfFill.class)).isEnabled()) {
               if (((ReverseStep)this.module).timer.passed(150L)) {
                  double downTrace = ((ReverseStep)this.module).traceDown();
                  if (downTrace != -1337.0D) {
                     if (mc.player.isOnGround() && mc.player.getVelocity().y <= 0.0D && mc.player.getY() - downTrace < (double)(Float)((ReverseStep)this.module).height.getValue()) {
                        if (downTrace > mc.player.getY()) {
                           return;
                        }

                        mc.player.setPosition(mc.player.getX(), downTrace, mc.player.getZ());
                     }

                  }
               }
            }
         }
      }
   }
}
