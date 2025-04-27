package me.pollos.polloshook.impl.module.misc.antiafk;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.attribute.EntityAttributes;

public class ListenerUpdate extends ModuleListener<AntiAFK, UpdateEvent> {
   public ListenerUpdate(AntiAFK module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (mc.player.input != null) {
         Class<?> clazz = mc.player.input.getClass();
         if (clazz == KeyboardInput.class || clazz == Input.class) {
            ((AntiAFK)this.module).input.tick(mc.player.shouldSlowDown(), (float)mc.player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
            mc.player.input = ((AntiAFK)this.module).input;
         }

      }
   }
}