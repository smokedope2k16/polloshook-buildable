package me.pollos.polloshook.impl.module.player.reach;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import net.minecraft.world.GameMode;

public class ListenerReach extends ModuleListener<Reach, Reach.ReachEvent> {
   public ListenerReach(Reach module) {
      super(module, Reach.ReachEvent.class);
   }

   public void call(Reach.ReachEvent event) {
      float reach = event.isBlock() ? (Float)((Reach)this.module).blocks.getValue() : (Float)((Reach)this.module).entities.getValue();
      boolean creative = mc.interactionManager.getCurrentGameMode() == GameMode.CREATIVE;
      float fl = creative ? 5.0F : 4.5F;
      float min = Math.min(reach + fl, 6.0F);
      event.setAdd(min - fl);
      event.setCanceled(true);
   }
}
