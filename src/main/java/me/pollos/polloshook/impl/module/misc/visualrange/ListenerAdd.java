package me.pollos.polloshook.impl.module.misc.visualrange;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.entity.EntityWorldEvent;
import me.pollos.polloshook.impl.module.misc.visualrange.mode.DirectionMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class ListenerAdd extends ModuleListener<VisualRange, EntityWorldEvent.Add> {
   public ListenerAdd(VisualRange module) {
      super(module, EntityWorldEvent.Add.class);
   }

   public void call(EntityWorldEvent.Add event) {
      if ((Boolean)((VisualRange)this.module).left.getValue()) {
         Entity var3 = event.getEntity();
         if (var3 instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)var3;
            if (!event.getEntity().getName().getString().equals(mc.player.getName().getString())) {
               ClientLogger.getLogger().log(((VisualRange)this.module).getMessage("%s was spotted".formatted(new Object[]{player.getName().getString()}), player, DirectionMode.ENTERING), player.getId());
            }
         }
      }

   }
}
