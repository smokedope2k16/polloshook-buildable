package me.pollos.polloshook.impl.module.movement.autowalk;

import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.keyboard.InputKeyDownEvent;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.KeyBinding;

public class ListenerInput extends ModuleListener<AutoWalk, InputKeyDownEvent> {
   public ListenerInput(AutoWalk module) {
      super(module, InputKeyDownEvent.class);
   }

   public void call(InputKeyDownEvent event) {
      Input input = event.getInput();
      KeyBinding binding = event.getBinding();
      Freecam FREECAM = (Freecam)Managers.getModuleManager().get(Freecam.class);
      if (input.equals(((AutoWalk)this.module).input)) {
         if (binding == mc.options.forwardKey) {
            event.setPressed(true);
            return;
         }

         List<KeyBinding> asd = Arrays.asList(mc.options.leftKey, mc.options.backKey, mc.options.rightKey);
         if (FREECAM != null && FREECAM.isEnabled() || (Boolean)((AutoWalk)this.module).lock.getValue() && asd.contains(binding)) {
            event.setPressed(false);
         }
      }

   }
}
