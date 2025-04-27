package me.pollos.polloshook.impl.module.movement.holesnap;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.block.HoleUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;

public class ListenerMotion extends ModuleListener<HoleSnap, TickEvent> {
   public ListenerMotion(HoleSnap module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if (PlayerUtil.isNull()) {
         ((HoleSnap)this.module).setEnabled(false);
      } else {
         if (((HoleSnap)this.module).hole == null || EntityUtil.isSafe(mc.player) || HoleUtil.isTerrainHole(mc.player.getBlockPos())) {
            ((HoleSnap)this.module).toggle();
         }

      }
   }
}
