package me.pollos.polloshook.impl.module.player.sprint;

import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.player.sprint.mode.SprintMode;

public class ListenerMotion extends ModuleListener<Sprint, MotionUpdateEvent> {
   public ListenerMotion(Sprint module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (!((Sprint)this.module).doReturn() && !event.isModified() && ((Sprint)this.module).mode.getValue() != SprintMode.LEGIT) {
         if ((Boolean)((Sprint)this.module).rotate.getValue() && event.getStage() == Stage.PRE && MovementUtil.isMoving() && mc.player.isSprinting()) {
            float angle = MovementUtil.getSpoofYaw();
            Managers.getRotationManager().setRotations(angle, event.getPitch(), event);
         }

      }
   }
}
