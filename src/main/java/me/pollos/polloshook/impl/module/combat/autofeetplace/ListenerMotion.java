package me.pollos.polloshook.impl.module.combat.autofeetplace;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.BlockPlaceModule;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import net.minecraft.util.math.BlockPos;

public class ListenerMotion extends ModuleListener<AutoFeetPlace, MotionUpdateEvent> {
   public ListenerMotion(AutoFeetPlace module) {
      super(module, MotionUpdateEvent.class, 8000);
   }

   public void call(MotionUpdateEvent event) {
      if (!((AutoFeetPlace)this.module).handleJump((BlockPlaceModule)this.module)) {
         Set<BlockPos> blocked = ((AutoFeetPlace)this.module).createBlocked();
         Set<BlockPos> surrounding = ((AutoFeetPlace)this.module).createSurrounding(blocked, Managers.getEntitiesManager().getEntities());
         List<BlockPos> blocks = new ArrayList(surrounding);
         ((AutoFeetPlace)this.module).onEvent(blocks, event);
      }
   }
}
