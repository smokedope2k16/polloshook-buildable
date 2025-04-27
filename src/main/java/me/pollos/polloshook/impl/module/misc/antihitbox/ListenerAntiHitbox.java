package me.pollos.polloshook.impl.module.misc.antihitbox;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.world.AntiHitboxEvent;
import net.minecraft.util.hit.HitResult.Type;

public class ListenerAntiHitbox extends SafeModuleListener<AntiHitbox, AntiHitboxEvent> {
   public ListenerAntiHitbox(AntiHitbox module) {
      super(module, AntiHitboxEvent.class);
   }

   public void safeCall(AntiHitboxEvent event) {
      if (mc.crosshairTarget != null) {
         if (mc.crosshairTarget.getType() == Type.BLOCK || !(Boolean)((AntiHitbox)this.module).onlyBlock.getValue()) {
            event.setCanceled(((AntiHitbox)this.module).isValid);
         }
      }
   }
}
