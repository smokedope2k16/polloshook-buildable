package me.pollos.polloshook.impl.module.misc.visualrange;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.entity.EntityWorldEvent;
import me.pollos.polloshook.impl.module.misc.visualrange.mode.DirectionMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.player.PlayerEntity;

public class ListenerLeave extends ModuleListener<VisualRange, EntityWorldEvent.Remove> {
   public ListenerLeave(VisualRange module) {
      super(module, EntityWorldEvent.Remove.class);
   }

   public void call(EntityWorldEvent.Remove event) {
      boolean bl = event.getReason() == RemovalReason.UNLOADED_TO_CHUNK || event.getReason() == RemovalReason.CHANGED_DIMENSION || event.getReason() == RemovalReason.UNLOADED_WITH_PLAYER;
      if (!(Boolean)((VisualRange)this.module).onlyChunkLeave.getValue() || bl) {
         if ((Boolean)((VisualRange)this.module).left.getValue()) {
            Entity var4 = event.getEntity();
            if (var4 instanceof PlayerEntity) {
               PlayerEntity player = (PlayerEntity)var4;
               if (!event.getEntity().getName().getString().equals(mc.player.getName().getString())) {
                  ClientLogger.getLogger().log(((VisualRange)this.module).getMessage("%s left your visual range".formatted(new Object[]{player.getName().getString()}), player, DirectionMode.LEAVING), player.getId());
               }
            }
         }

      }
   }
}
