package me.pollos.polloshook.impl.module.movement.invwalk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.manager.minecraft.movement.RotationManager;

public class ListenerMotion extends ModuleListener<InvWalk, MotionUpdateEvent> {
   public ListenerMotion(InvWalk module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (((InvWalk)this.module).isValidScreen(mc.currentScreen) && (Boolean)((InvWalk)this.module).rotate.getValue()) {
         List<Integer> arrowKeys = Arrays.asList(265, 264, 263, 262);
         List<Integer> pressedKeys = new ArrayList();
         arrowKeys.forEach((key) -> {
            if (KeyboardUtil.isPressed(key)) {
               pressedKeys.add(key);
            }

         });
         pressedKeys.removeIf((k) -> {
            return !KeyboardUtil.isPressed(k);
         });
         if (!pressedKeys.isEmpty()) {
            pressedKeys.forEach(this::handleKey);
         }
      }
   }

   protected void handleKey(Integer key) {
      RotationManager rotate = Managers.getRotationManager();
      if (mc.player.getPitch() > 90.0F) {
         mc.player.setPitch(90.0F);
      }

      if (mc.player.getPitch() < -90.0F) {
         mc.player.setPitch(-90.0F);
      }

      float speed = 5.0F;
      switch(key) {
      case 262:
         rotate.setYaw(mc.player.getYaw() + speed);
         break;
      case 263:
         rotate.setYaw(mc.player.getYaw() - speed);
         break;
      case 264:
         rotate.setPitch(mc.player.getPitch() + speed);
         break;
      case 265:
         rotate.setPitch(mc.player.getPitch() - speed);
      }

   }
}
