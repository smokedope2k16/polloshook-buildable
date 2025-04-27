package me.pollos.polloshook.impl.module.player.sprint;

import java.util.ArrayList;
import java.util.Arrays;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.player.sprint.mode.SprintMode;
import net.minecraft.client.option.KeyBinding;

public class ListenerUpdate extends SafeModuleListener<Sprint, UpdateEvent> {
   private final KeyBinding sprint;

   public ListenerUpdate(Sprint module) {
      super(module, UpdateEvent.class);
      this.sprint = mc.options.sprintKey;
   }

   public void safeCall(UpdateEvent event) {
      if (!((Sprint)this.module).doReturn()) {
         if (((Sprint)this.module).canSprint()) {
            switch((SprintMode)((Sprint)this.module).mode.getValue()) {
            case RAGE:
               ArrayList<Boolean> booleans = new ArrayList(Arrays.asList(mc.options.forwardKey.isPressed() || mc.player.input.pressingForward, mc.options.leftKey.isPressed() || mc.player.input.pressingLeft, mc.options.rightKey.isPressed() || mc.player.input.pressingRight, mc.options.backKey.isPressed() || mc.player.input.pressingBack));
               booleans.forEach((bl) -> {
                  if (bl) {
                     mc.player.setSprinting(true);
                  }

               });
               break;
            case LEGIT:
               boolean pressingW = mc.options.forwardKey.isPressed() || mc.player.input.pressingForward;
               if (pressingW && (float)mc.player.getHungerManager().getFoodLevel() > 6.0F && !mc.player.isSneaking() && !mc.player.horizontalCollision) {
                  KeyBinding.setKeyPressed(this.sprint.getDefaultKey(), true);
               }
            }
         }

      }
   }
}
