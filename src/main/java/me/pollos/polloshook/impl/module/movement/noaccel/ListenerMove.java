package me.pollos.polloshook.impl.module.movement.noaccel;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.player.scaffold.Scaffold;

public class ListenerMove extends ModuleListener<NoAccel, MoveEvent> {
   public ListenerMove(NoAccel module) {
      super(module, MoveEvent.class, 1000);
   }

   public void call(MoveEvent event) {
      if (!((Scaffold)Managers.getModuleManager().get(Scaffold.class)).isEnabled()) {
         if (!((NoAccel)this.module).cantNoAccel()) {
            ((NoAccel)this.module).strafe(event, MovementUtil.getDefaultMoveSpeed());
         }

      }
   }
}
