package me.pollos.polloshook.impl.module.movement.autowalk;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.misc.antiafk.AntiAFK;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.attribute.EntityAttributes;

public class ListenerUpdate extends ModuleListener<AutoWalk, UpdateEvent> {
   public ListenerUpdate(AutoWalk module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (mc.player.input != null) {
         Class<?> clazz = mc.player.input.getClass();
         AntiAFK ANTI_AFK = (AntiAFK)Managers.getModuleManager().get(AntiAFK.class);
         if (clazz == KeyboardInput.class || clazz == Input.class || ANTI_AFK.getInput().getClass() == clazz) {
            ((AutoWalk)this.module).input.tick(mc.player.shouldSlowDown(), (float)mc.player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            mc.player.input = ((AutoWalk)this.module).input;
         }

      }
   }
}
